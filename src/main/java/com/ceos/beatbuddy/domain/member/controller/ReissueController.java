package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.TokenProvider;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshToken;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reissue Controller", description = "Token 만료 시 새로운 Access, Refresh 토큰을 재발급하는 컨트롤러")
public class ReissueController {

    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급",
            description = "Access 토큰이 만료된 경우, Refresh 토큰으로 재발급합니다.\n"
                    + "Access 토큰과 Refresh 토큰을 각각 헤더와 쿠키에 담아 반환합니다.\n"
                    + "Refresh 토큰이 만료됐거나 유효하지 않은 토큰일 경우 에러를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰을 재발급하는데 성공했습니다."
                    , headers = {
                    @Header(name = "access", description = "새로운 Access 토큰입니다"),
            }, extensions = {@Extension(name = "x-cookie", properties = {
                    @ExtensionProperty(name = "refresh", value = "새로운 Refresh 토큰입니다")
            })}
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰입니다"
                    + "에러 메시지가 출력됩니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "유저의 장르 선호도가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
                break;
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            tokenProvider.isExpired(refresh);
        } catch (Exception e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = tokenProvider.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);

        if (!isExist) {
            //response body
            return new ResponseEntity<>("refresh token not found", HttpStatus.NOT_FOUND);
        }

        String username = tokenProvider.getUsername(refresh);
        String role = tokenProvider.getRole(refresh);
        Long memberId = tokenProvider.getMemberId(refresh);

        String newAccess = tokenProvider.createToken("access", memberId, username, role, 1000 * 60 * 60 * 2L);
        String newRefresh = tokenProvider.createToken("refresh", memberId, username, role, 1000 * 3600 * 24 * 14L);

        refreshTokenRepository.deleteByRefreshToken(refresh);
        saveRefreshToken(username, newRefresh);

        response.setHeader("access", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 14);
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");

        return cookie;
    }

    private void saveRefreshToken(String username, String refresh) {

        RefreshToken refreshToken = new RefreshToken(refresh, username);

        refreshTokenRepository.save(refreshToken);
    }
}

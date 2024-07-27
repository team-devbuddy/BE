package com.ceos.beatbuddy.global.config.oauth.controller;

import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.oauth.application.Oauth2Service;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    @GetMapping("/logout")
    @Operation(summary = "로그아웃",
            description = "로그아웃을 진행합니다.\n"
                    + "로그아웃을 진행하면 세션이 종료되고 Refresh 토큰이 만료됩니다.\n"
                    + "로그아웃 후 다시 로그인을 진행해야 합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다. 본문은 로그아웃한 유저의 kakao id입니다."
                    , content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰입니다"
                    + "\n에러 메시지가 출력됩니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<String> kakaoLogout(HttpSession session) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        ResponseEntity<String> result = oauth2Service.logout(memberId);
        return result;
    }

    @PostMapping("/resign")
    @Operation(summary = "회원탈퇴",
            description = "회원탈퇴를 진행합니다.\n"
                    + "회원탈퇴를 진행하면 유저의 대한 장르,지역,분위기들의 선호도와 하트비트,아카이브가 전부 삭제됩니다.\n"
                    + "탈퇴가 완료되면 로그아웃됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴에 성공했습니다. 본문은 회원탈퇴한 유저의 kakao id입니다."
                    , content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰입니다"
                    + "\n에러 메시지가 출력됩니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<String> kakaoResign(HttpSession session) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        ResponseEntity<String> result = oauth2Service.resign(memberId);
        return result;
    }

}

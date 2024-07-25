package com.ceos.beatbuddy.global.config.oauth;

import com.ceos.beatbuddy.global.config.jwt.TokenProvider;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshToken;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshTokenRepository;
import com.ceos.beatbuddy.global.config.oauth.dto.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = oAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        Long memberId = oAuth2User.getMemberId();

        String access = tokenProvider.createToken("access", memberId, username, role, 1000 * 60 * 60 * 2L);
        String refresh = tokenProvider.createToken("refresh", memberId, username, role, 1000 * 3600 * 24 * 14L);

        saveRefreshToken(username, refresh);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .memberId(oAuth2User.getMemberId())
                .loginId(oAuth2User.getUsername())
                .username(oAuth2User.getName())
                .accessToken(access)
                .refreshToken(refresh)
                .build();

        ResponseCookie cookie = ResponseCookie.from("refresh", refresh)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(loginResponseDto);
        response.getWriter().write(jsonResponse);

        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(600);

        log.info("access: " + access);
        String redirectUrl = "http://localhost:3000/login/oauth2/callback/kakao?access=" + access;

        if (!response.isCommitted()) {
            response.sendRedirect(redirectUrl);
        }
    }

    private void saveRefreshToken(String username, String refresh) {

        RefreshToken refreshToken = new RefreshToken(refresh, username);

        refreshTokenRepository.save(refreshToken);
    }
}

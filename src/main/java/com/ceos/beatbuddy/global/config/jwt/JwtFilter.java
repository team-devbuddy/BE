package com.ceos.beatbuddy.global.config.jwt;

import com.ceos.beatbuddy.domain.member.dto.Oauth2MemberDto;
import com.ceos.beatbuddy.global.config.oauth.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                }
            }
        }

        if (authorization == null) {
            System.out.println("token is null");
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorization;

        if (tokenProvider.isExpired(token)) {
            System.out.println("token is expired");
            filterChain.doFilter(request, response);

            return;
        }

        String username = tokenProvider.getUsername(token);
        String role = tokenProvider.getRole(token);

        Oauth2MemberDto memberDto = Oauth2MemberDto.builder()
                .role(role)
                .name(username)
                .build();

        CustomOAuth2User oAuth2User = new CustomOAuth2User(memberDto);

        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null,
                oAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

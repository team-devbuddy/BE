package com.ceos.beatbuddy.global.config.jwt;

import com.ceos.beatbuddy.global.config.oauth.dto.Oauth2MemberDto;
import com.ceos.beatbuddy.global.config.oauth.CustomOAuth2User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

        String accessToken = request.getHeader("access");

        // access token이 없을 경우
        if (accessToken == null || !accessToken.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = accessToken.split(" ")[1];

        try{
            tokenProvider.isExpired(accessToken);
        }
        catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = tokenProvider.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        String username = tokenProvider.getUsername(accessToken);
        String role = tokenProvider.getRole(accessToken);
        Long memberId = tokenProvider.getMemberId(accessToken);

        Oauth2MemberDto memberDto = Oauth2MemberDto.builder()
                .memberId(memberId)
                .role(role)
                .loginId(username)
                .build();

        CustomOAuth2User oAuth2User = new CustomOAuth2User(memberDto);

        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null,
                oAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

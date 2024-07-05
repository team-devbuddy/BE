package com.ceos.beatbuddy.global.config;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

import com.ceos.beatbuddy.global.config.jwt.JwtFilter;
import com.ceos.beatbuddy.global.config.jwt.TokenProvider;
import com.ceos.beatbuddy.global.config.oauth.CustomClientRegistrationRepo;
import com.ceos.beatbuddy.global.config.oauth.OAuth2SuccessHandler;
import io.jsonwebtoken.Jwts;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenProvider tokenProvider;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    private static final String BaseUri = "https://beatbuddy.world/**";
    private static final String FE_LOCAL_URL = "http://localhost:3000";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(CsrfConfigurer::disable)
                .formLogin((auth) -> auth.disable())
                .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Custom filter 추가
                .addFilterAfter(new JwtFilter(tokenProvider), OAuth2LoginAuthenticationFilter.class)
                // 경로에 대한 권한 부여
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/reissue","/swagger-ui/**","/v3/api-docs/**","/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                //oauth2
                .oauth2Login(oath2 -> oath2
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }

    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",
                getDefaultCorsConfiguration());

        return source;
    }

    private CorsConfiguration getDefaultCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(asList(
                FE_LOCAL_URL,
                BaseUri));
        config.setAllowedHeaders(singletonList("*")); //모든 종류의 HTTP 헤더를 허용하도록 설정
        config.setAllowedMethods(singletonList("*")); //모든 종류의 HTTP 메소드를 허용하도록 설정
        config.setAllowCredentials(true); //인증 정보와 관련된 요청을 허용
        config.setMaxAge(3600L);

        config.setExposedHeaders(Collections.singletonList("Set-Cookie"));
        config.setExposedHeaders(Collections.singletonList("Authorization"));

        return config;
    }
}

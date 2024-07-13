package com.ceos.beatbuddy.global.config.jwt;

import com.ceos.beatbuddy.global.config.oauth.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            CustomOAuth2User userDetails = (CustomOAuth2User) authentication.getPrincipal();
            return userDetails.getMemberId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}

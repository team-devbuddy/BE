package com.ceos.beatbuddy.global.config.oauth.controller;

import com.ceos.beatbuddy.global.config.oauth.application.Oauth2Service;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
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
    public ResponseEntity<String> kakaoLogout(HttpSession session) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        ResponseEntity<String> result = oauth2Service.logout(memberId);
        return result;
    }

    @PostMapping("/resign")
    public ResponseEntity<String> kakaoResign(HttpSession session) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        ResponseEntity<String> result = oauth2Service.resign(memberId);
        return result;
    }

}

package com.ceos.beatbuddy.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String kakaoOauthRedirect(String code) {
        return "login";
    }

}

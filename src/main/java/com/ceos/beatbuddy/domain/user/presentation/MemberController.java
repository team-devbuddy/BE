package com.ceos.beatbuddy.domain.user.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

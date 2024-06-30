package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/prefer/{id}")
    public ResponseEntity<Long> addPreference(@PathVariable Long id, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(memberService.addPreference(id, preferences));
    }

}

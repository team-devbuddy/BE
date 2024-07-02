package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/onboarding/consent/{memberId}")
    public ResponseEntity<Long> saveTermConsent(@PathVariable Long memberId, @RequestBody MemberConsentRequestDTO memberConsentRequestDTO) {
        return ResponseEntity.ok(memberService.saveMemberConsent(memberId, memberConsentRequestDTO));
    }

    @PostMapping("/onboarding/nickname/{memberId}")
    public ResponseEntity<MemberResponseDTO> saveNickname(@PathVariable Long memberId, @RequestBody NicknameRequestDTO nicknameRequestDTO) {
        return ResponseEntity.ok(memberService.saveAndCheckNickname(memberId, nicknameRequestDTO));
    }


}

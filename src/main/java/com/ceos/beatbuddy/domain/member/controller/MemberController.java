package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.domain.member.entity.RegionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/onboarding/consent/{memberId}")
    public ResponseEntity<MemberResponseDTO> saveTermConsent(@PathVariable Long memberId, @RequestBody MemberConsentRequestDTO memberConsentRequestDTO) {
        return ResponseEntity.ok(memberService.saveMemberConsent(memberId, memberConsentRequestDTO));
    }

    @PostMapping("/onboarding/nickname/{memberId}")
    public ResponseEntity<MemberResponseDTO> saveNickname(@PathVariable Long memberId, @RequestBody NicknameRequestDTO nicknameRequestDTO) {
        return ResponseEntity.ok(memberService.saveAndCheckNickname(memberId, nicknameRequestDTO));
    }

    @PostMapping("/onboarding/regions/{memberId}")
    public ResponseEntity<MemberResponseDTO> saveRegions(@PathVariable Long memberId, @RequestBody RegionRequestDTO regionRequestDTO) {;
        return ResponseEntity.ok(memberService.saveRegions(memberId, regionRequestDTO));
    }


}

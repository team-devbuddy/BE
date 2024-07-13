package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.application.MemberMoodService;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member-mood")
@RequiredArgsConstructor
@Tag(name = "MemberMood Controller", description = "사용자의 분위기 선호도를 생성, 삭제하는 컨트롤러")
public class MemberMoodController {
    private final MemberMoodService memberMoodService;

    @PostMapping("")
    @Operation(summary = "사용자 분위기 선호도 생성", description = "사용자의 새로운 분위기 선호도를 생성합니다.")
    public ResponseEntity<MemberVectorResponseDTO> addMoodPreference(@RequestBody Map<String, Double> preferences) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.addMoodVector(memberId, preferences));
    }

    @DeleteMapping("/{memberMoodId}")
    @Operation(summary = "사용자 기존 분위기 선호도 삭제", description = "사용자의 기존 분위기 선호도를 삭제합니다")
    public ResponseEntity<MemberVectorResponseDTO> deleteMoodPreference(@PathVariable Long memberMoodId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.deleteMoodVector(memberId, memberMoodId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MemberVectorResponseDTO>> getAllMoodPreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.getAllMoodVector(memberId));
    }

    @GetMapping("/latest")
    public ResponseEntity<MemberVectorResponseDTO> getLatestMoodPreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.getLatestMoodVector(memberId));
    }
}

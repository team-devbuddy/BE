package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.application.MemberMoodService;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
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

    @PostMapping("/{memberId}")
    @Operation(summary = "사용자 분위기 선호도 생성", description = "사용자의 새로운 분위기 선호도를 생성합니다.")
    public ResponseEntity<MemberVectorResponseDTO> addMoodPreference(@PathVariable Long memberId, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(memberMoodService.addMoodVector(memberId, preferences));
    }

    @DeleteMapping("/{memberId}/{memberMoodId}")
    @Operation(summary = "사용자 기존 분위기 선호도 삭제", description = "사용자의 기존 분위기 선호도를 삭제합니다")
    public ResponseEntity<MemberVectorResponseDTO> deleteMoodPreference(@PathVariable Long memberId, @PathVariable Long memberMoodId) {
        return ResponseEntity.ok(memberMoodService.deleteMoodVector(memberId, memberMoodId));
    }

    @GetMapping("/all/{memberId}")
    public ResponseEntity<List<MemberVectorResponseDTO>> getAllMoodPreference(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberMoodService.getAllMoodVector(memberId));
    }

    @GetMapping("/latest/{memberId}")
    public ResponseEntity<MemberVectorResponseDTO> getLatestMoodPreference(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberMoodService.getLatestMoodVector(memberId));
    }
}

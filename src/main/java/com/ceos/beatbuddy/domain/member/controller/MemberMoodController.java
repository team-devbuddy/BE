package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.application.MemberMoodService;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import com.ceos.beatbuddy.domain.vector.dto.MoodRequestDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 분위기 선호도 저장 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberVectorResponseDTO> addMoodPreference(@RequestBody MoodRequestDTO moodRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.addMoodVector(memberId, moodRequestDTO.getMoodPreferences()));
    }

    @DeleteMapping("/{memberMoodId}")
    @Operation(summary = "사용자 기존 분위기 선호도 삭제", description = "사용자의 기존 분위기 선호도를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 분위기 선호도 삭제 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다 or 해당 memberMoodId를 갖는 벡터가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "400", description = "분위기 선호도가 1개밖에 없어서 삭제할 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberVectorResponseDTO> deleteMoodPreference(@PathVariable Long memberMoodId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.deleteMoodVector(memberId, memberMoodId));
    }

    @GetMapping("/all")
    @Operation(summary = "사용자 분위기 선호도 리스트 전체 조회", description = "사용자의 모든 분위기 선호도를 리스트로 전부 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 분위기 선호도 전체 조회에 성공했습니다"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<List<MemberVectorResponseDTO>> getAllMoodPreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.getAllMoodVector(memberId));
    }

    @GetMapping("/latest")
    @Operation(summary = "사용자의 가장 최근 분위기 선호도 1개 조회", description = "사용자의 모든 분위기 선호도 중 가장 최근 것만 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 가장 최근 분위기 선호도 조회에 성공했습니다"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberVectorResponseDTO> getLatestMoodPreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberMoodService.getLatestMoodVector(memberId));
    }
}

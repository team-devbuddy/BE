package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
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
@RequestMapping("/member-genre")
@RequiredArgsConstructor
@Tag(name = "MemberGenre Controller", description = "사용자의 장르 선호도를 생성, 삭제하는 컨트롤러")
public class MemberGenreController {
    private final MemberGenreService memberGenreService;

    @PostMapping("")
    @Operation(summary = "사용자 장르 선호도 생성", description = "사용자의 새로운 장르 선호도를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장르 선호도 생성에 성공하였습니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberVectorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberVectorResponseDTO> addGenrePreference(@RequestBody Map<String, Double> preferences) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberGenreService.addGenreVector(memberId, preferences));
    }

    @DeleteMapping("/{memberGenreId}")
    @Operation(summary = "사용자 장르 선호도 삭제", description = "사용자의 기존 장르 선호도를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장르 선호도 삭제에 성공하였습니다.\n"
                    + "반환값은 삭제한 장르 선호도에 대한 정보입니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "장르 선호도가 1개밖에 없어서 삭제할 수 없습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "유저 장르 선호도가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberVectorResponseDTO> deleteGenrePreference(@PathVariable Long memberGenreId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberGenreService.deleteGenreVector(memberId, memberGenreId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MemberVectorResponseDTO>> getAllGenrePreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberGenreService.getAllGenreVector(memberId));
    }

    @GetMapping("/latest")
    public ResponseEntity<MemberVectorResponseDTO> getLatestGenrePreference() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberGenreService.getLatestGenreVector(memberId));
    }
}

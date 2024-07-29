package com.ceos.beatbuddy.domain.archive.controller;


import com.ceos.beatbuddy.domain.archive.application.ArchiveService;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveRequestDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveResponseDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveUpdateDTO;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
@Tag(name = "Archive Controller", description = "아카이브 컨트롤러\n"
        + "사용자가 아카이브 추가, 삭제, 조회, 수정하는 로직이 있습니다.")
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("")
    @Operation(summary = "사용자의 취향을 아카이브에 저장", description = "장르 벡터 ID와 무드 벡터 ID를 입력하면 아카이브에 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아카이브에 저장 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ArchiveResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다 or 요청한 멤버 장르 벡터가 존재하지 않습니다 or 요청한 멤버 무드 벡터가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 조합의 아카이브입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<ArchiveDTO> addArchive(@RequestBody ArchiveRequestDTO archiveRequestDTO){
        Long memberId = SecurityUtils.getCurrentMemberId();
        Long memberMoodId = archiveRequestDTO.getMemberMoodId();
        Long memberGenreId = archiveRequestDTO.getMemberGenreId();
        return ResponseEntity.ok(archiveService.addPreferenceInArchive(memberId, memberMoodId, memberGenreId));
    }

    @DeleteMapping("/{archiveId}")
    @Operation(summary = "사용자의 취향을 아카이브에서 삭제", description = "해당 아카이브 ID인 아카이브를 삭제한 다음 삭제한 아카이브 정보를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아카이브 삭제 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ArchiveResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 아카이브가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<ArchiveDTO> deleteArchive(@PathVariable Long archiveId){
        return ResponseEntity.ok(archiveService.deletePreferenceInArchive(archiveId));
    }

    @PatchMapping("/{archiveId}")
    @Operation(summary = "아카이브 정보 수정", description = "해당 아카이브 ID인 아카이브에서 관심 지역, 무드, 장르 선호 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아카이브 수정 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ArchiveResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 아카이브가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<ArchiveDTO> updateArchive(@PathVariable Long archiveId, @RequestBody ArchiveUpdateDTO archiveUpdateDTO) {
        return ResponseEntity.ok(archiveService.updatePreferenceInArchive(archiveId, archiveUpdateDTO));
    }

    @GetMapping("/all")
    @Operation(summary = "사용자의 모든 아카이브 전체 조회", description = "사용자가 보유 중인 모든 아카이브 리스트로 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아카이브 리스트 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = ArchiveResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "요청한 아카이브가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<List<ArchiveResponseDTO>> getArchives(){
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(archiveService.getArchives(memberId));
    }

    @GetMapping("/history/{archiveId}")
    @Operation(summary = "아카이브 클릭 시 추천 히스토리 조회", description = "아카이브를 클릭하면 해당 취향을 기반으로 추천했던 히스토리를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아카이브 추천 히스토리 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = ArchiveResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "요청한 아카이브가 존재하지 않습니다 or 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<List<VenueResponseDTO>> getHistory(@PathVariable Long archiveId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(archiveService.getHistory(memberId, archiveId));
    }
}

package com.ceos.beatbuddy.domain.venue.controller;

import com.ceos.beatbuddy.domain.venue.application.VenueInfoService;
import com.ceos.beatbuddy.domain.venue.dto.VenueInfoResponseDTO;
import com.ceos.beatbuddy.domain.venue.dto.VenueRequestDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "VenueInfo Controller", description = "베뉴에 대한 정보를 제공하는 컨트롤러")
@RequestMapping("/venue-info")
public class VenueInfoController {
    private final VenueInfoService venueInfoService;

    @GetMapping
//    @Operation(summary = "존재하는 모든 베뉴의 리스트 조회", description = "모든 베뉴의 목록을 조회합니다")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "사용자 성인인증 여부 확인 성공",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = Boolean.class))),
//            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResponseTemplate.class)))
//    })
    public ResponseEntity<List<Venue>> getAllVenueInfo() {
        return ResponseEntity.ok(venueInfoService.getVenueInfoList());
    }

    @GetMapping("/{venueId}")
    @Operation(summary = "베뉴 상세정보 조회", description = "베뉴에 대한 상세페이지 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "베뉴 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VenueInfoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 베뉴가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<VenueInfoResponseDTO> getVenueInfo(@PathVariable Long venueId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(venueInfoService.getVenueInfo(venueId, memberId));
    }

    @PostMapping
    public ResponseEntity<Long> PostVenueInfo(@RequestBody VenueRequestDTO venueRequestDTO,
                                              @Parameter(description = "로고 이미지", required = false,
                                                      content = @Content(mediaType = "multipart/form-data"))
                                              @RequestParam(value = "file", required = false) MultipartFile logoImage,
                                              @Parameter(description = "배경 이미지, 비디오 파일", required = false,
                                                      content = @Content(mediaType = "multipart/form-data"))
                                                  @RequestParam(value = "file", required = false) List<MultipartFile> backgroundImage)
            throws IOException {
        return ResponseEntity.ok(venueInfoService.addVenueInfo(venueRequestDTO,logoImage,backgroundImage).getVenueId());
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Long> DeleteVenueInfo(@PathVariable Long venueId) {
        return ResponseEntity.ok(venueInfoService.deleteVenueInfo(venueId));
    }
}

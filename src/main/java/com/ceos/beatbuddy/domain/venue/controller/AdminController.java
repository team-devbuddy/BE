package com.ceos.beatbuddy.domain.venue.controller;

import com.ceos.beatbuddy.domain.venue.application.VenueInfoService;
import com.ceos.beatbuddy.domain.venue.dto.VenueRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private VenueInfoService venueInfoService;

    @PostMapping
    @Operation(summary = "베뉴 정보 등록", description = "베뉴 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "베뉴 정보 등록 성공"),
            @ApiResponse(responseCode = "400", description = "틀린 이미지 형식"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패")
    })
    public ResponseEntity<Long> PostVenueInfo(@RequestBody VenueRequestDTO venueRequestDTO,
                                              @Parameter(description = "로고 이미지", required = false,
                                                      content = @Content(mediaType = "multipart/form-data"))
                                              @RequestParam(value = "file", required = false) MultipartFile logoImage,
                                              @Parameter(description = "배경 이미지, 비디오 파일", required = false,
                                                      content = @Content(mediaType = "multipart/form-data"))
                                              @RequestParam(value = "file", required = false) List<MultipartFile> backgroundImage)
            throws IOException {
        return ResponseEntity.ok(
                venueInfoService.addVenueInfo(venueRequestDTO, logoImage, backgroundImage).getVenueId());
    }

    @DeleteMapping("/{venueId}")
    @Operation(summary = "베뉴 정보 삭제", description = "베뉴 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "베뉴 정보 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "베뉴 정보가 존재하지 않음")
    })
    public ResponseEntity<Long> DeleteVenueInfo(@PathVariable Long venueId) {
        return ResponseEntity.ok(venueInfoService.deleteVenueInfo(venueId));
    }

    @PutMapping("/{venueId}")
    @Operation(summary = "베뉴 정보 수정", description = "베뉴 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "베뉴 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "틀린 이미지 형식"),
            @ApiResponse(responseCode = "404", description = "베뉴 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패")
    })
    public ResponseEntity<Long> updateVenueInfo(@PathVariable Long venueId,
                                                @RequestBody VenueRequestDTO venueRequestDTO,
                                                @Parameter(description = "로고 이미지", required = false,
                                                        content = @Content(mediaType = "multipart/form-data"))
                                                @RequestParam(value = "file", required = false) MultipartFile logoImage,
                                                @Parameter(description = "배경 이미지, 비디오 파일", required = false,
                                                        content = @Content(mediaType = "multipart/form-data"))
                                                @RequestParam(value = "file", required = false) List<MultipartFile> backgroundImage)
            throws IOException {
        return ResponseEntity.ok(
                venueInfoService.updateVenueInfo(venueId, venueRequestDTO, logoImage, backgroundImage).getVenueId());
    }
}

package com.ceos.beatbuddy.domain.venue.controller;

import com.ceos.beatbuddy.domain.venue.application.VenueInfoService;
import com.ceos.beatbuddy.domain.venue.dto.VenueRequestDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/venueInfo")
public class VenueInfoController {
    private final VenueInfoService venueInfoService;

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenueInfo() {
        return ResponseEntity.ok(venueInfoService.getVenueInfoList());
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> getVenueInfo(@PathVariable Long venueId) {
        return ResponseEntity.ok(venueInfoService.getVenueInfo(venueId));
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

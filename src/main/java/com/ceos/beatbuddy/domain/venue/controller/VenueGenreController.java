package com.ceos.beatbuddy.domain.venue.controller;

import com.ceos.beatbuddy.domain.venue.application.VenueGenreService;
import com.ceos.beatbuddy.domain.venue.dto.VenueVectorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/venue-genre")
@RequiredArgsConstructor
@Tag(name = "VenueGenre Controller", description = "베뉴에 대한 장르를 추가, 수정하는 컨트롤러\n"
        + "현재는 베뉴 업자에 대한 페이지가 없으므로 아직 구현할 것이 없습니다\n"
        + "(잘못 알고 있다면 알려주세요)")
public class VenueGenreController {
    private final VenueGenreService venueGenreService;

    @PostMapping("/{venueId}")
    @Operation(summary = "베뉴 장르 선호도 추가",
            description = "베뉴의 장르 선호도를 추가합니다")

    public ResponseEntity<VenueVectorResponseDTO> addGenrePreference(@PathVariable Long venueId,
                                                                     @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(venueGenreService.addGenreVector(venueId, preferences));
    }

    @PatchMapping("/{venueId}")
    @Operation(summary = "베뉴 장르 선호도 업데이트",
            description = "베뉴 장르 선호도를 업데이트 합니다.\n"
                    + "일반적으로 베뉴 장르는 크게 변하지 않으며 장르가 없을 수 없으므로\n"
                    + "DeleteMapping은 구현하지 않았습니다.")
    public ResponseEntity<VenueVectorResponseDTO> updateGenrePreference(@PathVariable Long venueId,
                                                                        @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(venueGenreService.updateGenreVector(venueId, preferences));
    }
}

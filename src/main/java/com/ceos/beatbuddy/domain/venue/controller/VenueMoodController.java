package com.ceos.beatbuddy.domain.venue.controller;


import com.ceos.beatbuddy.domain.vector.dto.MoodRequestDTO;
import com.ceos.beatbuddy.domain.venue.application.VenueMoodService;
import com.ceos.beatbuddy.domain.venue.dto.VenueVectorResponseDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/venue-mood")
@RequiredArgsConstructor
@Tag(name = "VenueMood Controller", description = "베뉴의 분위기를 생성, 수정하는 컨트롤러\n"
        + "현재는 베뉴 업자에 대한 페이지가 없으므로 구현할 것이 없습니다\n"
        + "(잘못 알고 있다면 알려주세요)")
public class VenueMoodController {

    private final VenueMoodService venueMoodService;

    @PostMapping("/{venueId}")
    @Operation(summary = "베뉴 분위기 선호도 추가",
            description = "베뉴의 분위기 선호도를 추가합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "베뉴의 분위기 데이터 업데이트 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = VenueVectorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 베뉴는 존재하지 않습니다"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<VenueVectorResponseDTO> addGenrePreference(@PathVariable Long venueId, @RequestBody MoodRequestDTO moodRequestDTO) {
        return ResponseEntity.ok(venueMoodService.addMoodVector(venueId, moodRequestDTO.getMoodPreferences()));
    }

    @PatchMapping("/{venueId}")
    @Operation(summary = "베뉴 분위기 선호도 업데이트",
            description = "베뉴 분위기 선호도를 업데이트 합니다.\n"
                    + "일반적으로 베뉴 분위기는 크게 변하지 않으며 분위기가 없을 수 없으므로\n"
                    + "DeleteMapping은 구현하지 않았습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "베뉴의 분위기 데이터 업데이트 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = VenueVectorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 베뉴는 존재하지 않습니다"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "400", description = "해당 베뉴의 분위기 데이터가 존재하지 않습니다"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<VenueVectorResponseDTO> updateGenrePreference(@PathVariable Long venueId, @RequestBody MoodRequestDTO moodRequestDTO) {
        return ResponseEntity.ok(venueMoodService.updateMoodVector(venueId, moodRequestDTO.getMoodPreferences()));
    }
}

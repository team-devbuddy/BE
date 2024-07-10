package com.ceos.beatbuddy.domain.heartbeat.controller;


import com.ceos.beatbuddy.domain.heartbeat.application.HeartbeatService;
import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
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

import java.util.List;

@RestController
@RequestMapping("/heartbeat")
@RequiredArgsConstructor
@Tag(name = "Heartbeat Controller", description = "하트비트 컨트롤러\n"
        + "사용자가 하트비트 추가, 삭제, 조회하는 로직이 있습니다.")
public class HeartbeatController {
    private final HeartbeatService heartbeatService;

    @PostMapping("/{memberId}/{venueId}")
    @Operation(summary = "하트비트 추가", description = "사용자가 베뉴에 하트비트 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하트비트 추가 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = HeartbeatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "요청한 베뉴가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "409", description = "사용자가 이미 해당 베뉴에 하트비트를 추가해놓은 상태입니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<HeartbeatResponseDTO> addHeartbeat(@PathVariable Long memberId, @PathVariable Long venueId) {
        return ResponseEntity.ok(heartbeatService.addHeartbeat(memberId, venueId));
    }

    @DeleteMapping("/{memberId}/{venueId}")
    @Operation(summary = "하트비트 삭제", description = "사용자가 베뉴에 하트비트 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하트비트 삭제 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = HeartbeatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "요청한 베뉴가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<HeartbeatResponseDTO> deleteHeartbeat(@PathVariable Long memberId, @PathVariable Long venueId) {
        return ResponseEntity.ok(heartbeatService.deleteHeartbeat(memberId, venueId));
    }

    @GetMapping("/{memberId}/all")
    public ResponseEntity<List<HeartbeatResponseDTO>> getAllHeartbeat(@PathVariable Long memberId) {
        return ResponseEntity.ok(heartbeatService.getAllHeartbeat(memberId));
    }


    @GetMapping("/{memberId}/{venueId}")
    public ResponseEntity<HeartbeatResponseDTO> getHeartbeat(@PathVariable Long memberId, @PathVariable Long venueId) {
        return ResponseEntity.ok(heartbeatService.getHeartbeat(memberId, venueId));
    }

    @GetMapping("/hot-chart")
    public ResponseEntity<List<VenueResponseDTO>> getHotChart() {
        return ResponseEntity.ok(heartbeatService.getHotChart());
    }

}

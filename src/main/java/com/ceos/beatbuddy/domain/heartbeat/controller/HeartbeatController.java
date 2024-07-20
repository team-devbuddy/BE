package com.ceos.beatbuddy.domain.heartbeat.controller;


import com.ceos.beatbuddy.domain.heartbeat.application.HeartbeatService;
import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
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
@RequestMapping("/heartbeat")
@RequiredArgsConstructor
@Tag(name = "Heartbeat Controller", description = "하트비트 컨트롤러\n"
        + "사용자가 하트비트 추가, 삭제, 조회하는 로직이 있습니다.")
public class HeartbeatController {
    private final HeartbeatService heartbeatService;

    @PostMapping("/{venueId}")
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
    public ResponseEntity<HeartbeatResponseDTO> addHeartbeat(@PathVariable Long venueId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(heartbeatService.addHeartbeat(memberId, venueId));
    }

    @DeleteMapping("/{venueId}")
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
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "유저가 해당 베뉴에 하트비트를 추가하지 않은 상태입니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<HeartbeatResponseDTO> deleteHeartbeat(@PathVariable Long venueId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(heartbeatService.deleteHeartbeat(memberId, venueId));
    }

    @GetMapping("/all")
    @Operation(summary = "사용자의 전체 하트비트 리스트 조회", description = "사용자가 하트비트 누른 전체 베뉴 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하트비트 전체 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = HeartbeatResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<List<HeartbeatResponseDTO>> getAllHeartbeat() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(heartbeatService.getAllHeartbeat(memberId));
    }


    @GetMapping("/{venueId}")
    @Operation(summary = "사용자가 하트비트를 누른 베뉴에 대한 조회", description = "사용자가 하트비트를 누른 베뉴에 대한 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하트비트 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = HeartbeatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "요청한 베뉴가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "404", description = "유저가 해당 베뉴에 하트비트를 추가하지 않은 상태입니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<HeartbeatResponseDTO> getHeartbeat(@PathVariable Long venueId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(heartbeatService.getHeartbeat(memberId, venueId));
    }

    @GetMapping("/hot-chart")
    @Operation(summary = "HOT chart", description = "하트비트 개수 TOP 10 베뉴 내림차순 정렬")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HOT chart 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = VenueResponseDTO.class))))
    })
    public ResponseEntity<List<VenueResponseDTO>> getHotChart() {
        return ResponseEntity.ok(heartbeatService.getHotChart());
    }

}

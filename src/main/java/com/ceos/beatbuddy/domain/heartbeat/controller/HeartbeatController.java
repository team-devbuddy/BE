package com.ceos.beatbuddy.domain.heartbeat.controller;


import com.ceos.beatbuddy.domain.heartbeat.application.HeartbeatService;
import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heartbeat")
@RequiredArgsConstructor
public class HeartbeatController {
    private final HeartbeatService heartbeatService;

    @PostMapping("/{memberId}/{venueId}")
    public ResponseEntity<HeartbeatResponseDTO> addHeartbeat(@PathVariable Long memberId, @PathVariable Long venueId) {
        return ResponseEntity.ok(heartbeatService.addHeartbeat(memberId, venueId));
    }

    @DeleteMapping("/{memberId}/{venueId}")
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


}

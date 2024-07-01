package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member-genre")
@RequiredArgsConstructor
public class MemberGenreController {
    private final MemberGenreService memberGenreService;

    @PostMapping("/{id}")
    public ResponseEntity<Long> addPreference(@PathVariable Long id, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(memberGenreService.addGenreVector(id, preferences));
    }
}

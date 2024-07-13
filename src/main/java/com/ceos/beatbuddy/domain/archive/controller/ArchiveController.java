package com.ceos.beatbuddy.domain.archive.controller;


import com.ceos.beatbuddy.domain.archive.application.ArchiveService;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveRequestDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveResponseDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveUpdateDTO;
import com.ceos.beatbuddy.domain.archive.entity.Archive;
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
    public ResponseEntity<ArchiveResponseDTO> addArchive(@RequestBody ArchiveRequestDTO archiveRequestDTO){
        Long memberId = archiveRequestDTO.getMemberId();
        Long memberMoodId = archiveRequestDTO.getMemberMoodId();
        Long memberGenreId = archiveRequestDTO.getMemberGenreId();
        return ResponseEntity.ok(archiveService.addPreferenceInArchive(memberId, memberMoodId, memberGenreId));
    }

    @DeleteMapping("/{archiveId}")
    public ResponseEntity<ArchiveResponseDTO> deleteArchive(@PathVariable Long archiveId){
        return ResponseEntity.ok(archiveService.deletePreferenceInArchive(archiveId));
    }

    @PatchMapping("/{archiveId}")
    public ResponseEntity<ArchiveResponseDTO> updateArchive(@PathVariable Long archiveId, @RequestBody ArchiveUpdateDTO archiveUpdateDTO) {
        return ResponseEntity.ok(archiveService.updatePreferenceInArchive(archiveId, archiveUpdateDTO));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<ArchiveResponseDTO>> getArchives(@PathVariable Long memberId){
        return ResponseEntity.ok(archiveService.getArchives(memberId));
    }

}

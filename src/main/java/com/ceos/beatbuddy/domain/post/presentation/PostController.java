package com.ceos.beatbuddy.domain.post.presentation;

import com.ceos.beatbuddy.domain.post.application.PostService;
import com.ceos.beatbuddy.domain.post.dto.FreePostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.PiecePostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.ResponsePostDto;
import com.ceos.beatbuddy.domain.post.entity.FreePost;
import com.ceos.beatbuddy.domain.post.entity.PiecePost;
import com.ceos.beatbuddy.domain.post.entity.Post;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/free")
    @Operation(summary = "게시물 생성", description = "자유게시물을 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 생성 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 생성 실패")
    })
    public ResponseEntity<FreePost> addFreePost(@RequestBody FreePostRequestDto requestDto) {
        return ResponseEntity.ok(postService.addFreePost(requestDto));
    }

    @GetMapping("/free/{postId}")
    @Operation(summary = "게시물 조회", description = "자유게시판을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물가 존재하지 않습니다.")
    })
    public ResponseEntity<FreePost> readFreePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.readFreePost(postId));
    }

    @GetMapping("/free")
    @Operation(summary = "전체 게시물 조회", description = "자유게시판의 전체 게시물을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Page<ResponsePostDto>> readAllPost(
            @PathVariable(value = "0") int page, @PathVariable(value = "10") int size) {
        return ResponseEntity.ok(postService.readAllPost(page, size));
    }

    @DeleteMapping("/free/{postId}")
    @Operation(summary = "게시물 삭제", description = "자유게시물을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postService.deleteFreePost(postId, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/piece")
    @Operation(summary = "게시물 생성", description = "조각모집 게시물을 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 생성 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 생성 실패")
    })
    public ResponseEntity<PiecePost> addPiecePost(@RequestBody PiecePostRequestDto requestDto) {
        return ResponseEntity.ok(postService.addPiecePost(requestDto));
    }

    @GetMapping("/piece/{postId}")
    @Operation(summary = "게시물 조회", description = "조각모집 게시물을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Post> readPiecePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.readPiecePost(postId));
    }

    @GetMapping("/piece")
    @Operation(summary = "전체 게시물 조회", description = "조각모집 게시판의 전체 게시물을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Page<ResponsePostDto>> readAllPiecePost(
            @PathVariable(value = "0") int page, @PathVariable(value = "10") int size) {
        return ResponseEntity.ok(postService.readAllPiecePost(page, size));
    }

    @DeleteMapping("/piece/{postId}")
    @Operation(summary = "게시물 삭제", description = "조각모집 게시물을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Void> deletePiecePost(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postService.deletePiecePost(postId, memberId);
        return ResponseEntity.ok().build();
    }
}

package com.ceos.beatbuddy.domain.post.presentation;

import com.ceos.beatbuddy.domain.post.dto.PostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.ResponsePostDto;
import com.ceos.beatbuddy.domain.post.entity.Post;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ceos.beatbuddy.domain.post.application.PostService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "게시물 컨트롤러\n"
        + "사용자가 전반적인 게시물들을 추가, 조회, 삭제하는 로직이 있습니다.")
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/{type}")
    @Operation(summary = "게시물 생성", description = "게시물을 생성합니다 (type: free/piece)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 생성 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 생성 실패")
    })
    public ResponseEntity<Post> addPost(
            @PathVariable String type,
            @RequestBody PostRequestDto requestDto) {
        return ResponseEntity.ok(postService.addPost(type, requestDto));
    }

    @GetMapping("/{type}/{postId}")
    @Operation(summary = "게시물 조회", description = "게시물을 조회합니다 (type: free/piece)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Post> readPost(
            @PathVariable String type,
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.readPost(type, postId));
    }

    @GetMapping("/{type}")
    @Operation(summary = "전체 게시물 조회", description = "전체 게시물을 조회합니다 (type: free/piece)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 게시물 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Page<ResponsePostDto>> readAllPosts(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.readAllPosts(type, page, size));
    }

    @DeleteMapping("/{type}/{postId}")
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다 (type: free/piece)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시물이 존재하지 않습니다.")
    })
    public ResponseEntity<Void> deletePost(
            @PathVariable String type,
            @PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postService.deletePost(type, postId, memberId);
        return ResponseEntity.ok().build();
    }
}
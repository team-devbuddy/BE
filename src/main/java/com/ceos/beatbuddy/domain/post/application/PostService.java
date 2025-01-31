package com.ceos.beatbuddy.domain.post.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.post.dto.PostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.PostRequestDto.PiecePostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.ResponsePostDto;
import com.ceos.beatbuddy.domain.post.entity.FreePost;
import com.ceos.beatbuddy.domain.post.entity.Piece;
import com.ceos.beatbuddy.domain.post.entity.PiecePost;
import com.ceos.beatbuddy.domain.post.entity.Post;
import com.ceos.beatbuddy.domain.post.exception.PostErrorCode;
import com.ceos.beatbuddy.domain.post.repository.FreePostRepository;
import com.ceos.beatbuddy.domain.post.repository.PiecePostRepository;
import com.ceos.beatbuddy.domain.post.repository.PieceRepository;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import com.ceos.beatbuddy.global.UploadUtil;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final FreePostRepository freePostRepository;
    private final PiecePostRepository piecePostRepository;
    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;
    private final PieceRepository pieceRepository;

    @Transactional
    public Post addPost(String type, PostRequestDto requestDto) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(PostErrorCode.MEMBER_NOT_EXIST));

        List<String> imageUrls = uploadImages(requestDto.images());

        return switch (type) {
            case "free" -> createFreePost(member, requestDto, imageUrls);
            case "piece" -> createPiecePost(member, requestDto, imageUrls);
            default -> throw new CustomException(PostErrorCode.INVALID_POST_TYPE);
        };
    }

    public Post readPost(String type, Long postId) {
        return switch (type) {
            case "free" -> freePostRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));
            case "piece" -> piecePostRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));
            default -> throw new CustomException(PostErrorCode.INVALID_POST_TYPE);
        };
    }

    public Page<ResponsePostDto> readAllPosts(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return switch (type) {
            case "free" -> freePostRepository.findAll(pageable).map(ResponsePostDto::of);
            case "piece" -> piecePostRepository.findAll(pageable).map(ResponsePostDto::of);
            default -> throw new CustomException(PostErrorCode.INVALID_POST_TYPE);
        };
    }

    @Transactional
    public void deletePost(String type, Long postId, Long memberId) {
        Post post = readPost(type, postId);

        if (!post.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(PostErrorCode.MEMBER_NOT_MATCH);
        }

        switch (type) {
            case "free" -> freePostRepository.delete((FreePost) post);
            case "piece" -> {
                PiecePost piecePost = (PiecePost) post;
                pieceRepository.delete(piecePost.getPiece());
                piecePostRepository.delete(piecePost);
            }
            default -> throw new CustomException(PostErrorCode.INVALID_POST_TYPE);
        }
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        return images.stream()
                .map(image -> {
                    try {
                        return UploadUtil.upload(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private FreePost createFreePost(Member member, PostRequestDto requestDto, List<String> imageUrls) {
        FreePost post = FreePost.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .imageUrls(imageUrls)
                .member(member)
                .anonymous(requestDto.anonymous())
                .build();
        return freePostRepository.save(post);
    }

    private PiecePost createPiecePost(Member member, PostRequestDto requestDto, List<String> imageUrls) {
        Venue venue = venueRepository.findById(requestDto.venueId())
                .orElseThrow(() -> new CustomException(PostErrorCode.VENUE_NOT_EXIST));
        PiecePostRequestDto request = (PiecePostRequestDto) requestDto;
        Piece piece = Piece.builder()
                .member(member)
                .venue(venue)
                .eventDate(request.eventDate())
                .totalPrice(request.totalPrice())
                .totalMembers(request.totalMembers())
                .build();
        pieceRepository.save(piece);

        PiecePost post = PiecePost.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .imageUrls(imageUrls)
                .member(member)
                .anonymous(requestDto.anonymous())
                .piece(piece)
                .build();
        return piecePostRepository.save(post);
    }

    public Post findPostByIdWithDiscriminator(Long postId) {
        // 먼저 자유 게시글 확인
        Optional<FreePost> freePost = freePostRepository.findById(postId);
        if (freePost.isPresent()) {
            return freePost.get();
        }

        // 없다면 조각모집 게시글 확인
        Optional<PiecePost> piecePost = piecePostRepository.findById(postId);
        if (piecePost.isPresent()) {
            return piecePost.get();
        }

        throw new CustomException(PostErrorCode.POST_NOT_EXIST);
    }
}
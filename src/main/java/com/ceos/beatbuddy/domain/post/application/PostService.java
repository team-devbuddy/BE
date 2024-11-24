package com.ceos.beatbuddy.domain.post.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.post.entity.Piece;
import com.ceos.beatbuddy.domain.post.dto.FreePostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.PiecePostRequestDto;
import com.ceos.beatbuddy.domain.post.dto.ResponsePostDto;
import com.ceos.beatbuddy.domain.post.entity.FreePost;
import com.ceos.beatbuddy.domain.post.entity.PiecePost;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public FreePost addFreePost(FreePostRequestDto requestDto) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        List<String> imageUrls = requestDto.getImages().stream()
                .map(image -> {
                    try {
                        return UploadUtil.upload(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(PostErrorCode.MEMBER_NOT_EXIST));
        FreePost post = FreePost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrls(imageUrls)
                .member(member)
                .build();

        return freePostRepository.save(post);
    }

    public FreePost readFreePost(Long postId) {
        return freePostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));
    }

    public Page<ResponsePostDto> readAllPost(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return freePostRepository.findAll(pageable).map(post -> ResponsePostDto.of(post));
    }

    @Transactional
    public void deleteFreePost(Long postId, Long memberId) {
        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));

        if (post.getMember().getMemberId().equals(memberId)) {
            freePostRepository.delete(post);
        } else {
            throw new CustomException(PostErrorCode.MEMBER_NOT_MATCH);
        }
    }

    @Transactional
    public PiecePost addPiecePost(PiecePostRequestDto requestDto) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        List<String> imageUrls = requestDto.getImages().stream()
                .map(image -> {
                    try {
                        return UploadUtil.upload(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(PostErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new CustomException(PostErrorCode.VENUE_NOT_EXIST));
        Piece piece = Piece.builder()
                .member(member)
                .venue(venue)
                .eventDate(requestDto.getEventDate())
                .totalPrice(requestDto.getTotalPrice())
                .totalMembers(requestDto.getTotalMembers())
                .build();

        pieceRepository.save(piece);

        PiecePost post = PiecePost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrls(imageUrls)
                .member(member)
                .piece(piece)
                .build();

        return piecePostRepository.save(post);
    }

    public PiecePost readPiecePost(Long postId) {
        return piecePostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));
    }

    public Page<ResponsePostDto> readAllPiecePost(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return piecePostRepository.findAll(pageable).map(post -> ResponsePostDto.of(post));
    }

    @Transactional
    public void deletePiecePost(Long postId, Long memberId) {
        PiecePost post = piecePostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_EXIST));

        if (post.getMember().getMemberId().equals(memberId)) {
            pieceRepository.delete(post.getPiece());
            piecePostRepository.delete(post);
        } else {
            throw new CustomException(PostErrorCode.MEMBER_NOT_MATCH);
        }
    }
}
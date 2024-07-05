package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.*;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberGenreService {
    private final MemberRepository memberRepository;
    private final MemberGenreRepository memberGenreRepository;

    @Transactional
    public MemberVectorResponseDTO addGenreVector(Long memberId, Map<String, Double> genres) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        Vector preferenceVector = Vector.fromGenres(genres);

        MemberGenre memberGenre = MemberGenre.builder()
                .member(member).genreVectorString(preferenceVector.toString())
                .build();

        memberGenreRepository.save(memberGenre);
        return MemberVectorResponseDTO.builder()
                .vectorString(memberGenre.getGenreVectorString())
                .memberId(member.getMemberId())
                .vectorId(memberGenre.getMemberGenreId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .build();
    }


    @Transactional
    public MemberVectorResponseDTO deleteGenreVector(Long memberId, Long memberGenreId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberGenre memberGenre = memberGenreRepository.findById(memberGenreId).orElseThrow(()->new CustomException((MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST)));
        List<MemberGenre> memberGenres = memberGenreRepository.findAllByMember(member);

        if (memberGenres.size() <= 1) {
            throw new CustomException(MemberGenreErrorCode.MEMBER_GENRE_ONLY_ONE);
        }

        memberGenreRepository.delete(memberGenre);

        return MemberVectorResponseDTO.builder()
                .vectorString(memberGenre.getGenreVectorString())
                .memberId(member.getMemberId())
                .vectorId(memberGenre.getMemberGenreId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .build();
    }

    public List<MemberVectorResponseDTO> getAllGenreVector(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<MemberGenre> memberGenres = memberGenreRepository.findAllByMember(member);
        return memberGenres.stream()
                .map(memberGenre -> MemberVectorResponseDTO.builder()
                        .memberId(member.getMemberId())
                        .vectorId(memberGenre.getMemberGenreId())
                        .loginId(member.getLoginId())
                        .nickname(member.getNickname())
                        .realName(member.getRealName())
                        .vectorString(memberGenre.getGenreVectorString())
                        .build())
                .collect(Collectors.toList());
    }

    public MemberVectorResponseDTO getLatestGenreVector(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberGenre memberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(()-> new CustomException((MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST)));
        return MemberVectorResponseDTO.builder()
                .vectorString(memberGenre.getGenreVectorString())
                .memberId(member.getMemberId())
                .vectorId(memberGenre.getMemberGenreId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .build();
    }

}

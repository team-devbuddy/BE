package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberException;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberGenreService {
    private final MemberRepository memberRepository;
    private final MemberGenreRepository memberGenreRepository;

    @Transactional
    public Long addGenreVector(Long id, Map<String, Double> preferences) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));

        Vector preferenceVector = Vector.fromPreferences(preferences);

        MemberGenre memberGenre = MemberGenre.builder()
                .member(member).genreVectorString(preferenceVector.toString())
                .build();

        return memberGenreRepository.save(memberGenre).getMemberGenreId();
    }
}

package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberException;
import com.ceos.beatbuddy.domain.member.exception.MemberMoodErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberMoodException;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberMoodService {
    private final MemberRepository memberRepository;
    private final MemberMoodRepository memberMoodRepository;

    @Transactional
    public MemberVectorResponseDTO addMoodVector(Long memberId, Map<String, Double> moods) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));

        Vector preferenceVector = Vector.fromMoods(moods);

        MemberMood memberMood = MemberMood.builder()
                .member(member).moodVectorString(preferenceVector.toString())
                .build();

        memberMoodRepository.save(memberMood);
        return MemberVectorResponseDTO.builder()
                .vectorString(memberMood.getMoodVectorString())
                .memberId(member.getMemberId())
                .vectorId(memberMood.getMemberMoodId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .build();
    }

    @Transactional
    public MemberVectorResponseDTO deleteMoodVector(Long memberId, Long memberMoodId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood memberMood = memberMoodRepository.findById(memberMoodId).orElseThrow(()->new MemberMoodException((MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST)));
        List<MemberMood> memberMoods = memberMoodRepository.findAllByMember(member);

        if (memberMoods.size() <= 1) {
            throw new MemberMoodException(MemberMoodErrorCode.MEMBER_MOOD_ONLY_ONE);
        }

        memberMoodRepository.delete(memberMood);

        return MemberVectorResponseDTO.builder()
                .vectorString(memberMood.getMoodVectorString())
                .memberId(member.getMemberId())
                .vectorId(memberMood.getMemberMoodId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .build();
    }


}

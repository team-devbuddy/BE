package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.*;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueMoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {
    private final MemberRepository memberRepository;
    private final MemberGenreRepository  memberGenreRepository;
    private final MemberMoodRepository memberMoodRepository;
    private final VenueGenreRepository venueGenreRepository;
    private final VenueMoodRepository venueMoodRepository;

    public List<VenueResponseDTO> recommendVenuesByGenre(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberGenre latestMemberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(() -> new MemberGenreException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));;
        List<VenueGenre> allVenueGenres = venueGenreRepository.findAllWithVenue();

        List<VenueGenre> recommendVenueGenres =  allVenueGenres.stream()
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -latestMemberGenre.getGenreVector().cosineSimilarity(v.getGenreVector());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Double.MIN_VALUE;
                    }
                }))
                .limit(num)
                .collect(Collectors.toList());

        List<VenueResponseDTO> recommendedVenues = recommendVenueGenres.stream()
                .map(vg -> new VenueResponseDTO(
                        vg.getVenue().getVenueId(),
                        vg.getVenue().getEnglishName(),
                        vg.getVenue().getKoreanName()))
                .collect(Collectors.toList());

        return recommendedVenues;

    }


    public List<VenueResponseDTO> recommendVenuesByMood(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood latestMemberMood = memberMoodRepository.findLatestMoodByMember(member).orElseThrow(() -> new MemberMoodException(MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST));;
        List<VenueMood> allVenueMoods = venueMoodRepository.findAllWithVenue();

        List<VenueMood> recommendVenueGenres =  allVenueMoods.stream()
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -latestMemberMood.getMoodVector().cosineSimilarity(v.getMoodVector());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Double.MIN_VALUE;
                    }
                }))
                .limit(num)
                .collect(Collectors.toList());

        List<VenueResponseDTO> recommendedVenues = recommendVenueGenres.stream()
                .map(vm -> new VenueResponseDTO(
                        vm.getVenue().getVenueId(),
                        vm.getVenue().getEnglishName(),
                        vm.getVenue().getKoreanName()))
                .collect(Collectors.toList());

        return recommendedVenues;

    }
}

package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberException;
import com.ceos.beatbuddy.domain.member.exception.MemberGenreErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberGenreException;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
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
    private final VenueRepository venueRepository;
    private final MemberGenreRepository  memberGenreRepository;
    private final VenueGenreRepository venueGenreRepository;

    public List<VenueResponseDTO> recommendVenuesByGenre(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberGenre latestMemberGenre = memberGenreRepository.findLatestByMember(member).orElseThrow(() -> new MemberGenreException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));;
        List<VenueGenre> allVenueGenres = venueGenreRepository.findAllWithVenue();

        List<VenueGenre> recommendVenueGenres =  allVenueGenres.stream()
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -latestMemberGenre.getGenreVector().cosineSimilarity(v.getPreferenceVector());
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


}

package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.*;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueGenreErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueMoodErrorCode;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueMoodRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {
    private final MemberRepository memberRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final MemberMoodRepository memberMoodRepository;
    private final VenueGenreRepository venueGenreRepository;
    private final VenueMoodRepository venueMoodRepository;
    private final VenueRepository venueRepository;

    public List<VenueResponseDTO> recommendVenuesByGenre(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberGenre latestMemberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(() -> new CustomException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));

        if(member.getRegions().isEmpty()){
            throw new CustomException(MemberErrorCode.REGION_FIELD_EMPTY);
        }

        List<VenueGenre> allVenueGenres = venueGenreRepository.findByVenueRegion(member.getRegions());

        List<VenueGenre> recommendVenueGenres = allVenueGenres.stream()
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

        return recommendVenueGenres.stream()
                .map(venueGenre -> {

                    List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

                    Venue venue = venueGenre.getVenue();
                    VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
                    List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());
                    String region = venue.getRegion().getText();

                    List<String> tagList = new ArrayList<>(trueGenreElements);
                    tagList.addAll(trueMoodElements);
                    tagList.add(region);

                    return VenueResponseDTO.builder()
                            .tagList(tagList)
                            .venueId(venue.getVenueId())
                            .koreanName(venue.getKoreanName())
                            .englishName(venue.getEnglishName())
                            .heartbeatNum(venue.getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());

    }


    public List<VenueResponseDTO> recommendVenuesByMood(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood latestMemberMood = memberMoodRepository.findLatestMoodByMember(member).orElseThrow(() -> new CustomException(MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST));

        if(member.getRegions().isEmpty()){
            throw new CustomException(MemberErrorCode.REGION_FIELD_EMPTY);
        }
        List<VenueMood> allVenueMoods = venueMoodRepository.findByVenueRegion(member.getRegions());

        List<VenueMood> recommendVenueMoods = allVenueMoods.stream()
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

        return recommendVenueMoods.stream()
                .map(venueMood -> {

                    Venue venue = venueMood.getVenue();
                    VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
                    List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

                    List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());
                    String region = venue.getRegion().getText();

                    List<String> tagList = new ArrayList<>(trueGenreElements);
                    tagList.addAll(trueMoodElements);
                    tagList.add(region);

                    return VenueResponseDTO.builder()
                            .tagList(tagList)
                            .venueId(venue.getVenueId())
                            .koreanName(venue.getKoreanName())
                            .englishName(venue.getEnglishName())
                            .heartbeatNum(venue.getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());

    }

    public List<VenueResponseDTO> recommendByBBpick(Long num) {
        long count = venueRepository.count();
        if (count == 0) {
            throw new CustomException(VenueErrorCode.VENUE_NOT_EXIST);
        }

        List<Long> venueIds = venueRepository.findAllIds();
        if (num > venueIds.size()) {
            throw new CustomException(VenueErrorCode.VENUE_OVER_REQUEST);
        }

        List<Long> randomIds = ThreadLocalRandom.current()
                .ints(0, venueIds.size())
                .distinct()
                .limit(num)
                .mapToObj(venueIds::get)
                .collect(Collectors.toList());


        List<Venue> venues = venueRepository.findAllById(randomIds);
        return venues.stream().
                map(venue -> {

                    VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
                    List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

                    VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
                    List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());
                    String region = venue.getRegion().getText();

                    List<String> tagList = new ArrayList<>(trueGenreElements);
                    tagList.addAll(trueMoodElements);
                    tagList.add(region);

                    return VenueResponseDTO.builder()
                            .tagList(tagList)
                            .venueId(venue.getVenueId())
                            .koreanName(venue.getKoreanName())
                            .englishName(venue.getEnglishName())
                            .heartbeatNum(venue.getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());


    }

}

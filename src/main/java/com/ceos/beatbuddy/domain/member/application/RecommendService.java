package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.*;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.venue.dto.RecommendFilterDTO;
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
    private final HeartbeatRepository heartbeatRepository;

    private static final List<String> REGIONS = Arrays.asList(
            "HONGDAE","ITAEWON","GANGNAM/SINSA","APGUJEONG","OTHERS"
    );

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


    public List<VenueResponseDTO> recommendVenues(Long memberId, Long num) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood latestMemberMood = memberMoodRepository.findLatestMoodByMember(member).orElseThrow(() -> new CustomException(MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST));
        MemberGenre latestMemberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(() -> new CustomException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));
        Vector memberVector = Vector.mergeVectors(latestMemberGenre.getGenreVector(), latestMemberMood.getMoodVector());

        if(member.getRegions().isEmpty()){
            throw new CustomException(MemberErrorCode.REGION_FIELD_EMPTY);
        }

        List<Venue> allVenues = venueRepository.findByVenueRegion(member.getRegions());
        List<Vector> allVenueVectors = new ArrayList<>();

        for(Venue venue : allVenues){
            VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
            VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
            Vector totalVector = Vector.mergeVectors(venueGenre.getGenreVector(), venueMood.getMoodVector());
            allVenueVectors.add(totalVector);
        }
        List<Vector> recommendVenueVectors = allVenueVectors.stream()
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -memberVector.cosineSimilarity(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Double.MIN_VALUE;
                    }
                }))
                .limit(num)
                .collect(Collectors.toList());

        return recommendVenueVectors.stream()
                .map(venueVector -> {
                    int index = allVenueVectors.indexOf(venueVector);
                    Venue venue = allVenues.get(index);

                    List<Double> genreElements = venueVector.getElements().subList(0, 10);
                    List<Double> moodElements = venueVector.getElements().subList(10, 18);

                    Vector genreVector = new Vector(genreElements);
                    Vector moodVector = new Vector(moodElements);

                    List<String> trueGenreElements = Vector.getTrueGenreElements(genreVector);
                    List<String> trueMoodElements = Vector.getTrueMoodElements(moodVector);
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
                            .backgroundUrl(venue.getBackgroundUrl())
                            .logoUrl(venue.getLogoUrl())
                            .isHeartbeat(heartbeatRepository.findByMemberVenue(member,venue).isPresent())
                            .heartbeatNum(venue.getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());

    }

    public List<VenueResponseDTO> recommendVenuesByFilter(Long memberId, Long num, RecommendFilterDTO recommendFilterDTO) {

        int genreIndex, moodIndex;
        List<Region> regions=null;
        List<Integer> indexList = new ArrayList<>();

        List<String> genreTags = recommendFilterDTO.getGenreTags();
        List<String> moodTags = recommendFilterDTO.getMoodTags();
        List<String> regionTags = recommendFilterDTO.getRegionTags();
        if(genreTags.isEmpty() && moodTags.isEmpty() && regionTags.isEmpty()) throw new CustomException(VectorErrorCode.TAGS_EMPTY);

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood latestMemberMood = memberMoodRepository.findLatestMoodByMember(member).orElseThrow(() -> new CustomException(MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST));
        MemberGenre latestMemberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(() -> new CustomException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));
        Vector memberVector = Vector.mergeVectors(latestMemberGenre.getGenreVector(), latestMemberMood.getMoodVector());

        List<String> trueMemberGenreElements = Vector.getTrueGenreElements(latestMemberGenre.getGenreVector());
        List<String> trueMemberMoodElements = Vector.getTrueMoodElements(latestMemberMood.getMoodVector());

        List<Region> memberRegions = member.getRegions();
        if(memberRegions.isEmpty()){
            throw new CustomException(MemberErrorCode.REGION_FIELD_EMPTY);
        }

        if(!genreTags.isEmpty()){
            for(String genreTag: genreTags){
                genreIndex = Vector.getGenreIndex(genreTag);
                if(genreIndex==-1) throw new CustomException(VectorErrorCode.GENRE_INDEX_NOT_EXIST);
                else {
                    if(!trueMemberGenreElements.contains(genreTag)) throw new CustomException(VectorErrorCode.UNAVAILABLE_GENRE);
                    indexList.add(genreIndex);
                }
            }
        }
        if(!moodTags.isEmpty()){
            for(String moodTag: moodTags){
                moodIndex = Vector.getMoodIndex(moodTag);
                if(moodIndex==-1) throw new CustomException(VectorErrorCode.MOOD_INDEX_NOT_EXIST);
                else {
                    if(!trueMemberMoodElements.contains(moodTag)) throw new CustomException(VectorErrorCode.UNAVAILABLE_MOOD);
                    indexList.add(moodIndex+10);
                }
            }
        }
        if(!regionTags.isEmpty()){
            for(String regionTag: regionTags){
                if(!REGIONS.contains(regionTag)) throw new CustomException(MemberErrorCode.REGION_NOT_EXIST);
            }
            regions = regionTags.stream()
                    .map(Region::fromText)
                    .collect(Collectors.toList());
            if(!memberRegions.containsAll(regions)) throw new CustomException(MemberErrorCode.UNAVAILABLE_REGION);
            memberRegions = regions;
        }

        List<Venue> allVenues = venueRepository.findByVenueRegion(memberRegions);
        List<Vector> allVenueVectors = new ArrayList<>();

        for(Venue venue : allVenues){
            VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
            VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
            Vector totalVector = Vector.mergeVectors(venueGenre.getGenreVector(), venueMood.getMoodVector());
            allVenueVectors.add(totalVector);
        }

        List<Vector> recommendVenueVectors = allVenueVectors.stream()
                .filter(v -> indexList.stream().allMatch(index -> v.getElements().get(index) == 1.0))
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -memberVector.cosineSimilarity(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Double.MIN_VALUE;
                    }
                }))
                .limit(num)
                .collect(Collectors.toList());

        return recommendVenueVectors.stream()
                .map(venueVector -> {
                    int index = allVenueVectors.indexOf(venueVector);
                    Venue venue = allVenues.get(index);

                    List<Double> genreElements = venueVector.getElements().subList(0, 10);
                    List<Double> moodElements = venueVector.getElements().subList(10, 18);

                    Vector genreVector = new Vector(genreElements);
                    Vector moodVector = new Vector(moodElements);

                    List<String> trueGenreElements = Vector.getTrueGenreElements(genreVector);
                    List<String> trueMoodElements = Vector.getTrueMoodElements(moodVector);
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
                            .backgroundUrl(venue.getBackgroundUrl())
                            .logoUrl(venue.getLogoUrl())
                            .isHeartbeat(heartbeatRepository.findByMemberVenue(member,venue).isPresent())
                            .heartbeatNum(venue.getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());
    }



}

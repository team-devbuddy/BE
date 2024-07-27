package com.ceos.beatbuddy.domain.heartbeat.application;

import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.heartbeat.entity.Heartbeat;
import com.ceos.beatbuddy.domain.heartbeat.exception.HeartbeatErrorCode;
import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartbeatService {

    private final HeartbeatRepository heartbeatRepository;
    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;
    private final VenueGenreRepository venueGenreRepository;
    private final VenueMoodRepository venueMoodRepository;

    @Transactional
    public HeartbeatResponseDTO addHeartbeat(Long memberId, Long venueId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(venueId).orElseThrow(()->new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        boolean doesAlreadyExist = heartbeatRepository.findByMemberVenue(member, venue).isPresent();
        if(doesAlreadyExist) {
            throw new CustomException(HeartbeatErrorCode.HEARTBEAT_ALREADY_EXIST);
        }
        Heartbeat heartbeat = Heartbeat.builder()
                .member(member)
                .venue(venue)
                .build();
        heartbeatRepository.save(heartbeat);

        venue.addHeartbeatNum();
        venueRepository.save(venue);
        return HeartbeatResponseDTO.builder()
                .memberId(member.getMemberId())
                .venueId(venue.getVenueId())
                .heartId(heartbeat.getHeartId())
                .build();
    }

    @Transactional
    public HeartbeatResponseDTO deleteHeartbeat(Long memberId, Long venueId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(venueId).orElseThrow(()->new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        Heartbeat heartbeat = heartbeatRepository.findByMemberVenue(member, venue).orElseThrow(()->new CustomException(HeartbeatErrorCode.HEARTBEAT_NOT_EXIST));

        heartbeatRepository.delete(heartbeat);

        venue.deleteHeartbeatNum();
        venueRepository.save(venue);

        return HeartbeatResponseDTO.builder()
                .memberId(member.getMemberId())
                .venueId(venue.getVenueId())
                .heartId(heartbeat.getHeartId())
                .build();
    }

    public List<VenueResponseDTO> getAllHeartbeat(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Heartbeat> heartbeats = heartbeatRepository.findByMember(member);

        return heartbeats.stream()
                .map(heartBeat -> {
                    Venue venue = heartBeat.getVenue();
                    VenueGenre venueGenre = venueGenreRepository.findByVenue(venue)
                            .orElseThrow(()-> new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
                    List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

                    VenueMood venueMood = venueMoodRepository.findByVenue(venue)
                            .orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
                    List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());
                    String region = venue.getRegion().getText();

                    List<String> tagList = new ArrayList<>(trueGenreElements);
                    tagList.addAll(trueMoodElements);
                    tagList.add(region);

                    return VenueResponseDTO.builder()
                            .isHeartbeat(true)
                            .koreanName(heartBeat.getVenue().getKoreanName())
                            .englishName(heartBeat.getVenue().getEnglishName())
                            .venueId(heartBeat.getVenue().getVenueId())
                            .logoUrl(venue.getLogoUrl())
                            .backgroundUrl(venue.getBackgroundUrl())
                            .tagList(tagList)
                            .heartbeatNum(heartBeat.getVenue().getHeartbeatNum())
                            .build();
                })
                .collect(Collectors.toList());

    }

    public HeartbeatResponseDTO getHeartbeat(Long memberId, Long venueId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(venueId).orElseThrow(()->new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        Heartbeat heartbeat = heartbeatRepository.findByMemberVenue(member, venue).orElseThrow(()->new CustomException(HeartbeatErrorCode.HEARTBEAT_NOT_EXIST));

        return HeartbeatResponseDTO.builder()
                .memberId(member.getMemberId())
                .venueId(venue.getVenueId())
                .heartId(heartbeat.getHeartId())
                .build();
    }

    public List<VenueResponseDTO> getHotChart(Long memberId){
        List<Venue> venues = venueRepository.sortByHeartbeatCount();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        return venues.stream()
                .map(venue -> {
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
                            .logoUrl(venue.getLogoUrl())
                            .backgroundUrl(venue.getBackgroundUrl())
                            .isHeartbeat(heartbeatRepository.findByMemberVenue(member, venue).isPresent())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

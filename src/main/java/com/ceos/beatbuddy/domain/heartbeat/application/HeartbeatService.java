package com.ceos.beatbuddy.domain.heartbeat.application;

import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.heartbeat.entity.Heartbeat;
import com.ceos.beatbuddy.domain.heartbeat.exception.HeartbeatErrorCode;
import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartbeatService {

    private final HeartbeatRepository heartbeatRepository;
    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public HeartbeatResponseDTO addHeartbeat(Long memberId, Long venueId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(venueId).orElseThrow(()->new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        Heartbeat heartbeat = Heartbeat.builder()
                .member(member)
                .venue(venue)
                .build();
        heartbeatRepository.save(heartbeat);
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

        return HeartbeatResponseDTO.builder()
                .memberId(member.getMemberId())
                .venueId(venue.getVenueId())
                .heartId(heartbeat.getHeartId())
                .build();
    }

    public List<HeartbeatResponseDTO> getAllHeartbeat(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Heartbeat> heartbeats = heartbeatRepository.findByMember(member);

        return heartbeats.stream()
                .map(heartBeat -> HeartbeatResponseDTO.builder()
                        .memberId(member.getMemberId())
                        .venueId(heartBeat.getVenue().getVenueId())
                        .heartId(heartBeat.getHeartId())
                        .build())
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
}

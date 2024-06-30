package com.ceos.beatbuddy.domain.member.service;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberException;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private MemberRepository userRepository;
    private VenueRepository venueRepository;

    public List<Venue> recommendVenues(Long id) {
        Member member = userRepository.findById(id).orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Venue> allVenues = venueRepository.findAll();

        return allVenues.stream()
                .sorted(Comparator.comparingDouble(v -> {
                    try {
                        return -member.getPreferenceVector().cosineSimilarity(v.getFeatureVector());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Double.MIN_VALUE;
                    }
                }))
                .limit(5)
                .collect(Collectors.toList());
    }
}

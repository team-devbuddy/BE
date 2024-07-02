package com.ceos.beatbuddy.domain.venue.application;

import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.dto.VenueVectorResponseDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueException;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueMoodRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VenueMoodService {
    private final VenueRepository venueRepository;
    private final VenueMoodRepository venueMoodRepository;

    @Transactional
    public VenueVectorResponseDTO addMoodVector(Long venueId, Map<String, Double> moods) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new VenueException(VenueErrorCode.VENUE_NOT_EXIST));

        Vector preferenceVector = Vector.fromMoods(moods);

        VenueMood venueMood = VenueMood.builder()
                .venue(venue).moodVectorString(preferenceVector.toString())
                .build();

        venueMoodRepository.save(venueMood);
        return VenueVectorResponseDTO.builder()
                .vectorString(venueMood.getMoodVectorString())
                .venueId(venue.getVenueId())
                .vectorId(venueMood.getVenueMoodId())
                .englishName(venue.getEnglishName())
                .koreanName(venue.getKoreanName())
                .region(venue.getRegion())
                .build();
    }

    @Transactional
    public VenueVectorResponseDTO updateMoodVector(Long venueId, Map<String, Double> moods) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new VenueException(VenueErrorCode.VENUE_NOT_EXIST));
        VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new VenueException(VenueErrorCode.INVALID_VENUE_INFO));

        venueMood.updateMoodVector(Vector.fromMoods(moods));

        venueMoodRepository.save(venueMood);
        return VenueVectorResponseDTO.builder()
                .vectorString(venueMood.getMoodVectorString())
                .venueId(venue.getVenueId())
                .vectorId(venueMood.getVenueMoodId())
                .englishName(venue.getEnglishName())
                .koreanName(venue.getKoreanName())
                .region(venue.getRegion())
                .build();
    }

}

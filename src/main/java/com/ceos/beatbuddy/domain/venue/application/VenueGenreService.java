package com.ceos.beatbuddy.domain.venue.application;

import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.dto.VenueVectorResponseDTO;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VenueGenreService {
    private final VenueRepository venueRepository;
    private final VenueGenreRepository venueGenreRepository;

    @Transactional
    public VenueVectorResponseDTO addGenreVector(Long venueId, Map<String, Double> genres) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));

        Vector preferenceVector = Vector.fromGenres(genres);

        VenueGenre venueGenre = VenueGenre.builder()
                .venue(venue).genreVectorString(preferenceVector.toString())
                .build();

        venueGenreRepository.save(venueGenre);
        return VenueVectorResponseDTO.builder()
                .vectorString(venueGenre.getGenreVectorString())
                .venueId(venue.getVenueId())
                .vectorId(venueGenre.getVenueGenreId())
                .englishName(venue.getEnglishName())
                .koreanName(venue.getKoreanName())
                .region(venue.getRegion())
                .build();
    }

    @Transactional
    public VenueVectorResponseDTO updateGenreVector(Long venueId, Map<String, Double> genres) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueErrorCode.INVALID_VENUE_INFO));

        venueGenre.updateGenreVector(Vector.fromGenres(genres));

        venueGenreRepository.save(venueGenre);
        return VenueVectorResponseDTO.builder()
                .vectorString(venueGenre.getGenreVectorString())
                .venueId(venue.getVenueId())
                .vectorId(venueGenre.getVenueGenreId())
                .englishName(venue.getEnglishName())
                .koreanName(venue.getKoreanName())
                .region(venue.getRegion())
                .build();
    }
}

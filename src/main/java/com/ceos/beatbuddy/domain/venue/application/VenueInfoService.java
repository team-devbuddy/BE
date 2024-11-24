package com.ceos.beatbuddy.domain.venue.application;

import com.amazonaws.services.s3.AmazonS3;
import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.dto.VenueInfoResponseDTO;
import com.ceos.beatbuddy.domain.venue.dto.VenueRequestDTO;
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
import com.ceos.beatbuddy.global.UploadUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueInfoService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final VenueRepository venueRepository;
    private final HeartbeatRepository heartbeatRepository;
    private final MemberRepository memberRepository;
    private final VenueGenreRepository venueGenreRepository;
    private final VenueMoodRepository venueMoodRepository;

    private final AmazonS3 amazonS3;

    public List<Venue> getVenueInfoList() {
        return venueRepository.findAll();
    }

    public VenueInfoResponseDTO getVenueInfo(Long venueId, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        boolean isHeartbeat = heartbeatRepository.findByMemberVenue(member, venue).isPresent();

        VenueGenre venueGenre = venueGenreRepository.findByVenue(venue)
                .orElseThrow(() -> new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
        List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

        VenueMood venueMood = venueMoodRepository.findByVenue(venue)
                .orElseThrow(() -> new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
        List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());
        String region = venue.getRegion().getText();

        List<String> tagList = new ArrayList<>(trueGenreElements);
        tagList.addAll(trueMoodElements);
        tagList.add(region);

        return VenueInfoResponseDTO.builder()
                .venue(venue)
                .isHeartbeat(isHeartbeat)
                .tagList(tagList)
                .build();
    }

    @Transactional
    public Long deleteVenueInfo(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
        this.deleteImage(venue.getLogoUrl());
        this.deleteImage(venue.getBackgroundUrl());
        return venueRepository.deleteByVenueId(venueId);
    }

    @Transactional
    public Venue addVenueInfo(VenueRequestDTO request, MultipartFile logoImage, List<MultipartFile> backgroundImage)
            throws IOException {

        String logoImageUrl = null;
        List<String> backgroundImageUrls = new ArrayList<>();

        if (logoImage != null) {
            logoImageUrl = UploadUtil.upload(logoImage);
        }

        if (!backgroundImage.isEmpty()) {
            for (MultipartFile multipartFile : backgroundImage) {
                backgroundImageUrls.add(UploadUtil.upload(multipartFile));
            }
        }

        return venueRepository.save(Venue.of(request, logoImageUrl, backgroundImageUrls));
    }

    private void deleteImage(String imageUrl) {
        String s3FileName = imageUrl.split("/")[3];

        try {
            amazonS3.deleteObject(bucketName, s3FileName);
        } catch (Exception e) {
            throw new CustomException(VenueErrorCode.IMAGE_DELETE_FAILED);
        }

    }

    private void deleteImage(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            String s3FileName = imageUrl.split("/")[3];

            try {
                amazonS3.deleteObject(bucketName, s3FileName);
            } catch (Exception e) {
                throw new CustomException(VenueErrorCode.IMAGE_DELETE_FAILED);
            }
        }
    }

    @Transactional
    public Venue updateVenueInfo(Long venueId, VenueRequestDTO venueRequestDTO, MultipartFile logoImage, List<MultipartFile> backgroundImage)
            throws IOException {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));

        String logoImageUrl = venue.getLogoUrl();
        List<String> backgroundImageUrls = venue.getBackgroundUrl();

        if (logoImage != null) {
            this.deleteImage(logoImageUrl);
            logoImageUrl = UploadUtil.upload(logoImage);
        }

        if (!backgroundImage.isEmpty()) {
            this.deleteImage(backgroundImageUrls);
            backgroundImageUrls = new ArrayList<>();
            for (MultipartFile multipartFile : backgroundImage) {
                backgroundImageUrls.add(UploadUtil.upload(multipartFile));
            }
        }

        venue.update(venueRequestDTO, logoImageUrl, backgroundImageUrls);
        return venueRepository.save(venue);
    }
}

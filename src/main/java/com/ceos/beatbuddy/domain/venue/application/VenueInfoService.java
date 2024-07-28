package com.ceos.beatbuddy.domain.venue.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

        VenueGenre venueGenre = venueGenreRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST));
        List<String> trueGenreElements = Vector.getTrueGenreElements(venueGenre.getGenreVector());

        VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
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
            logoImageUrl = this.upload(logoImage);
        }

        if (!backgroundImage.isEmpty()) {
            for (MultipartFile multipartFile : backgroundImage) {
                backgroundImageUrls.add(this.upload(multipartFile));
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

    public String upload(MultipartFile image) throws IOException {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(VenueErrorCode.INVALID_VENUE_IMAGE);
        }

        this.validationImage(image.getOriginalFilename());
        return this.uploadImageS3(image);
    }

    private String uploadImageS3(MultipartFile image) throws IOException {
        String s3FileName = generateFileName(image.getOriginalFilename()); //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = getObjectMetadata(image);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            //실제로 S3에 이미지 데이터를 넣는 부분이다.
            amazonS3.putObject(putObjectRequest); // put image to S3
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(VenueErrorCode.IMAGE_UPLOAD_FAILED);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    private static String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString().substring(0, 10) + originalFilename;
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile image) {
        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());
        return metadata;
    }

    private void validationImage(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(VenueErrorCode.INVALID_VENUE_IMAGE);
        }

//        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
//        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "heic", "mp4", "mov");
//
//        if (!allowedExtensions.contains(extension)) {
//            throw new CustomException(VenueErrorCode.INVALID_VENUE_IMAGE);
//        }
    }

}

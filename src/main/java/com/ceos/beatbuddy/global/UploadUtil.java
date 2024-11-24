package com.ceos.beatbuddy.global;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class UploadUtil {
    @Value("${cloud.aws.s3.bucket}")
    private static String bucketName;
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static AmazonS3 amazonS3;

    @PostConstruct
    private void init() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public static String upload(MultipartFile image) throws IOException {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(VenueErrorCode.INVALID_VENUE_IMAGE);
        }

        validationImage(image.getOriginalFilename());
        return uploadImageS3(image);
    }

    private static String uploadImageS3(MultipartFile image) throws IOException {
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

    private static void validationImage(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(VenueErrorCode.INVALID_VENUE_IMAGE);
        }
    }
}

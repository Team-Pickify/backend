package com.pickyfy.pickyfy.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.image}")
    private String imageFolder;

    // TODO: @Async 비동기 처리
    // TODO: 비동기 처리에 따른 반환값을 CompletableFuture<String>로 수정
    public String upload(MultipartFile multipartFile) {
        String fileName = imageFolder + multipartFile.getOriginalFilename();

        if (!(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(
                ".gif") || fileName.endsWith(".bmp"))) {
            throw new RuntimeException();
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        return putS3(multipartFile, fileName, metadata);     // 업로드된 파일의 S3 URL 주소 반환
    }

    // TODO: @Async 비동기 처리
    // TODO: 비동기 처리에 따른 반환값을 CompletableFuture<String>로 수정
    private String putS3(MultipartFile multipartFile, String fileName, ObjectMetadata metadata) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String getImageFileUrl(String fileName) {
        String filePath = imageFolder + fileName;
        return amazonS3Client.getUrl(bucket, filePath).toString();
    }

    //파일 이름 중복 방지 코드
    private String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension; // 고유 파일 이름 생성
    }

    //검증로직 필요하면 사용 가능
    private void validateFileFormat(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
    }

    //S3 파일 삭제가 필요한 경우
    public void removeFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3Client.deleteObject(bucket, imageFolder + fileName);
    }
}
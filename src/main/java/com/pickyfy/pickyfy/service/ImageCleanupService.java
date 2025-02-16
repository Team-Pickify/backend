package com.pickyfy.pickyfy.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.pickyfy.pickyfy.repository.PlaceImageRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageCleanupService {

    private final AmazonS3Client amazonS3Client;
    private final PlaceImageRepository placeImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.image}")
    private String imageFolder;

    @Scheduled(cron = "0 0 2 * * 1") // 매주 월요일 새벽 2시에 실행
    public void cleanupOrphanedImages() {
        try {
            // 1. S3의 모든 이미지 목록 가져오기
            ListObjectsV2Request listReq = new ListObjectsV2Request()
                    .withBucketName(bucket)
                    .withPrefix(imageFolder);

            List<String> s3ImageUrls = new ArrayList<>();
            ListObjectsV2Result result;

            do {
                result = amazonS3Client.listObjectsV2(listReq);
                for (S3ObjectSummary obj : result.getObjectSummaries()) {
                    s3ImageUrls.add(amazonS3Client.getUrl(bucket, obj.getKey()).toString());
                }
                listReq.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

            // 2. DB에 저장된 모든 이미지 URL 가져오기
            List<String> dbImageUrls = placeImageRepository.findAllImageUrls();

            // 3. S3에는 있지만 DB에는 없는 이미지 찾기
            List<String> orphanedImages = s3ImageUrls.stream()
                    .filter(s3Url -> !dbImageUrls.contains(s3Url))
                    .toList();

            // 4. 고아 이미지 삭제
            for (String imageUrl : orphanedImages) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                amazonS3Client.deleteObject(bucket, imageFolder + fileName);
                log.info("Deleted orphaned image: {}", imageUrl);
            }

            log.info("Image cleanup completed. Deleted {} orphaned images", orphanedImages.size());

        } catch (Exception e) {
            log.error("Error during image cleanup", e);
        }
    }
}

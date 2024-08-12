package com.fluffytime.post.aws;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final Region region; // Region 주입받음

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        // 파일 이름을 UUID와 원본 파일 이름으로 설정하여 고유한 이름 생성
        String fileName =
            UUID.randomUUID().toString() + "_" + Paths.get(file.getOriginalFilename()).getFileName()
                .toString();
        try {
            // S3에 파일 업로드
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType()) // url 클릭시 파일 다운로드가 아닌 미리보기로 설정
                    .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );
            log.info("File uploaded successfully: {}", fileName); // 업로드 성공 로그
            return fileName; // 업로드한 파일 이름 반환
        } catch (IOException e) {
            log.error("Failed to upload file: {}", fileName, e); // 업로드 실패 로그
            throw new RuntimeException("Failed to upload file", e); // 예외 발생
        }
    }

    public String getFileUrl(String fileName) {
        // S3에서 파일 접근 URL 생성
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(),
            fileName);
        log.info("Generated file URL: {}", fileUrl); // 생성된 URL 로그
        return fileUrl; // 파일 URL 반환
    }
}
package com.fluffytime.global.config.aws;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final Region region; // Region 주입받음

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    // 멀티파트 업로드 시 각 파트의 크기 설정 (5MB)
    private static final long PART_SIZE = 5 * 1024 * 1024;

    public String uploadFile(MultipartFile file) {
        // 업로드할 파일의 고유한 이름 생성
        String fileName = UUID.randomUUID().toString() + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString();

        // 1. 멀티파트 업로드 요청 생성
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType())  // 파일의 MIME 타입 설정
            .build();

        // 멀티파트 업로드를 시작하고, S3로부터 uploadId를 받음
        String uploadId = s3Client.createMultipartUpload(createMultipartUploadRequest).uploadId();

        // 업로드된 각 파트에 대한 정보를 저장할 리스트 초기화
        List<CompletedPart> completedParts = new ArrayList<>();
        try {
            // 업로드할 파일을 바이트 배열로 변환
            byte[] fileBytes = file.getBytes();
            int partNumber = 1;

            // 2. 파일을 파트 크기(PART_SIZE)만큼 나누어 각 파트를 업로드
            for (int i = 0; i < fileBytes.length; i += PART_SIZE) {
                int end = Math.min(fileBytes.length, i + (int) PART_SIZE);  // 현재 파트의 끝 위치 계산
                byte[] partBytes = java.util.Arrays.copyOfRange(fileBytes, i, end);  // 파트 데이터를 추출

                // 파트 업로드 요청 생성
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .partNumber(partNumber++)  // 현재 파트의 번호 설정
                    .build();

                // 파트를 S3에 업로드하고, 결과를 받아옴
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(partBytes));

                // 업로드된 파트의 정보를 리스트에 저장
                completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber - 1)
                    .eTag(uploadPartResponse.eTag())  // S3로부터 받은 ETag 값 저장
                    .build());
            }

            // 3. 모든 파트 업로드가 완료된 후 업로드를 완료하는 요청을 보냄
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())  // 모든 파트 정보를 포함하여 요청
                .build();

            // 업로드 완료 요청을 S3에 보냄
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            // 업로드 성공 시 로그 출력 및 업로드된 파일의 이름 반환
            log.info("Large file uploaded successfully: {}", fileName);
            return fileName;
        } catch (IOException e) {
            // 업로드 도중 예외가 발생하면 로그 출력 및 업로드 중단 요청
            log.error("Failed to upload large file: {}", fileName, e);
            s3Client.abortMultipartUpload(a -> a.bucket(bucketName).key(fileName).uploadId(uploadId));
            throw new RuntimeException("Failed to upload large file", e);
        }
    }

    public String getFileUrl(String fileName) {
        // S3에서 파일 접근 URL 생성
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), fileName);
        log.info("Generated file URL: {}", fileUrl); // 생성된 URL 로그
        return fileUrl; // 파일 URL 반환
    }
}
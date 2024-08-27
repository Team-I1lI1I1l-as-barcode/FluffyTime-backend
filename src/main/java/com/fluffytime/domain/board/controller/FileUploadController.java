package com.fluffytime.domain.board.controller;

import com.fluffytime.global.config.aws.S3Service;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class FileUploadController {

    private final S3Service s3Service; // S3Service 주입받음

    @PostMapping("/upload") // 파일 업로드 엔드포인트 매핑
    public ResponseEntity<Map<String, String>> uploadFile(
        @RequestParam("file") MultipartFile file) { // 파일 업로드 요청 파라미터
        String fileName = s3Service.uploadFile(file); // 파일 업로드 처리
        String fileUrl = s3Service.getFileUrl(fileName); // 업로드된 파일의 URL 가져오기
        Map<String, String> response = new HashMap<>(); // 응답 데이터를 담을 맵 생성
        response.put("url", fileUrl); // 맵에 URL 추가
        return ResponseEntity.ok(response); // 응답 반환
    }
}

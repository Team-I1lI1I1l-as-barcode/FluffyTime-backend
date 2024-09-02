package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.response.ReelsResponse;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.Reels;
import com.fluffytime.domain.board.repository.ReelsRepository;
import com.fluffytime.domain.user.entity.Profile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReelsService {

    private final ReelsRepository reelsRepository;

    // 모든 릴스를 조회하여 반환하는 메서드
    public List<ReelsResponse> getAllReels() {
        return reelsRepository.findAll().stream()
            .map(reels -> {
                // 프로필 이미지 URL을 가져오기
                String profileImageUrl = null;
                Profile profile = reels.getPost().getUser().getProfile();

                if (profile != null && profile.getProfileImages() != null) {
                    profileImageUrl = profile.getProfileImages().getFilePath();
                }

                // ReelsResponse 생성 시 프로필 이미지 URL 포함
                return new ReelsResponse(
                    reels.getReelsId(),
                    reels.getFilename(),
                    reels.getFileUrl(),
                    reels.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
                    reels.getPost().getContent(),
                    reels.getPost().getUser().getNickname(),
                    profileImageUrl // 프로필 사진 URL 추가
                );
            })
            .collect(Collectors.toList());
    }

    public void reelsUpload(Post post, String fileName, String fileUrl) {
        Reels reels = Reels.builder()
            .post(post)
            .filename(fileName)
            .fileUrl(fileUrl)
            .createdAt(LocalDateTime.now())
            .build();
        reelsRepository.save(reels);
        log.info("Reels uploaded successfully: {}, URL: {}", fileName, fileUrl);
    }

}

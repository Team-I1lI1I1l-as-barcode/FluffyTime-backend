package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.response.ReelsResponse;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.Reels;
import com.fluffytime.domain.board.repository.BookmarkRepository;
import com.fluffytime.domain.board.repository.PostLikeRepository;
import com.fluffytime.domain.board.repository.ReelsRepository;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
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
    private final BookmarkRepository bookmarkRepository;
    private final PostLikeRepository postLikeRepository;
    private final JwtTokenizer jwtTokenizer;

    // 모든 릴스를 조회하여 반환하는 메서드
    public List<ReelsResponse> getAllReels(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request); // 사용자 ID 가져오기

        return reelsRepository.findAll().stream()
            .map(reels -> {
                // 프로필 이미지 URL을 가져오기
                String profileImageUrl = null;
                Profile profile = reels.getPost().getUser().getProfile();

                if (profile != null && profile.getProfileImages() != null) {
                    profileImageUrl = profile.getProfileImages().getFilePath();
                }

                // 북마크 여부 확인
                boolean isBookmarked = bookmarkRepository.existsByUserUserIdAndPostPostId(userId, reels.getPost().getPostId());

                // 좋아요 수 계산
                int likeCount = postLikeRepository.countByPost(reels.getPost());

                // 사용자가 해당 릴스를 좋아요 했는지 여부 확인
                boolean isLiked = postLikeRepository.existsByPostAndUserUserId(reels.getPost(), userId);

                // ReelsResponse 생성 시
                return new ReelsResponse(
                    reels.getReelsId(),
                    reels.getPost().getPostId(),
                    reels.getUser().getUserId(),
                    reels.getFilename(),
                    reels.getFilepath(),
                    reels.getFilesize(),
                    reels.getMimetype(),
                    reels.getPost().getContent(),
                    reels.getUser().getNickname(),
                    profileImageUrl,
                    isBookmarked,
                    likeCount,
                    isLiked
                );
            })
            .collect(Collectors.toList());
    }

    // 요청에서 사용자 ID를 가져오는 메서드
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String accessToken = jwtTokenizer.getTokenFromCookie(request, "accessToken");
        if (accessToken == null) {
            return null; // 사용자가 로그인하지 않은 경우
        }
        return jwtTokenizer.getUserIdFromToken(accessToken);
    }

    public void reelsUpload(Post post, User user, String filename, String filepath, String mimetype) {
        // 파일 크기 계산 (파일 시스템에 실제로 파일이 존재한다고 가정)
        File file = new File(filepath);
        Long filesize = file.length();

        // Reels 객체 생성 및 저장
        Reels reels = Reels.builder()
            .post(post)
            .user(user)
            .filename(filename)
            .filepath(filepath)
            .filesize(filesize)
            .mimetype(mimetype)
            .build();

        reelsRepository.save(reels);
    }

}
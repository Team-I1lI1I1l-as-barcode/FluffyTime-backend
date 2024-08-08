package com.fluffytime.post.service;

import com.fluffytime.domain.Post;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.post.dto.PostRequest;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // UserRepository 주입받음
    private final S3Service s3Service; // S3Service 주입받음
    private final JwtTokenizer jwtTokenizer; // JWT 토크나이저 주입

    // 게시글 등록하기
    public Long createPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        // HTTP 요청에서 JWT 토큰 추출
        String token = getToken(request);
        if (token == null) {
            throw new IllegalArgumentException("유효한 토큰이 필요합니다.");
        }

        // 토큰을 파싱하여 클레임 추출
        Claims claims = jwtTokenizer.parseAccessToken(token);
        Long userId = claims.get("userId", Long.class);

        // User 객체 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유효한 사용자 ID가 필요합니다."));

        if (files.length > 10) {
            throw new IllegalArgumentException("최대 10개의 이미지만 업로드할 수 있습니다."); // 파일 개수 제한
        }

        if (postRequest.getTempStatus() == null) {
            postRequest.setTempStatus(TempStatus.SAVE); // 기본 상태 설정
        }

        List<String> imageUrls = postRequest.getImageUrls();

        for (MultipartFile file : files) {
            String imageUrl = s3Service.uploadFile(file); // 파일 업로드
            imageUrls.add(imageUrl); // 업로드된 파일 URL 추가
        }

        Post post = Post.builder()
            .user(user) // 사용자 객체 설정
            .content(postRequest.getContent()) // 게시글 내용 설정
            .createdAt(LocalDateTime.now()) // 생성 시간 설정
            .tempStatus(postRequest.getTempStatus()) // 게시글 상태 설정
            .imageUrls(imageUrls) // 이미지 URL 설정
            .build();

        postRepository.save(post); // 게시글 저장
        return post.getPostId(); // 저장된 게시글 ID 반환
    }

    // 게시글 조회하기
    public Post getPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Invalid post Id: " + id)); // 게시글 ID로 조회
    }

    // 게시글 수정하기
    public Post updatePost(Long id, PostRequest postRequest, MultipartFile[] files) {
        Post existingPost = getPostById(id);

        if (postRequest.getContent() != null) {
            existingPost.setContent(postRequest.getContent()); // 내용 수정
        }

        List<String> imageUrls = existingPost.getImageUrls();
        for (MultipartFile file : files) {
            String imageUrl = s3Service.uploadFile(file); // 파일 업로드
            imageUrls.add(imageUrl); // 업로드된 파일 URL 추가
        }
        existingPost.setImageUrls(imageUrls);
        existingPost.setUpdatedAt(LocalDateTime.now()); // 수정 시간 설정

        postRepository.save(existingPost); // 게시글 저장
        return existingPost;
    }

    // 게시글 삭제하기
    public void deletePost(Long id) {
        postRepository.deleteById(id); // 게시글 삭제
    }

    // 임시 게시글 삭제하기
    public void deleteTempPost(Long id) {
        Post post = getPostById(id);
        if (post.getTempStatus() == TempStatus.TEMP) {
            postRepository.deleteById(id); // 임시 게시글 삭제
        } else {
            throw new IllegalArgumentException("Post is not in TEMP status"); // 임시 게시글이 아닌 경우 예외 발생
        }
    }

    // 임시 게시글 목록 조회하기
    public List<Post> getTempPosts() {
        return postRepository.findAll().stream()
            .filter(post -> post.getTempStatus() == TempStatus.TEMP) // 임시 상태인 게시글 필터링
            .collect(Collectors.toList());
    }

    // HTTP 요청에서 JWT 토큰 추출하기
    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return null;
    }
}

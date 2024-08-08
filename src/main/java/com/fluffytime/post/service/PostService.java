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
import jakarta.servlet.http.Cookie;
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
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final JwtTokenizer jwtTokenizer;

    // 게시글 등록하기
    public Long createPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        if (files.length > 10) {
            throw new IllegalArgumentException("최대 10개의 이미지만 업로드할 수 있습니다.");
        }

        String token = getTokenFromRequest(request);
        Claims claims = jwtTokenizer.parseAccessToken(token);
        Long userId = claims.get("userId", Long.class);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (postRequest.getTempStatus() == null) {
            postRequest.setTempStatus(TempStatus.SAVE);
        }

        List<String> imageUrls = postRequest.getImageUrls();

        for (MultipartFile file : files) {
            String imageUrl = s3Service.uploadFile(file); // 파일 업로드
            imageUrls.add(imageUrl); // 업로드된 파일 URL 추가
        }

        Post post = Post.builder()
            .user(user)
            .content(postRequest.getContent())
            .createdAt(LocalDateTime.now())
            .tempStatus(postRequest.getTempStatus())
            .imageUrls(imageUrls)
            .build();

        postRepository.save(post);
        return post.getPostId(); // 저장된 게시글 ID 반환
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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
            imageUrls.add(imageUrl);
        }
        existingPost.setImageUrls(imageUrls);
        existingPost.setUpdatedAt(LocalDateTime.now());

        postRepository.save(existingPost); // 게시글 저장
        return existingPost;
    }

    // 게시글 삭제하기
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public void deleteTempPost(Long id) {
        Post post = getPostById(id);
        if (post.getTempStatus() == TempStatus.TEMP) {
            postRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Post is not in TEMP status");
        }
    }

    // 임시 게시글 삭제하기
    public List<Post> getTempPosts() {
        return postRepository.findAll().stream()
            .filter(post -> post.getTempStatus() == TempStatus.TEMP)
            .collect(Collectors.toList());
    }
}

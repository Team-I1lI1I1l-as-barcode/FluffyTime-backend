package com.fluffytime.bookmark.service;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.bookmark.dto.response.BookmarkResponse;
import com.fluffytime.bookmark.exception.BookmarkAlreadyExists;
import com.fluffytime.bookmark.exception.BookmarkNotFound;
import com.fluffytime.common.exception.global.PostNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Bookmark;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.User;
import com.fluffytime.repository.BookmarkRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;

    // JWT 토큰에서 사용자 ID 추출
    public Long findUserByAccessToken(HttpServletRequest request) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        return jwtTokenizer.getUserIdFromToken(accessToken);
    }

    // 북마크 생성하기
    @Transactional
    public BookmarkResponse createBookmark(Long postId, HttpServletRequest request) {
        Long userId = findUserByAccessToken(request);

        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        if (bookmarkRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
            throw new BookmarkAlreadyExists();
        }

        Bookmark bookmark = Bookmark.builder()
            .user(user)
            .post(post)
            .build();

        bookmarkRepository.save(bookmark);
        return new BookmarkResponse(bookmark.getBookmarkId(), userId, postId);
    }

    // 북마크 삭제하기
    @Transactional
    public void deleteBookmark(Long bookmarkId, HttpServletRequest request) {
        log.info("북마크 삭제 시도, 북마크 ID: {}", bookmarkId);

        Long userId = findUserByAccessToken(request);

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
            .orElseThrow(() -> {
                log.error("북마크를 찾을 수 없습니다, 북마크 ID: {}", bookmarkId);
                return new BookmarkNotFound();
            });

        log.info("사용자 확인, 사용자 ID: {}", userId);

        if (!bookmark.getUser().getUserId().equals(userId)) {
            log.error("사용자 ID 불일치: 요청한 사용자 ID = {}, 북마크 소유자 ID = {}", userId,
                bookmark.getUser().getUserId());
            throw new UserNotFound(); // 사용자 ID가 일치하지 않으면 UserNotFound 예외 발생
        }

        bookmarkRepository.delete(bookmark);
        log.info("북마크 삭제 성공, 북마크 ID: {}", bookmarkId);
    }

    // 사용자가 북마크한 게시물 목록 조회하기
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarksByUser(HttpServletRequest request) {
        Long userId = findUserByAccessToken(request);

        userRepository.findById(userId).orElseThrow(UserNotFound::new);

        List<Bookmark> bookmarks = bookmarkRepository.findByUserUserId(userId);
        return bookmarks.stream()
            .map(bookmark -> new BookmarkResponse(bookmark.getBookmarkId(),
                bookmark.getUser().getUserId(), bookmark.getPost().getPostId()))
            .collect(Collectors.toList());
    }

    // 게시물을 북마크한 사용자 조회하기
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarksByPost(Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        List<Bookmark> bookmarks = bookmarkRepository.findByPostPostId(postId);
        return bookmarks.stream()
            .map(bookmark -> new BookmarkResponse(bookmark.getBookmarkId(),
                bookmark.getUser().getUserId(), bookmark.getPost().getPostId()))
            .collect(Collectors.toList());
    }

    // 사용자가 게시물을 북마크했는지 조회하기
    @Transactional(readOnly = true)
    public boolean checkIfUserBookmarkedPost(Long postId, HttpServletRequest request) {
        Long userId = findUserByAccessToken(request);

        userRepository.findById(userId).orElseThrow(UserNotFound::new);
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return bookmarkRepository.existsByUserUserIdAndPostPostId(userId, postId);
    }
}

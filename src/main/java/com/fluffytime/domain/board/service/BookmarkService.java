package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.response.BookmarkResponse;
import com.fluffytime.domain.board.exception.BookmarkAlreadyExists;
import com.fluffytime.domain.board.exception.BookmarkNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.PostNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
import com.fluffytime.domain.board.entity.Bookmark;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.repository.BookmarkRepository;
import com.fluffytime.domain.board.repository.PostRepository;
import com.fluffytime.domain.user.repository.UserRepository;
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
        // JwtTokenizer의 getTokenFromCookie를 사용하여 쿠키에서 accessToken 추출
        String accessToken = jwtTokenizer.getTokenFromCookie(request, "accessToken");

        if (accessToken == null) {
            throw new UserNotFound(); // 토큰이 없으면 예외 처리
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

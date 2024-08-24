package com.fluffytime.bookmark.controller.api;

import com.fluffytime.bookmark.dto.request.BookmarkRequest;
import com.fluffytime.bookmark.dto.response.BookmarkResponse;
import com.fluffytime.bookmark.service.BookmarkService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Slf4j
public class BookmarkRestController {

    private final BookmarkService bookmarkService;

    // 북마크 생성하기
    @PostMapping("/reg")
    public ResponseEntity<BookmarkResponse> createBookmark(
        @RequestBody BookmarkRequest bookmarkRequest,
        HttpServletRequest request) {

        BookmarkResponse bookmarkResponse = bookmarkService.createBookmark(
            bookmarkRequest.getPostId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarkResponse);
    }

    // 북마크 삭제하기
    @PostMapping("/delete/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(
        @PathVariable Long bookmarkId,
        HttpServletRequest request) {

        log.info("북마크 삭제 요청 받음, 북마크 ID: {}", bookmarkId);

        bookmarkService.deleteBookmark(bookmarkId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 사용자가 북마크한 게시물 목록 조회하기
    @GetMapping("/user")
    public ResponseEntity<List<BookmarkResponse>> getBookmarksByUser(HttpServletRequest request) {
        List<BookmarkResponse> bookmarkList = bookmarkService.getBookmarksByUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarkList);
    }

    // 게시물을 북마크한 사용자 조회하기
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<BookmarkResponse>> getBookmarksByPost(@PathVariable Long postId) {
        List<BookmarkResponse> bookmarkList = bookmarkService.getBookmarksByPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarkList);
    }

    // 사용자가 게시물을 북마크했는지 조회하기
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIfUserBookmarkedPost(
        @RequestParam Long postId,
        HttpServletRequest request) {

        boolean isBookmarked = bookmarkService.checkIfUserBookmarkedPost(postId, request);
        return ResponseEntity.status(HttpStatus.OK).body(isBookmarked);
    }
}
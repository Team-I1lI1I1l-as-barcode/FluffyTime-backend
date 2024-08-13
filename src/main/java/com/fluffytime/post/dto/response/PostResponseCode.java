package com.fluffytime.post.dto.response;

import lombok.Getter;

@Getter
public enum PostResponseCode {
    CREATE_POST_SUCCESS("200", "게시글 생성 성공"),
    GET_POST_SUCCESS("200", "게시글 조회 성공"),
    UPDATE_POST_SUCCESS("200", "게시글 수정 성공"),
    DELETE_POST_SUCCESS("200", "게시글 삭제 성공"),
    TEMP_SAVE_POST_SUCCESS("200", "임시 저장 성공"),
    DELETE_TEMP_POST_SUCCESS("200", "임시 게시글 삭제 성공"),
    GET_TEMP_POSTS_SUCCESS("200", "임시 게시글 조회 성공"),
    UPLOAD_FILE_SUCCESS("200", "파일 업로드 성공");

    private final String code;
    private final String message;

    PostResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

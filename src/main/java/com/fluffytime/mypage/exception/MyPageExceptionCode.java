package com.fluffytime.mypage.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 마이페이지에서 발생할 수 있는 다양한 예외 코드 정리
// 상수명(에러상태코드, 코드 메시지)
@Getter
@AllArgsConstructor
public enum MyPageExceptionCode {

    // 사용자 요청이 정상적으로 처리된 경우
    OK("200", "The request was processed successfully."),
    // 마이페이지 정보가 정상적으로 등록된 경우
    MYPAGE_CREATED("201", "My page information has been successfully created."),
    // 프로필 정보가 정상적으로 등록된 경우
    PROFILE_CREATED("201", "Profile information has been successfully created."),


    // 제공될 정보가 누락되거나 잘못된 형식으로 요청을 보낸 경우
    BAD_REQUEST("400", "Invalid or missing data."),
    // 다른 사용자가 나의 마이페이지를 편집하려고 하거나 수정하려고 할 때
    NO_PERMISSION_MYPAGE("403", "You do not have permission to edit this mypage."),
    // 다른 사용자가 나의 프로필을 편집하려고 하거나 수정하려고 할 때
    NO_PERMISSION_PROFILE("403", "You do not have permission to edit this profile."),
    // 요청된 id에 해당되는 마이페이지를 찾을 수 없는 경우
    NOT_FOUND_MYPAGE("404", "The requested mypage could not be found."),
    // 요청된 id에 해당되는 프로필을 찾을 수 없는 경우
    NOT_FOUND_PROFILE("404", "The requested profile could not be found."),
    // 요청한 userid에 해당하는 유저 정보를 찾을 수 없는 경우
    NOT_FOUND_USER("404", "The requested user information could not be found."),
    // 서버 내부에서 예기치 않은 오류 발생으로 요청 처리 불가
    INTERNAL_SERVER_ERROR("500", "An unexpected error occurred.");

    private String code; // 에러 상태 코드
    private String message; // 에러 메시지 설정


}

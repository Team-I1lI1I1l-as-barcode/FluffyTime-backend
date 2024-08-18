package com.fluffytime.auth.jwt.util;

import com.fluffytime.auth.jwt.exception.JwtErrorCode;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtResponseProvider {

    // JWT 인증 과정에서 발생한 예외에 대한 HTTP 응답 설정
    // 응답의 Content-Type과 상태 코드를 설정하고, 예외 메시지와 코드를 포함한 JSON 응답을 생성하여 클라이언트에게 반환
    public static void setResponse(HttpServletResponse response, JwtErrorCode exceptionCode)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(exceptionCode.getHttpStatus().value());
        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("code", exceptionCode.getCode());
        errorInfo.put("message", exceptionCode.getMessage());
        // Gson 객체를 사용하여 HashMap을 JSON 문자열로 변환
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        // 변환된 JSON 문자열을 응답으로 출력
        response.getWriter().print(responseJson);
    }

}

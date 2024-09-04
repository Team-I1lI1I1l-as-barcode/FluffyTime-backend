package com.fluffytime.global.auth.jwt.exception;

import com.fluffytime.global.auth.jwt.util.JwtResponseProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 시큐리티가 인증되지 않은 사용자가 인증이 필요한 리소스에 접근 할때 동작하게 하는 인터페이스
// 사용자가 로그인하지 않은 상태에서 보호된 페이지에 접근하려고 할 때
// 사용자의 세션이 만료되어 인증이 필요한 자원에 접근하지 못할 때
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //사용자가 인증되지 않았을때.. 어떻게 처리할지를 구현함.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        // 요청 속성에서 예외 메시지 가져오기
        String exception = (String) request.getAttribute("exception");

        //RESTful로 요청한건지..  그냥 페이지 요청한건지 구분해서 다르게 동작하도록 구현.
        if (isRestRequest(request)) {
            handleRestResponse(request, response, exception);
        } else {
            handlePageResponse(request, response, exception);
        }
    }

    // 사용자 요청이 RESTful인지 판별
    private boolean isRestRequest(HttpServletRequest request) {

        String requestedWithHeader = request.getHeader("X-Requested-With");

        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI()
            .startsWith("/api/");
    }

    // 사용자 요청이 페이지 요청라면 이에 대한 처리 구현
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {

        log.error("Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals("INVALID_TOKEN")) {
                log.error("entry point >> invalid token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                log.error("entry point >> expired token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                log.error("entry point >> unsupported token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                log.error("entry point >> not found token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.TOKEN_NOT_FOUND);
            } else {
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        }
        String requestURI = request.getRequestURI();
        response.sendRedirect("/login?redirectURL="+requestURI);
        log.info("redirect url = {}", request.getRequestURI());
    }

    // 사용자 요청이 RESTful라면 이에 대한 처리 구현
    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals("INVALID_TOKEN")) {
                log.error("entry point >> invalid token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                log.error("entry point >> expired token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                log.error("entry point >> unsupported token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                log.error("entry point >> not found token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.TOKEN_NOT_FOUND);
            } else {
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        } else {
            JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
        }
    }
}


package com.fluffytime.login.jwt.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //시큐리티가 인증되지 않은 사용자가 (인증해야만 사용할 수 있는) 리소스에 접근 할때 동작하게 하는 인터페이스
    //사용자가 인증되지 않았을때.. 어떻게 처리할지를 구현함.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        //어떤요청인지를 구분..
        //RESTful로 요청한건지..  그냥 페이지 요청한건지 구분해서 다르게 동작하도록 구현.
        if (isRestRequest(request)) {
            handleRestResponse(request, response, exception);
        } else {
            handlePageResponse(request, response, exception);
        }
    }

    //페이지로 요청이 들어왔을 때 인증되지 않은 사용자라면 무조건 /login으로 리디렉션 시키겠다.
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {
        log.error("Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 페이지 요청에 대한 예외 처리 로직을 여기에 추가할 수 있습니다.
            response.sendRedirect("/login");
        }

    }

    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI()
            .startsWith("/api/");
    }

    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals("INVALID_TOKEN")) {
                log.error("entry point >> invalid token");
                setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                log.error("entry point >> expired token");
                setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                log.error("entry point >> unsupported token");
                setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                log.error("entry point >> not found token");
                setResponse(response, JwtErrorCode.NOT_FOUND_TOKEN);
            } else {
                setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        } else {
            setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
        }
    }

    private void setResponse(HttpServletResponse response, JwtErrorCode exceptionCode)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }
}


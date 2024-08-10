package com.fluffytime.login.jwt.token;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

// 사용자의 인증 상태와 권한 정보를 나타내는 JwtAuthenticationToken
// AbstractAuthenticationToken 클래스
// Spring Security에서 인증(Authentication)을 나타내는 추상 클래스
// 사용자 인증 요청을 처리할 때, 인증된 사용자(principal)의 정보와 사용자의 인증 상태(인증된지 여부, 권한 등)를 담고 있는 객체
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String token; // JWT 토큰
    private Object principal; // 사용자 id or 사용자 객체
    private Object credentials; // 인증을 위해 사용된 자격 증명 (비밀번호, 암호화된 토큰)

    // JWT 토큰과 함께 주체와 자격 증명 정보를 받아서 인증된 토큰을 생성하는 생성자
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities,
        Object principal, Object credentials) {
        super(authorities); // 부모 클래스의 생성자 호출
        this.principal = principal; // 주체 설정
        this.credentials = credentials; // 자격 증명 정보 설정
        this.setAuthenticated(true); // 인증 상태 설정
    }

    // JWT 토큰만을 받아서 인증되지 않은 토큰을 생성하는 생성자.
    public JwtAuthenticationToken(String token) {
        super(null); // 부모 클래스의 생성자 호출 (권한 없음)
        this.token = token; // JWT 토큰 설정
        this.setAuthenticated(false); // 인증 상태 설정 - 미 인증 상태
    }

    @Override
    public Object getCredentials() {
        // 사용자 자격증명 정보 반환
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        // 사용자(주체) 반환
        return this.principal;
    }
}

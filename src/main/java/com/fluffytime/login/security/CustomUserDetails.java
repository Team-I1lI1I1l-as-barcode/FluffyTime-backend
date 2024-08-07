package com.fluffytime.login.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 사용자의 세부 정보 구현
public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String nickname;
    //GrantedAuthority : 사용자의 권한을 나타내는 인터페이스 -> 사용자의 권한에 대한 정보를 담음
    // 해당 사용자가 가지고 있는 모든 권한을 담는 리스트
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(String email, String password, String nickname, List<String> roles) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        // GrantedAuthority의 구현체는 SimpleGrantedAuthority
        this.authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

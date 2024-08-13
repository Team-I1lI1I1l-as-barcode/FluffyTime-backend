package com.fluffytime.login.security;

import com.fluffytime.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 사용자의 세부 정보 구현
public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;
    private final String email;
    private final String password;
    @Getter
    private final String nickname;
    //GrantedAuthority : 사용자의 권한을 나타내는 인터페이스 -> 사용자의 권한에 대한 정보를 담음
    // 해당 사용자가 가지고 있는 모든 권한을 담는 리스트
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Long id ,String email, String password, String nickname, List<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        // GrantedAuthority의 구현체는 SimpleGrantedAuthority
        this.authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public CustomUserDetails(User user) {
        System.out.println("여기실행");
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        // GrantedAuthority의 구현체는 SimpleGrantedAuthority
        this.authorities = getRoles(user).stream()
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

    private List<String> getRoles(User user) {
        return user.getUserRoles().stream().map(role -> role
            .getRole()
            .getRoleName()
            .getName()
        ).toList();
    }
}

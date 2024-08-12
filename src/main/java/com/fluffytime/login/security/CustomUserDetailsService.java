package com.fluffytime.login.security;

import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.User;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자 이름을 기반으로 사용자 정보를 찾는다.
// Spring Security에서 사용자 인증을 처리하기 위해 사용자 정보를 로드하는 핵심 인터페이스
// 사용자 이름(일반적으로 사용자 ID 또는 이메일)을 기반으로 사용자 정보를 검색하는 메소드를 정의
// 이를 사용하여 데이터베이스나 다른 외부 시스템에서 사용자 정보를 가져온다.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("여기실행");
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUser::new);
        return new CustomUserDetails(user);
//
//        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(
//            email);
//        userBuilder.password(user.getPassword());
//        userBuilder.roles(user.getUserRoles().stream().map(role -> role
//            .getRole()
//            .getRoleName()
//            .getName()
//        ).toArray(String[]::new));
//
//        return userBuilder.build();
    }
}

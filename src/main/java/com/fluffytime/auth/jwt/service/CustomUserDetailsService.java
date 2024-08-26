package com.fluffytime.auth.jwt.service;

import com.fluffytime.auth.jwt.dto.CustomUserDetails;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.User;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFound::new);
        return new CustomUserDetails(user);
    }
}

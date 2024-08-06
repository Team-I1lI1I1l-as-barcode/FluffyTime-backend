package com.fluffytime.login.security;

import com.fluffytime.domain.User;
import com.fluffytime.login.exception.NotFoundUser;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUser::new);

        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(
            email);
        userBuilder.password(user.getPassword());
        userBuilder.roles(user.getUserRoles().stream().map(role -> role
            .getRole()
            .getRoleName()
            .getName()
        ).toArray(String[]::new));

        return userBuilder.build();
    }
}

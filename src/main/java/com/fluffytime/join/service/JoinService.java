package com.fluffytime.join.service;

import static com.fluffytime.domain.RoleName.ROLE_USER;

import com.fluffytime.domain.Role;
import com.fluffytime.domain.User;
import com.fluffytime.domain.UserRole;
import com.fluffytime.join.common.ResponseCode;
import com.fluffytime.join.reponse.JoinResponseDto;
import com.fluffytime.join.reponse.ResponseDto;
import com.fluffytime.join.repository.RoleRepository;
import com.fluffytime.join.repository.UserRepository;
import com.fluffytime.join.request.JoinRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ResponseDto<JoinResponseDto> join(JoinRequestDto joinUser) {
        Role role = roleRepository.findByRoleName(ROLE_USER).orElse(null);

        User user = User.builder()
            .email(joinUser.getEmail())
            .password(joinUser.getPassword())
            .nickname(joinUser.getNickname())
            .build();

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        user.getUserRoles().add(userRole);
        userRepository.save(user);

        JoinResponseDto joinResult = JoinResponseDto.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();

        return ResponseDto.response(ResponseCode.JOIN_SUCCESS, joinResult);
    }
}

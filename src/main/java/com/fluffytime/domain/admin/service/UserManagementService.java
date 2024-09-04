package com.fluffytime.domain.admin.service;

import com.fluffytime.domain.admin.dto.response.UserInfoResponse;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepository userRepository;

    @Transactional
    public Page<UserInfoResponse> findAll(Pageable pageable) {
        Pageable sortedByDescId = PageRequest.of(pageable.getPageNumber(), 2, Sort.by(Direction.DESC, "registrationAt"));

        Page<User> users = userRepository.findAll(sortedByDescId);
        Page<UserInfoResponse> usersResponses = users.map(UserInfoResponse::new);

        return usersResponses;
    }
}

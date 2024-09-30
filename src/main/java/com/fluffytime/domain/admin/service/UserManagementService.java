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
    private final int PAGE_SIZE = 2;
    private final String DESC_PROPERTIES = "registrationAt";


    private final UserRepository userRepository;

    // 유저 전체 목록 불러오기 서비스 (페이징 처리)
    @Transactional
    public Page<UserInfoResponse> findAll(Pageable pageable) {
        Pageable sortedByDescId = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE, Sort.by(Direction.DESC, DESC_PROPERTIES));

        Page<User> users = userRepository.findAll(sortedByDescId);

        return users.map(UserInfoResponse::new);
    }
}

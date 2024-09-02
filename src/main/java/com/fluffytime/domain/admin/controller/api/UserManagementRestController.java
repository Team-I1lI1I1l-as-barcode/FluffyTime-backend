package com.fluffytime.domain.admin.controller.api;

import com.fluffytime.domain.admin.dto.response.UserInfoResponse;
import com.fluffytime.domain.admin.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/management")
public class UserManagementRestController {

    private final UserManagementService userManagementService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(Pageable pageable) {
        Page<UserInfoResponse> users = userManagementService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

}

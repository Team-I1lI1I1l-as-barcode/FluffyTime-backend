package com.fluffytime.domain.admin.dto.response;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.entity.UserRole;
import com.fluffytime.domain.user.entity.enums.LoginType;
import com.fluffytime.domain.user.entity.enums.RoleName;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private LoginType loginType;
    private LocalDateTime registrationAt;
    private List<String> roles;

    @Builder
    public UserInfoResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.loginType = user.getLoginType();
        this.registrationAt = user.getRegistrationAt();
        this.roles = user.getUserRoles().stream()
            .map(userRole ->
                userRole.getRole().getRoleName().getNoneHeaderName()
            ).toList();
    }

}

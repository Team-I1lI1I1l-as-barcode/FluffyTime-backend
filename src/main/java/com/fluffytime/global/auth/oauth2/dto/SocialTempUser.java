package com.fluffytime.global.auth.oauth2.dto;

import com.fluffytime.domain.user.entity.enums.LoginType;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("TempUser")
@Getter
@Setter
@NoArgsConstructor
public class SocialTempUser implements Serializable {

    @Id
    private String email;
    private LoginType loginType;

    @Builder
    public SocialTempUser(String email, LoginType loginType) {
        this.email = email;
        this.loginType = loginType;
    }
}

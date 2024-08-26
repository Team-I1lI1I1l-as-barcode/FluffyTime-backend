package com.fluffytime.auth.oauth2.dto;

import com.fluffytime.domain.LoginType;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("TempUser")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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

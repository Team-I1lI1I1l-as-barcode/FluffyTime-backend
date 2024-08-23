package com.fluffytime.user.dto;

import com.fluffytime.domain.LoginType;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("TempUser")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TempUser implements Serializable {

    @Id
    private String email;
    private String password;
    private String nickname;
    private LoginType loginType;
    private Boolean certificationStatus;

    @Builder
    public TempUser(String email, String password, String nickname, LoginType loginType,
        Boolean certificationStatus) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.certificationStatus = certificationStatus;
    }

    public void successCertification() {
        this.certificationStatus = true;
    }
}

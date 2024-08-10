package com.fluffytime.join.dto;

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
public class TempUser implements Serializable {

    @Id
    private String email;
    private String password;
    private String nickname;
    private Boolean certificationStatus;

    @Builder
    public TempUser(String email, String password, String nickname,
        Boolean certificationStatus) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.certificationStatus = certificationStatus;
    }

    public void successCertification() {
        this.certificationStatus = true;
    }
}

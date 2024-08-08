package com.fluffytime.join.service;

import static com.fluffytime.domain.RoleName.ROLE_USER;
import static com.fluffytime.join.dto.response.JoinResponseCode.JOIN_SUCCESS;
import static com.fluffytime.join.dto.response.JoinResponseCode.NOT_DUPLICATED_EMAIL;
import static com.fluffytime.join.dto.response.JoinResponseCode.NOT_DUPLICATED_NICKNAME;
import static com.fluffytime.join.dto.response.JoinResponseCode.TEMP_JOIN_SUCCESS;

import com.fluffytime.common.exception.global.NotFoundRoleName;
import com.fluffytime.domain.Role;
import com.fluffytime.domain.User;
import com.fluffytime.domain.UserRole;
import com.fluffytime.join.dao.EmailCertificationDao;
import com.fluffytime.join.dto.TempUser;
import com.fluffytime.join.dto.request.JoinRequest;
import com.fluffytime.join.dto.response.ApiResponse;
import com.fluffytime.join.dto.response.CheckDuplicationResponse;
import com.fluffytime.join.dto.response.JoinResponse;
import com.fluffytime.join.exception.AlreadyExistsEmail;
import com.fluffytime.join.exception.AlreadyExistsNickname;
import com.fluffytime.join.exception.InvalidTempUser;
import com.fluffytime.join.exception.NotFoundTempUser;
import com.fluffytime.repository.RoleRepository;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailCertificationDao emailCertificationDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public JoinResponse tempJoin(JoinRequest joinUser) {
        TempUser tempUser = TempUser.builder()
            .email(joinUser.getEmail())
            .password(bCryptPasswordEncoder.encode(joinUser.getPassword()))
            .nickname(joinUser.getNickname())
            .certificationStatus(false)
            .build();

        emailCertificationDao.saveEmailCertificationTempUser(tempUser);

        return JoinResponse.builder()
            .email(tempUser.getEmail())
            .nickname(tempUser.getNickname())
            .build();
    }

    @Transactional
    public JoinResponse join(String email) {

        TempUser tempUser = emailCertificationDao.getTempUser(email)
            .orElseThrow(NotFoundTempUser::new);

        if (!tempUser.getCertificationStatus()) {
            throw new InvalidTempUser();
        }

        Role role = roleRepository.findByRoleName(ROLE_USER).orElseThrow(NotFoundRoleName::new);

        User user = User.builder()
            .email(tempUser.getEmail())
            .password(tempUser.getPassword())
            .nickname(tempUser.getNickname())
            .build();

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        user.getUserRoles().add(userRole);

        userRepository.save(user);

        emailCertificationDao.removeTempUser(email);

        return JoinResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();
    }

    @Transactional(readOnly = true)
    public CheckDuplicationResponse checkExistsEmail(
        String email) {
        boolean isExists = userRepository.findByEmail(email).isPresent();
        if (isExists) {
            throw new AlreadyExistsEmail();
        }
        return CheckDuplicationResponse.builder()
                .isExists(false)
                .build();
    }

    @Transactional(readOnly = true)
    public CheckDuplicationResponse checkExistsNickname(
        String nickname) {
        boolean isExists = userRepository.findByNickname(nickname).isPresent();
        if (isExists) {
            throw new AlreadyExistsNickname();
        }
        return CheckDuplicationResponse.builder()
            .isExists(false)
            .build();
    }
}

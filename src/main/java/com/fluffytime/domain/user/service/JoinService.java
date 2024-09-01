package com.fluffytime.domain.user.service;

import static com.fluffytime.domain.user.entity.enums.RoleName.ROLE_USER;

import com.fluffytime.domain.admin.components.AdminSseEmitters;
import com.fluffytime.domain.notification.service.SseEmitters;
import com.fluffytime.global.auth.oauth2.dao.SocialTempUserDao;
import com.fluffytime.global.auth.oauth2.dto.SocialTempUser;
import com.fluffytime.global.common.exception.global.RoleNameNotFound;
import com.fluffytime.domain.user.entity.enums.LoginType;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.Role;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.entity.UserRole;

import com.fluffytime.domain.user.repository.RoleRepository;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.domain.user.dao.EmailCertificationDao;
import com.fluffytime.domain.user.dto.redis.TempUser;
import com.fluffytime.domain.user.dto.request.JoinRequest;
import com.fluffytime.domain.user.dto.response.CheckDuplicationResponse;
import com.fluffytime.domain.user.dto.response.JoinResponse;
import com.fluffytime.domain.user.dto.response.SucceedCertificationResponse;
import com.fluffytime.domain.user.exception.AlreadyExistsEmail;
import com.fluffytime.domain.user.exception.AlreadyExistsNickname;
import com.fluffytime.domain.user.exception.InvalidTempUser;
import com.fluffytime.domain.user.exception.TempUserNotFound;
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
    private final SocialTempUserDao socialTempUserDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public JoinResponse tempJoin(JoinRequest joinUser) {
        TempUser tempUser = TempUser.builder()
            .email(joinUser.getEmail())
            .password(bCryptPasswordEncoder.encode(joinUser.getPassword()))
            .nickname(joinUser.getNickname())
            .loginType(LoginType.Regular)
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
            .orElseThrow(TempUserNotFound::new);

        if (!tempUser.getCertificationStatus()) {
            throw new InvalidTempUser();
        }

        Role role = roleRepository.findByRoleName(ROLE_USER).orElseThrow(RoleNameNotFound::new);

        Profile basicProfile = new Profile("none", Long.valueOf(0), "none");

        User user = User.builder()
            .email(tempUser.getEmail())
            .password(tempUser.getPassword())
            .nickname(tempUser.getNickname())
            .loginType(tempUser.getLoginType())
            .profile(basicProfile)
            .build();

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        user.getUserRoles().add(userRole);

        basicProfile.setUser(user);

        userRepository.save(user);

        emailCertificationDao.removeTempUser(email);

        return JoinResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();
    }

    @Transactional
    public JoinResponse socialJoin(JoinRequest joinUser) {

        SocialTempUser tempUser = socialTempUserDao.getSocialTempUser(joinUser.getEmail())
            .orElseThrow(TempUserNotFound::new);

        Role role = roleRepository.findByRoleName(ROLE_USER).orElseThrow(RoleNameNotFound::new);

        User user = User.builder()
            .email(tempUser.getEmail())
            .password(bCryptPasswordEncoder.encode(joinUser.getPassword()))
            .nickname(joinUser.getNickname())
            .loginType(tempUser.getLoginType())
            .build();

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        user.getUserRoles().add(userRole);

        Profile basicProfile = new Profile("none", Long.valueOf(0), "none");

        basicProfile.setUser(user);
        user.setProfile(basicProfile);

        userRepository.save(user);

        socialTempUserDao.removeSocialTempUser(joinUser.getEmail());

        return JoinResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    // 인증 성공 or 실패 응답을 구현해야함
    public SucceedCertificationResponse certificateEmail(String email) {
        TempUser user = emailCertificationDao.getTempUser(email)
            .orElseThrow(TempUserNotFound::new);
        user.successCertification();
        emailCertificationDao.saveEmailCertificationTempUser(user);

        return SucceedCertificationResponse.builder()
            .email(email)
            .build();
    }
}

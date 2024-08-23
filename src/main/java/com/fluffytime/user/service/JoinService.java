package com.fluffytime.user.service;

import static com.fluffytime.domain.RoleName.ROLE_USER;

import com.fluffytime.auth.oauth2.dao.SocialTempUserDao;
import com.fluffytime.auth.oauth2.dto.SocialTempUser;
import com.fluffytime.common.exception.global.RoleNameNotFound;
import com.fluffytime.domain.LoginType;
import com.fluffytime.domain.Profile;
import com.fluffytime.domain.Role;
import com.fluffytime.domain.User;
import com.fluffytime.domain.UserRole;

import com.fluffytime.repository.RoleRepository;
import com.fluffytime.repository.UserRepository;
import com.fluffytime.user.dao.EmailCertificationDao;
import com.fluffytime.user.dto.TempUser;
import com.fluffytime.user.dto.request.JoinRequest;
import com.fluffytime.user.dto.response.CheckDuplicationResponse;
import com.fluffytime.user.dto.response.JoinResponse;
import com.fluffytime.user.exception.AlreadyExistsEmail;
import com.fluffytime.user.exception.AlreadyExistsNickname;
import com.fluffytime.user.exception.InvalidTempUser;
import com.fluffytime.user.exception.TempUserNotFound;
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

        User user = User.builder()
            .email(tempUser.getEmail())
            .password(tempUser.getPassword())
            .nickname(tempUser.getNickname())
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
            .password(joinUser.getPassword())
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

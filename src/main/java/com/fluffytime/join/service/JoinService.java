package com.fluffytime.join.service;

import static com.fluffytime.domain.RoleName.ROLE_USER;
import static com.fluffytime.join.dto.reponse.ResponseCode.JOIN_SUCCESS;
import static com.fluffytime.join.dto.reponse.ResponseCode.NOT_DUPLICATED_EMAIL;
import static com.fluffytime.join.dto.reponse.ResponseCode.NOT_DUPLICATED_NICKNAME;
import static com.fluffytime.join.dto.reponse.ResponseCode.TEMP_JOIN_SUCCESS;

import com.fluffytime.domain.Role;
import com.fluffytime.domain.User;
import com.fluffytime.domain.UserRole;
import com.fluffytime.join.dto.TempUser;
import com.fluffytime.join.dto.reponse.ApiResponse;
import com.fluffytime.join.dto.reponse.ExistsAccountResponse;
import com.fluffytime.join.dto.reponse.JoinResponse;
import com.fluffytime.join.dto.request.JoinRequest;
import com.fluffytime.join.exception.AlreadyExistsEmail;
import com.fluffytime.join.exception.AlreadyExistsNickname;
import com.fluffytime.join.exception.InvalidTempUser;
import com.fluffytime.join.exception.RoleNameNotFound;
import com.fluffytime.join.exception.TempUserNotFound;
import com.fluffytime.join.repository.RoleRepository;
import com.fluffytime.join.repository.UserRepository;
import com.fluffytime.join.repository.dao.EmailCertificationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailCertificationDao emailCertificationDao;
    private final CertificationService certificationService;

    @Transactional
    public ApiResponse<JoinResponse> tempJoin(JoinRequest joinUser) {

        TempUser tempUser = TempUser.builder()
            .email(joinUser.getEmail())
            .password(joinUser.getPassword())
            .nickname(joinUser.getNickname())
            .certificationStatus(false)
            .build();

        emailCertificationDao.saveEmailCertificationTempUser(tempUser);

        JoinResponse joinResult = JoinResponse.builder()
            .email(tempUser.getEmail())
            .nickname(tempUser.getNickname())
            .build();

        return ApiResponse.response(TEMP_JOIN_SUCCESS, joinResult);
    }

    @Transactional
    public ApiResponse<JoinResponse> join(String email) {

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
            .build();

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        user.getUserRoles().add(userRole);

        userRepository.save(user);

        emailCertificationDao.removeTempUser(email);

        JoinResponse joinResult = JoinResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();

        return ApiResponse.response(JOIN_SUCCESS, joinResult);
    }

    @Transactional(readOnly = true)
    public ApiResponse<ExistsAccountResponse> checkExistsEmail(
        String email) {
        boolean isExists = userRepository.findByEmail(email).isPresent();
        if (isExists) {
            throw new AlreadyExistsEmail();
        }
        return ApiResponse.response(NOT_DUPLICATED_EMAIL);
    }

    @Transactional(readOnly = true)
    public ApiResponse<ExistsAccountResponse> checkExistsNickname(
        String nickname) {
        boolean isExists = userRepository.findByNickname(nickname).isPresent();
        if (isExists) {
            throw new AlreadyExistsNickname();
        }
        return ApiResponse.response(NOT_DUPLICATED_NICKNAME);
    }
}

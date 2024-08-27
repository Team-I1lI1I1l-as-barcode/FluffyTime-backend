package com.fluffytime.global.auth.oauth2.service;

import com.fluffytime.global.auth.oauth2.dao.SocialTempUserDao;
import com.fluffytime.global.auth.oauth2.dto.CustomOAuth2User;
import com.fluffytime.global.auth.oauth2.dto.SocialTempUser;
import com.fluffytime.global.auth.oauth2.dto.UserDto;
import com.fluffytime.global.auth.oauth2.response.GoogleResponse;
import com.fluffytime.global.auth.oauth2.response.NaverResponse;
import com.fluffytime.global.auth.oauth2.response.Oauth2Response;
import com.fluffytime.domain.user.entity.enums.LoginType;
import com.fluffytime.domain.user.entity.enums.RoleName;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialTempUserDao socialTempUserDao;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2Response oAuth2Response = null;
        if(registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            log.info("naver response = {}", oAuth2Response);
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            log.info("google response = {}", oAuth2Response);
        } else {
            return null;
        }

        User existUser = userRepository.findByEmail(oAuth2Response.getEmail()).orElse(null);

        if (existUser == null) {
            SocialTempUser tempUser = SocialTempUser.builder()
                .email(oAuth2Response.getEmail())
                .loginType(LoginType.Social)
                .build();
            socialTempUserDao.saveSocialTempUser(oAuth2Response.getEmail(),tempUser);

            UserDto userDto = UserDto.builder()
                .id(null)
                .email(oAuth2Response.getEmail())
                .nickname(null)
                .roles(List.of(RoleName.ROLE_USER.getName()))
                .build();

            return new CustomOAuth2User(userDto);
        } else {
            log.info("이미 있는 유저");
            UserDto userDto = UserDto.builder()
                .id(existUser.getUserId())
                .email(oAuth2Response.getEmail())
                .nickname(existUser.getNickname())
                .roles(getRoles(existUser))
                .build();

            return new CustomOAuth2User(userDto);
        }
    }

    private List<String> getRoles(User user) {
        return user.getUserRoles().stream().map(role -> role
            .getRole()
            .getRoleName()
            .getName()
        ).toList();
    }
}

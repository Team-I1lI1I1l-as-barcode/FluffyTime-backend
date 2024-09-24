package com.fluffytime.global.auth.oauth2.response;

import static com.fluffytime.global.auth.oauth2.util.constants.RegistrationId.NAVER_ID;

import java.util.Map;

public class NaverResponse implements Oauth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return NAVER_ID.getId();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

}

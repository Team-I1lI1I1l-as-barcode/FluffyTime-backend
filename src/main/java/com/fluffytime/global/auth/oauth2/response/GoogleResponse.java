package com.fluffytime.global.auth.oauth2.response;

import static com.fluffytime.global.auth.oauth2.util.constants.RegistrationId.GOOGLE_ID;

import java.util.Map;

public class GoogleResponse implements Oauth2Response{

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    @Override
    public String getProvider() {
        return GOOGLE_ID.getId();
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
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

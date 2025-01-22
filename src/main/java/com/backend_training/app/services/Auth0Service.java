package com.backend_training.app.services;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import com.backend_training.app.dto.Auth0Config;
import com.backend_training.app.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class Auth0Service {

    private final AuthAPI authAPI;
    private final ManagementAPI managementAPI;
    private final String apiIdentifier;

    @Autowired
    public Auth0Service(Auth0Config auth0Config) {
        AuthAPI.Builder authBuilder = AuthAPI.newBuilder(auth0Config.getDomain(), auth0Config.getClientId(), auth0Config.getClientSecret());
        ManagementAPI.Builder builder =  ManagementAPI.newBuilder(auth0Config.getDomain(), auth0Config.getManagementApiToken());

        this.authAPI = authBuilder.build();
        this.managementAPI = builder.build();
        this.apiIdentifier = auth0Config.getApiIdentifier();
    }

    public void registerUser(User userDTO) throws Auth0Exception {
        com.auth0.json.mgmt.users.User user = new com.auth0.json.mgmt.users.User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword().toCharArray());
        user.setConnection("Username-Password-Authentication");
        user.setId(UUID.randomUUID().toString());
        user.setUsername(userDTO.getUserName());
        Response<com.auth0.json.mgmt.users.User> response = managementAPI.users().create(user).execute();
    }


    public String getAccessToken(String email, String password) throws Auth0Exception {
        TokenRequest authRequest = authAPI
                .login(email, password.toCharArray(), "Username-Password-Authentication")
                .setAudience(apiIdentifier);

        Response<TokenHolder> holder = authRequest.execute();
        return holder.getBody().getAccessToken();
    }
}

package com.backend_training.app.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth0")
public class Auth0Config {
    private String domain;
    private String clientId;
    private String clientSecret;
    private String apiIdentifier;
}
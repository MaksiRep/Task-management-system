package ru.maksirep.config.authorization;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
@Component
public class AuthorizeConfig {

    private Duration expirationTime;
    private String secret;

    public Duration getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Duration expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

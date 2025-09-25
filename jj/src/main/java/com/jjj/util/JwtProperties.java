package com.jjj.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private int accessExpirationMinutes = 30;
    private int refreshExpirationDays = 7;
}

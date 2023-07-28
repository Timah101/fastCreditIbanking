package com.iBanking.iBanking.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fast-credit")
public record FastCreditConfig(String userName, String password, String secretKey) {
}

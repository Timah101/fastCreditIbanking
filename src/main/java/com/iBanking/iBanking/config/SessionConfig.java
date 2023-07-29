package com.iBanking.iBanking.config;

import com.iBanking.iBanking.utils.SessionAttributeListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    @Bean(name = "customSessionAttributeListener")
    public SessionAttributeListener sessionAttributeListener() {
        return new SessionAttributeListener();
    }
}


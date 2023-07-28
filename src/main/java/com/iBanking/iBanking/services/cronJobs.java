package com.iBanking.iBanking.services;


import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Configuration
@EnableScheduling
@EnableEncryptableProperties
public class cronJobs {
    @Autowired
    SendOtpService otpService;

    @Scheduled(fixedRate = 50000)
    public void testSecurity() {
        otpService.testSecretKeys();
    }

}

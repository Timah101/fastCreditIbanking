package com.iBanking.iBanking.services;


import com.google.gson.Gson;
import com.iBanking.iBanking.utils.AuthenticationApi;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableEncryptableProperties
public class CronJobs {
    @Autowired
    SendOtpService otpService;
    @Autowired
    AuthenticationApi authenticationApi;
    Gson gson = new Gson();

    @Scheduled(fixedRate = 50000)
    public void testSecurity() {
//        otpService.testSecretKeys();
        String username = "Tunde";
        String requestJson = gson.toJson(username);
//        System.out.println(requestJson);
//        final String encrypt = authenticationApi.encrypt(requestJson);
//        System.out.println(encrypt);
        String decrypt = authenticationApi.decryptPayload("F9bIVbJKWko3yZUjelRMG4FBi4to9c63IiHymOdozcoi0ymtMaFFyvULUUaBd0aDhJ6dowgFq+RYTq5TvXOBeVC4pUbQ0pOVvE8kRkFbHzc=");
//        System.out.println(decrypt + ": decrypted");
    }
}

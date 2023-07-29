package com.iBanking.iBanking.config;

import lombok.Data;
import org.springframework.stereotype.Service;


@Data
@Service
public class Configurations {
    private String tokenUsername = "$2a$12$I0ZXLYfkiCrBgkgDRWwv8eny0XBF2MXcshWeMRI6EoalUffWHFdce";
    private String tokenPassword = "$2a$12$5apfcBIyryVHUzEKEyZs1emRnE08y8pijEmcIiA9y3tnTmKhDrdzm";

    private static FastCreditConfig fastCreditConfig = null;
//    @Autowired
//    FastCreditConfig fastCreditConfig;

    public Configurations(FastCreditConfig fastCreditConfig) {
        Configurations.fastCreditConfig = fastCreditConfig;
    }

    public void mainTester() {
        String userName = fastCreditConfig.userName();
        String password = fastCreditConfig.password();
        String encryptionKey = System.getenv("password");
        System.out.println("Encryption Key: " + userName);
    }
}

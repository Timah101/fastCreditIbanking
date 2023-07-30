package com.iBanking.iBanking;


import com.iBanking.iBanking.config.FastCreditConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties(FastCreditConfig.class)
@PropertySource("classpath:application.properties")
public class IBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(IBankingApplication.class, args);
    }

}

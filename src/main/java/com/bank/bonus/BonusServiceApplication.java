package com.bank.bonus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BonusServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BonusServiceApplication.class, args);
    }
}

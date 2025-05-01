package com.sap.fsad.leaveApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.sap.fsad.leaveApp")
@EnableScheduling
@EnableRetry
public class LeaveScheduler {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("APP_JWT_SECRET", dotenv.get("APP_JWT_SECRET"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("SPRING_MAIL_USERNAME", dotenv.get("SPRING_MAIL_USERNAME"));
        System.setProperty("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
        SpringApplication.run(LeaveScheduler.class, args);
    }

}

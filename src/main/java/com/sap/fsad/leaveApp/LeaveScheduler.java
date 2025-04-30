package com.sap.fsad.leaveApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.sap.fsad.leaveApp")
@EnableScheduling
@EnableJpaAuditing
public class LeaveScheduler {

    public static void main(String[] args) {
        SpringApplication.run(LeaveScheduler.class, args);
    }

}

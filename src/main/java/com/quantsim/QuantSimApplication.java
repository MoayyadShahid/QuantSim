package com.quantsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class QuantSimApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantSimApplication.class, args);
    }
}

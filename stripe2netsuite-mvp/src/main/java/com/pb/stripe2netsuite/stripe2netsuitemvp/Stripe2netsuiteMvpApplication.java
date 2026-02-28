package com.pb.stripe2netsuite.stripe2netsuitemvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Stripe2netsuiteMvpApplication {

    public static void main(String[] args) {
        SpringApplication.run(Stripe2netsuiteMvpApplication.class, args);
    }

}

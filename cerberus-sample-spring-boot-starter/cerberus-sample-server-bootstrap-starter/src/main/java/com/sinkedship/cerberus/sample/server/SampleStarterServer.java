package com.sinkedship.cerberus.sample.server;

import com.sinkedship.cerberus.bootstrap.CerberusServerBootstrap;
import com.sinkedship.cerberus.sample.server.impl.CalculatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Derrick Guan
 */
@SpringBootApplication
public class SampleStarterServer implements CommandLineRunner {

    @Autowired
    private CerberusServerBootstrap.Builder builder;

    public static void main(String[] args) {
        SpringApplication.run(SampleStarterServer.class, args);
    }

    @Override
    public void run(String... args) {
        builder.withService(new CalculatorServiceImpl())
                .build()
                .boot();
    }
}

package com.sinkedship.cerberus.sample.client;

import com.sinkedship.cerberus.sample.api.service.CalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Derrick Guan
 */
@SpringBootApplication
public class SampleStarterClient implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(SampleStarterClient.class);

    @Autowired
    private CalculatorService calculatorService;

    public static void main(String[] args) {
        SpringApplication.run(SampleStarterClient.class, args);
    }

    @Override
    public void run(String... args) {
        int r = calculatorService.add(1, 2);
        logger.info("result:{}", r);
    }
}

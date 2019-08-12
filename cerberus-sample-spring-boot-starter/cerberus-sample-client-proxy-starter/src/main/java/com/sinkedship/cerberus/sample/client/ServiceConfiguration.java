package com.sinkedship.cerberus.sample.client;

import com.sinkedship.cerberus.client.CerberusServiceFactory;
import com.sinkedship.cerberus.sample.api.service.CalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Derrick Guan
 */
@Configuration
public class ServiceConfiguration {

    @Bean
    public CalculatorService.Async asyncCalculatorService(CerberusServiceFactory factory) {
        return factory.newService(CalculatorService.Async.class);
    }

    @Bean
    public CalculatorService calculatorService(CerberusServiceFactory factory) {
        return factory.newService(CalculatorService.class);
    }
}

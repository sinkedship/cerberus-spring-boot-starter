package com.sinkedship.cerberus.sample.server.impl;

import com.sinkedship.cerberus.sample.api.service.CalculatorService;

/**
 * @author Derrick Guan
 */
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

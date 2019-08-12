package com.sinkedship.cerberus.sample.api.service;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

/**
 * @author Derrick Guan
 */
@ThriftService
public interface CalculatorService {

    @ThriftMethod
    int add(int a, int b);

    @ThriftService
    interface Async {
        @ThriftMethod
        int add(int a, int b);
    }

}

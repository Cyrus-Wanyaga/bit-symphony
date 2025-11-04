package com.techsol.tests;

import java.util.Map;

public interface PerformanceTest {
    String getName();
    Map<String, Object> getConfiguration();
    PerformanceResult runTest(TestResourceMonitor monitor) throws Exception;
}

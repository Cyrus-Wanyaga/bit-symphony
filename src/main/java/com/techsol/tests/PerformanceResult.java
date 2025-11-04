package com.techsol.tests;

import java.util.Map;

public class PerformanceResult {
    private final String testName;
    private final long durationMillis;
    private final Map<String, Object> metrics;

    public PerformanceResult(String testName, long durationMillis, Map<String, Object> metrics) {
        this.testName = testName;
        this.durationMillis = durationMillis;
        this.metrics = metrics;
    }

    public String getTestName() {
        return testName;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "PerformanceResult [testName=" + testName + ", durationMillis=" + durationMillis + ", metrics=" + metrics
                + "]";
    }
}

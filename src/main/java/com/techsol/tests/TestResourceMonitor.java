package com.techsol.tests;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestResourceMonitor {
    private long startUsedMemory;
    private long endUsedMemory;
    private long startTime;

    public void start() {
        System.gc();
        startUsedMemory = getUsedMemory();
        startTime = System.nanoTime();
    }

    public void stop() {
        endUsedMemory = getUsedMemory();
    }

    private long getUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("memoryUsedBytes", endUsedMemory - startUsedMemory);
        metrics.put("cpuLoad", getCpuLoad());
        return metrics;
    }

    private double getCpuLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean mBean) {
            return mBean.getProcessCpuLoad();
        }

        return -1.0;
    }
}
package com.techsol.models;

public class TestResult {
    private int id;
    private String testName;
    private String algorithmName;
    private String startTime;
    private String endTime;
    private double durationMs;
    private double cpuUsage;
    private long memoryUsage;
    private double diskIO;
    private String extraInfo;
    private int sessionId;

    public TestResult() {
    }

    public TestResult(String testName, String algorithmName, String startTime, String endTime,
            double durationMs, double cpuUsage, long memoryUsage, double diskIO, String extraInfo, int sessionId) {
        this.testName = testName;
        this.algorithmName = algorithmName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMs = durationMs;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskIO = diskIO;
        this.extraInfo = extraInfo;
        this.sessionId = sessionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(double durationMs) {
        this.durationMs = durationMs;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getDiskIO() {
        return diskIO;
    }

    public void setDiskIO(double diskIO) {
        this.diskIO = diskIO;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

}
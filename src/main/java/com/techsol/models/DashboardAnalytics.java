package com.techsol.models;

public class DashboardAnalytics {
    private int totalTestCases;
    private int totalTestFiles;
    private long totalTestFileSize;
    private long totalTestTime;
    private int averageTestTime;
    private int longestTestTime;
    private int shortestTestTime;

    public DashboardAnalytics() {
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getTotalTestFiles() {
        return totalTestFiles;
    }

    public void setTotalTestFiles(int totalTestFiles) {
        this.totalTestFiles = totalTestFiles;
    }

    public long getTotalTestFileSize() {
        return totalTestFileSize;
    }

    public void setTotalTestFileSize(long totalTestFileSize) {
        this.totalTestFileSize = totalTestFileSize;
    }

    public long getTotalTestTime() {
        return totalTestTime;
    }

    public void setTotalTestTime(long totalTestTime) {
        this.totalTestTime = totalTestTime;
    }

    public int getAverageTestTime() {
        return averageTestTime;
    }

    public void setAverageTestTime(int averageTestTime) {
        this.averageTestTime = averageTestTime;
    }

    public int getLongestTestTime() {
        return longestTestTime;
    }

    public void setLongestTestTime(int longestTestTime) {
        this.longestTestTime = longestTestTime;
    }

    public int getShortestTestTime() {
        return shortestTestTime;
    }

    public void setShortestTestTime(int shortestTestTime) {
        this.shortestTestTime = shortestTestTime;
    }

    @Override
    public String toString() {
        return "DashboardAnalytics [totalTestCases=" + totalTestCases + ", totalTestFiles=" + totalTestFiles
                + ", totalTestFileSize=" + totalTestFileSize + ", totalTestTime=" + totalTestTime + ", averageTestTime="
                + averageTestTime + ", longestTestTime=" + longestTestTime + ", shortestTestTime=" + shortestTestTime
                + "]";
    }

    
}

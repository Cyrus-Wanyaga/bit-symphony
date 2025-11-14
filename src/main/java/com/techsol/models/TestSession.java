package com.techsol.models;

import java.util.Date;

public class TestSession {
    private int id;
    private String testGroup;
    private String createdAt;
    private int totalRuns;
    private String description;

    public TestSession() {
    }

    public TestSession(String testGroup, String createdAt, int totalRuns, String description) {        
        this.testGroup = testGroup;
        this.totalRuns = totalRuns;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

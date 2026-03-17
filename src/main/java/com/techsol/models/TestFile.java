package com.techsol.models;

import java.util.Date;

public class TestFile {
    private int id;
    private String fileName;
    private String filePath;
    private int fileExists;
    private long fileSizeInBytes;
    private long numberOfItems;
    private String dataType;
    private String createdAt;
    private String updatedAt;

    public TestFile() {
    }

    public TestFile(String fileName, String filePath, int fileExists, long fileSizeInBytes, long numberOfItems,
            String dataType,
            String createdAt,
            String updatedAt) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileExists = fileExists;
        this.fileSizeInBytes = fileSizeInBytes;
        this.numberOfItems = numberOfItems;
        this.dataType = dataType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileExists() {
        return fileExists;
    }

    public void setFileExists(int fileExists) {
        this.fileExists = fileExists;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public long getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(long numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

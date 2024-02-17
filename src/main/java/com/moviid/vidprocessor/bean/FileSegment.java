package com.moviid.vidprocessor.bean;

public class FileSegment {
    private String filePath;
    private int segmentNumber;
    private long startTimestamp;
    private long endTimestamp;

    // Constructor
    public FileSegment(String filePath, int segmentNumber, long startTimestamp, long endTimestamp) {
        this.filePath = filePath;
        this.segmentNumber = segmentNumber;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    // toString method for displaying object information
    @Override
    public String toString() {
        return "FileSegment{" +
                "filePath='" + filePath + '\'' +
                ", segmentNumber=" + segmentNumber +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }
}

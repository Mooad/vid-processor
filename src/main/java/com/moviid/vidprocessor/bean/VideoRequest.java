package com.moviid.vidprocessor.bean;

public class VideoRequest {
    private String path;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    private String bucket;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
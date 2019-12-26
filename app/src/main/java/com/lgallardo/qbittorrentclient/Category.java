package com.lgallardo.qbittorrentclient;

public class Category {

    private String name;
    private String savePath;


    public Category(String name, String savePath) {
        this.name = name;
        this.savePath = savePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}

package com.lgallardo.qbittorrentclient;

/**
 * Created by lgallard on 2/23/18.
 */

public class Api {

    String apiVersion;

    public Api(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}

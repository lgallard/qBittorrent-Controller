package com.lgallardo.qbittorrentclient;

/**
 * Created by lgallard on 2/23/18.
 */

public class Api {

    String apiversion;

    public Api(String apiversion) {
        this.apiversion = apiversion;
    }

    public String getApiversion() {
        return apiversion;
    }

    public void setApiversion(String apiversion) {
        this.apiversion = apiversion;
    }
}

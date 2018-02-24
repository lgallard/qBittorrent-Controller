package com.lgallardo.qbittorrentclient;

import org.json.JSONObject;

/**
 * Created by lgallard on 2/24/18.
 */

public class CustomObjectResult {

    String data;
    String headers;

    public CustomObjectResult(String data, String headers) {
        this.data = data;
        this.headers = headers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}

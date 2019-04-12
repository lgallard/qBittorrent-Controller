package com.lgallardo.qbittorrentclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lgallard on 2/24/18.
 */

public class CustomObjectResult {
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("headers")
    @Expose
    private String headers;

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

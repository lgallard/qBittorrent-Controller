package com.lgallardo.qbittorrentclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferInfo {

    // New format
    @SerializedName("dl_info_speed")
    @Expose
    private long dl_info_speed;
    @SerializedName("dl_info_data")
    @Expose
    private long dl_info_data;
    @SerializedName("up_info_speed")
    @Expose
    private long up_info_speed;
    @SerializedName("up_info_data")
    @Expose
    private long up_info_data;
    @SerializedName("dl_rate_limit")
    @Expose
    private long dl_rate_limit;
    @SerializedName("up_rate_limit")
    @Expose
    private long up_rate_limit;
    @SerializedName("dht_nodes")
    @Expose
    private long dht_nodes;
    @SerializedName("connection_status")
    @Expose
    private String  connection_status;


    public long getDl_info_speed() {
        return dl_info_speed;
    }

    public void setDl_info_speed(long dl_info_speed) {
        this.dl_info_speed = dl_info_speed;
    }

    public long getDl_info_data() {
        return dl_info_data;
    }

    public void setDl_info_data(long dl_info_data) {
        this.dl_info_data = dl_info_data;
    }

    public long getUp_info_speed() {
        return up_info_speed;
    }

    public void setUp_info_speed(long up_info_speed) {
        this.up_info_speed = up_info_speed;
    }

    public long getUp_info_data() {
        return up_info_data;
    }

    public void setUp_info_data(long up_info_data) {
        this.up_info_data = up_info_data;
    }

    public long getDl_rate_limit() {
        return dl_rate_limit;
    }

    public void setDl_rate_limit(long dl_rate_limit) {
        this.dl_rate_limit = dl_rate_limit;
    }

    public long getUp_rate_limit() {
        return up_rate_limit;
    }

    public void setUp_rate_limit(long up_rate_limit) {
        this.up_rate_limit = up_rate_limit;
    }

    public long getDht_nodes() {
        return dht_nodes;
    }

    public void setDht_nodes(long dht_nodes) {
        this.dht_nodes = dht_nodes;
    }

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }
}

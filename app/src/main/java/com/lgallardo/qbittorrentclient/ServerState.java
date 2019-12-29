package com.lgallardo.qbittorrentclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerState {

    @SerializedName("alltime_dl")
    @Expose
    private double alltime_dl;
    @SerializedName("alltime_ul")
    @Expose
    private double alltime_ul;
    @SerializedName("average_time_queue")
    @Expose
    private int average_time_queue;
    @SerializedName("connection_status")
    @Expose
    private String connection_status;
    @SerializedName("dht_nodes")
    @Expose
    private int dht_nodes;
    @SerializedName("dl_info_data")
    @Expose
    private double dl_info_data;
    @SerializedName("dl_info_speed")
    @Expose
    private double dl_info_speed;
    @SerializedName("dl_rate_limit")
    @Expose
    private int dl_rate_limit;
    @SerializedName("free_space_on_disk")
    @Expose
    private double free_space_on_disk;
    @SerializedName("global_ratio")
    @Expose
    private float global_ratio;
    @SerializedName("queued_io_jobs")
    @Expose
    private int queued_io_jobs;
    @SerializedName("queueing")
    @Expose
    private boolean queueing;
    @SerializedName("read_cache_hits")
    @Expose
    private int read_cache_hits;
    @SerializedName("read_cache_overload")
    @Expose
    private int read_cache_overload;
    @SerializedName("refresh_interval")
    @Expose
    private int refresh_interval;
    @SerializedName("total_buffers_size")
    @Expose
    private int total_buffers_size;
    @SerializedName("total_peer_connections")
    @Expose
    private int total_peer_connections;
    @SerializedName("total_queued_size")
    @Expose
    private double total_queued_size;
    @SerializedName("total_wasted_session")
    @Expose
    private double total_wasted_session;
    @SerializedName("up_info_data")
    @Expose
    private double up_info_data;
    @SerializedName("up_info_speed")
    @Expose
    private double up_info_speed;
    @SerializedName("up_rate_limit")
    @Expose
    private int up_rate_limit;
    @SerializedName("use_alt_speed_limits")
    @Expose
    private boolean use_alt_speed_limits;
    @SerializedName("write_cache_overload")
    @Expose
    private int write_cache_overload;

    public double getAlltime_dl() {
        return alltime_dl;
    }

    public void setAlltime_dl(double allup_rate_limittime_dl) {
        this.alltime_dl = alltime_dl;
    }

    public double getAlltime_ul() {
        return alltime_ul;
    }

    public void setAlltime_ul(double alltime_ul) {
        this.alltime_ul = alltime_ul;
    }

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }

    public int getDht_nodes() {
        return dht_nodes;
    }

    public void setDht_nodes(int dht_nodes) {
        this.dht_nodes = dht_nodes;
    }

    public double getDl_info_data() {
        return dl_info_data;
    }

    public void setDl_info_data(double dl_info_data) {
        this.dl_info_data = dl_info_data;
    }

    public double getDl_info_speed() {
        return dl_info_speed;
    }

    public void setDl_info_speed(double dl_info_speed) {
        this.dl_info_speed = dl_info_speed;
    }

    public int getDl_rate_limit() {
        return dl_rate_limit;
    }

    public void setDl_rate_limit(int dl_rate_limit) {
        this.dl_rate_limit = dl_rate_limit;
    }

    public double getFree_space_on_disk() {
        return free_space_on_disk;
    }

    public void setFree_space_on_disk(double free_space_on_disk) {
        this.free_space_on_disk = free_space_on_disk;
    }

    public float getGlobal_ratio() {
        return global_ratio;
    }

    public void setGlobal_ratio(float global_ratio) {
        this.global_ratio = global_ratio;
    }

    public double getUp_info_data() {
        return up_info_data;
    }

    public void setUp_info_data(double up_info_data) {
        this.up_info_data = up_info_data;
    }

    public double getUp_info_speed() {
        return up_info_speed;
    }

    public void setUp_info_speed(double up_info_speed) {
        this.up_info_speed = up_info_speed;
    }

    public int getUp_rate_limit() {
        return up_rate_limit;
    }

    public void setUp_rate_limit(int up_rate_limit) {
        this.up_rate_limit = up_rate_limit;
    }

    public boolean isUse_alt_speed_limits() {
        return use_alt_speed_limits;
    }

    public void setUse_alt_speed_limits(boolean use_alt_speed_limits) {
        this.use_alt_speed_limits = use_alt_speed_limits;
    }

    public int getAverage_time_queue() {
        return average_time_queue;
    }

    public void setAverage_time_queue(int average_time_queue) {
        this.average_time_queue = average_time_queue;
    }

    public int getQueued_io_jobs() {
        return queued_io_jobs;
    }

    public void setQueued_io_jobs(int queued_io_jobs) {
        this.queued_io_jobs = queued_io_jobs;
    }

    public boolean isQueueing() {
        return queueing;
    }

    public void setQueueing(boolean queueing) {
        this.queueing = queueing;
    }

    public int getRead_cache_hits() {
        return read_cache_hits;
    }

    public void setRead_cache_hits(int read_cache_hits) {
        this.read_cache_hits = read_cache_hits;
    }

    public int getRead_cache_overload() {
        return read_cache_overload;
    }

    public void setRead_cache_overload(int read_cache_overload) {
        this.read_cache_overload = read_cache_overload;
    }

    public int getRefresh_interval() {
        return refresh_interval;
    }

    public void setRefresh_interval(int refresh_interval) {
        this.refresh_interval = refresh_interval;
    }

    public int getTotal_buffers_size() {
        return total_buffers_size;
    }

    public void setTotal_buffers_size(int total_buffers_size) {
        this.total_buffers_size = total_buffers_size;
    }

    public int getTotal_peer_connections() {
        return total_peer_connections;
    }

    public void setTotal_peer_connections(int total_peer_connections) {
        this.total_peer_connections = total_peer_connections;
    }

    public double getTotal_queued_size() {
        return total_queued_size;
    }

    public void setTotal_queued_size(double total_queued_size) {
        this.total_queued_size = total_queued_size;
    }

    public double getTotal_wasted_session() {
        return total_wasted_session;
    }

    public void setTotal_wasted_session(double total_wasted_session) {
        this.total_wasted_session = total_wasted_session;
    }

    public int getWrite_cache_overload() {
        return write_cache_overload;
    }

    public void setWrite_cache_overload(int write_cache_overload) {
        this.write_cache_overload = write_cache_overload;
    }
}

package com.lgallardo.qbittorrentclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lgallard on 6/11/18.
 */

public class GeneralInfo {

    // New format
    @SerializedName("save_path")
    @Expose
    private String save_path;
    @SerializedName("creation_date")
    @Expose
    private long creation_date;
    @SerializedName("piece_size")
    @Expose
    private long piece_size;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("total_wasted")
    @Expose
    private long total_wasted;

    @SerializedName("total_uploaded")
    @Expose
    private long total_uploaded;
    @SerializedName("total_uploaded_session")
    @Expose
    private long total_uploaded_session;
    @SerializedName("total_downloaded")
    @Expose
    private long total_downloaded;
    @SerializedName("total_downloaded_session")
    @Expose
    private long total_downloaded_session;
    @SerializedName("up_limit")
    @Expose
    private long up_limit;
    @SerializedName("dl_limit")
    @Expose
    private long dl_limit;
    @SerializedName("time_elapsed")
    @Expose
    private long time_elapsed;
    @SerializedName("seeding_time")
    @Expose
    private long seeding_time;
    @SerializedName("nb_connections")
    @Expose
    private long nb_connections;
    @SerializedName("nb_connections_limit")
    @Expose
    private long nb_connections_limit;
    @SerializedName("share_ratio")
    @Expose
    private float share_ratio;
    @SerializedName("addition_date")
    @Expose
    private long addition_date;
    @SerializedName("completion_date")
    @Expose
    private long completion_date;
    @SerializedName("created_by")
    @Expose
    private String created_by;
    @SerializedName("dl_speed_avg")
    @Expose
    private long dl_speed_avg;
    @SerializedName("dl_speed")
    @Expose
    private long dl_speed;
    @SerializedName("eta")
    @Expose
    private long eta;
    @SerializedName("last_seen")
    @Expose
    private long last_seen;
    @SerializedName("peers")
    @Expose
    private long peers;
    @SerializedName("peers_total")
    @Expose
    private long peers_total;
    @SerializedName("pieces_have")
    @Expose
    private long pieces_have;
    @SerializedName("pieces_num")
    @Expose
    private long pieces_num;
    @SerializedName("reannounce")
    @Expose
    private long reannounce;
    @SerializedName("seeds")
    @Expose
    private long seeds;
    @SerializedName("seeds_total")
    @Expose
    private long seeds_total;
    @SerializedName("total_size")
    @Expose
    private long total_size;
    @SerializedName("up_speed_avg")
    @Expose
    private long up_speed_avg;
    @SerializedName("up_speed")
    @Expose
    private long up_speed;

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public long getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(long creation_date) {
        this.creation_date = creation_date;
    }

    public long getPiece_size() {
        return piece_size;
    }

    public void setPiece_size(long piece_size) {
        this.piece_size = piece_size;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTotal_wasted() {
        return total_wasted;
    }

    public void setTotal_wasted(long total_wasted) {
        this.total_wasted = total_wasted;
    }

    public long getTotal_uploaded() {
        return total_uploaded;
    }

    public void setTotal_uploaded(long total_uploaded) {
        this.total_uploaded = total_uploaded;
    }

    public long getTotal_uploaded_session() {
        return total_uploaded_session;
    }

    public void setTotal_uploaded_session(long total_uploaded_session) {
        this.total_uploaded_session = total_uploaded_session;
    }

    public long getTotal_downloaded() {
        return total_downloaded;
    }

    public void setTotal_downloaded(long total_downloaded) {
        this.total_downloaded = total_downloaded;
    }

    public long getTotal_downloaded_session() {
        return total_downloaded_session;
    }

    public void setTotal_downloaded_session(long total_downloaded_session) {
        this.total_downloaded_session = total_downloaded_session;
    }

    public long getUp_limit() {
        return up_limit;
    }

    public void setUp_limit(long up_limit) {
        this.up_limit = up_limit;
    }

    public long getDl_limit() {
        return dl_limit;
    }

    public void setDl_limit(long dl_limit) {
        this.dl_limit = dl_limit;
    }

    public long getTime_elapsed() {
        return time_elapsed;
    }

    public void setTime_elapsed(long time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public long getSeeding_time() {
        return seeding_time;
    }

    public void setSeeding_time(long seeding_time) {
        this.seeding_time = seeding_time;
    }

    public long getNb_connections() {
        return nb_connections;
    }

    public void setNb_connections(long nb_connections) {
        this.nb_connections = nb_connections;
    }

    public long getNb_connections_limit() {
        return nb_connections_limit;
    }

    public void setNb_connections_limit(long nb_connections_limit) {
        this.nb_connections_limit = nb_connections_limit;
    }

    public float getShare_ratio() {
        return share_ratio;
    }

    public void setShare_ratio(float share_ratio) {
        this.share_ratio = share_ratio;
    }

    public long getAddition_date() {
        return addition_date;
    }

    public void setAddition_date(long addition_date) {
        this.addition_date = addition_date;
    }

    public long getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(long completion_date) {
        this.completion_date = completion_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public long getDl_speed_avg() {
        return dl_speed_avg;
    }

    public void setDl_speed_avg(long dl_speed_avg) {
        this.dl_speed_avg = dl_speed_avg;
    }

    public long getDl_speed() {
        return dl_speed;
    }

    public void setDl_speed(long dl_speed) {
        this.dl_speed = dl_speed;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public long getPeers() {
        return peers;
    }

    public void setPeers(long peers) {
        this.peers = peers;
    }

    public long getPeers_total() {
        return peers_total;
    }

    public void setPeers_total(long peers_total) {
        this.peers_total = peers_total;
    }

    public long getPieces_have() {
        return pieces_have;
    }

    public void setPieces_have(long pieces_have) {
        this.pieces_have = pieces_have;
    }

    public long getPieces_num() {
        return pieces_num;
    }

    public void setPieces_num(long pieces_num) {
        this.pieces_num = pieces_num;
    }

    public long getReannounce() {
        return reannounce;
    }

    public void setReannounce(long reannounce) {
        this.reannounce = reannounce;
    }

    public long getSeeds() {
        return seeds;
    }

    public void setSeeds(long seeds) {
        this.seeds = seeds;
    }

    public long getSeeds_total() {
        return seeds_total;
    }

    public void setSeeds_total(long seeds_total) {
        this.seeds_total = seeds_total;
    }

    public long getTotal_size() {
        return total_size;
    }

    public void setTotal_size(long total_size) {
        this.total_size = total_size;
    }

    public long getUp_speed_avg() {
        return up_speed_avg;
    }

    public void setUp_speed_avg(long up_speed_avg) {
        this.up_speed_avg = up_speed_avg;
    }

    public long getUp_speed() {
        return up_speed;
    }

    public void setUp_speed(long up_speed) {
        this.up_speed = up_speed;
    }
}

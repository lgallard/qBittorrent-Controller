package com.lgallardo.qbittorrentclient;

/**
 * Created by lgallard on 3/12/18.
 */

public class Options {

    // Maximum global number of simultaneous connections (or max_connec)
    public String global_max_num_connections;
    public String max_connec;

    // Maximum number of simultaneous connections per torrent (or max_connec_per_torrent)
    public String max_num_conn_per_torrent;
    public String max_connec_per_torrent;

    // Global maximum number of upload slots:
    public String max_uploads;

    // Maximum number of upload slots per torrent
    public String max_num_upslots_per_torrent;
    public String max_uploads_per_torrent;

    // Global upload speed limit in KiB/s; -1 means no limit is applied
    public String global_upload;
    public String up_limit;

    // Global download speed limit in KiB/s; -1 means no limit is applied
    public String global_download;
    public String dl_limit;

    // alternative global upload speed limit in KiB/s
    public String alt_upload;
    public String alt_up_limit;

    // alternative global upload speed limit in KiB/s
    public String alt_download;
    public String alt_dl_limit;

    // Is torrent queuing enabled ?
    public boolean torrent_queueing;

    // Maximum number of active simultaneous downloads
    public String max_act_downloads;

    // Maximum number of active simultaneous uploads
    public String max_act_uploads;

    // Maximum number of active simultaneous downloads and uploads
    public String max_act_torrents;

    // Schedule alternative rate limits
    public boolean schedule_alternative_rate_limits;

    // Scheduler starting hour
    public String alt_from_hour;

    // Scheduler starting min
    public String alt_from_min;

    // Scheduler ending hour
    public String alt_to_hour;

    // Scheduler ending min
    public String alt_to_min;

    // Scheduler scheduler days
    public String scheduler_days;

    public Options(String global_max_num_connections, String max_connec,
                   String max_num_conn_per_torrent, String max_connec_per_torrent,
                   String max_uploads, String max_num_upslots_per_torrent,
                   String max_uploads_per_torrent,
                   String global_upload, String up_limit,
                   String global_download, String dl_limit,
                   String alt_upload, String alt_download,
                   boolean torrent_queueing, String max_act_downloads, String max_act_uploads,
                   String max_act_torrents, boolean schedule_alternative_rate_limits,
                   String alt_from_hour, String alt_from_min, String alt_to_hour, String alt_to_min,
                   String scheduler_days) {
        this.global_max_num_connections = global_max_num_connections;
        this.max_connec = max_connec;
        this.max_num_conn_per_torrent = max_num_conn_per_torrent;
        this.max_connec_per_torrent = max_connec_per_torrent;
        this.max_uploads = max_uploads;
        this.max_num_upslots_per_torrent = max_num_upslots_per_torrent;
        this.max_uploads_per_torrent = max_uploads_per_torrent;
        this.global_upload = global_upload;
        this.up_limit = up_limit;
        this.global_download = global_download;
        this.dl_limit = dl_limit;
        this.alt_upload = alt_upload;
        this.alt_up_limit = alt_upload;
        this.alt_download = alt_download;
        this.alt_dl_limit = alt_download;
        this.torrent_queueing = torrent_queueing;
        this.max_act_downloads = max_act_downloads;
        this.max_act_uploads = max_act_uploads;
        this.max_act_torrents = max_act_torrents;
        this.schedule_alternative_rate_limits = schedule_alternative_rate_limits;
        this.alt_from_hour = alt_from_hour;
        this.alt_from_min = alt_from_min;
        this.alt_to_hour = alt_to_hour;
        this.alt_to_min = alt_to_min;
        this.scheduler_days = scheduler_days;
    }

    public String getGlobal_max_num_connections() {
        return global_max_num_connections;
    }

    public void setGlobal_max_num_connections(String global_max_num_connections) {
        this.global_max_num_connections = global_max_num_connections;
    }

    public String getMax_num_conn_per_torrent() {
        return max_num_conn_per_torrent;
    }

    public void setMax_num_conn_per_torrent(String max_num_conn_per_torrent) {
        this.max_num_conn_per_torrent = max_num_conn_per_torrent;
    }

    public String getMax_uploads() {
        return max_uploads;
    }

    public void setMax_uploads(String max_uploads) {
        this.max_uploads = max_uploads;
    }

    public String getMax_num_upslots_per_torrent() {
        return max_num_upslots_per_torrent;
    }

    public void setMax_num_upslots_per_torrent(String max_num_upslots_per_torrent) {
        this.max_num_upslots_per_torrent = max_num_upslots_per_torrent;
    }

    public String getGlobal_upload() {
        return global_upload;
    }

    public void setGlobal_upload(String global_upload) {
        this.global_upload = global_upload;
    }

    public String getGlobal_download() {
        return global_download;
    }

    public void setGlobal_download(String global_download) {
        this.global_download = global_download;
    }

    public String getAlt_upload() {
        return alt_upload;
    }

    public void setAlt_upload(String alt_upload) {
        this.alt_upload = alt_upload;
    }

    public String getAlt_download() {
        return alt_download;
    }

    public void setAlt_download(String alt_download) {
        this.alt_download = alt_download;
    }

    public boolean isTorrent_queueing() {
        return torrent_queueing;
    }

    public void setTorrent_queueing(boolean torrent_queueing) {
        this.torrent_queueing = torrent_queueing;
    }

    public String getMax_act_downloads() {
        return max_act_downloads;
    }

    public void setMax_act_downloads(String max_act_downloads) {
        this.max_act_downloads = max_act_downloads;
    }

    public String getMax_act_uploads() {
        return max_act_uploads;
    }

    public void setMax_act_uploads(String max_act_uploads) {
        this.max_act_uploads = max_act_uploads;
    }

    public String getMax_act_torrents() {
        return max_act_torrents;
    }

    public void setMax_act_torrents(String max_act_torrents) {
        this.max_act_torrents = max_act_torrents;
    }

    public boolean isSchedule_alternative_rate_limits() {
        return schedule_alternative_rate_limits;
    }

    public void setSchedule_alternative_rate_limits(boolean schedule_alternative_rate_limits) {
        this.schedule_alternative_rate_limits = schedule_alternative_rate_limits;
    }

    public String getAlt_from_hour() {
        return alt_from_hour;
    }

    public void setAlt_from_hour(String alt_from_hour) {
        this.alt_from_hour = alt_from_hour;
    }

    public String getAlt_from_min() {
        return alt_from_min;
    }

    public void setAlt_from_min(String alt_from_min) {
        this.alt_from_min = alt_from_min;
    }

    public String getAlt_to_hour() {
        return alt_to_hour;
    }

    public void setAlt_to_hour(String alt_to_hour) {
        this.alt_to_hour = alt_to_hour;
    }

    public String getAlt_to_min() {
        return alt_to_min;
    }

    public void setAlt_to_min(String alt_to_min) {
        this.alt_to_min = alt_to_min;
    }

    public String getScheduler_days() {
        return scheduler_days;
    }

    public void setScheduler_days(String scheduler_days) {
        this.scheduler_days = scheduler_days;
    }

    public String getMax_connec() {
        return max_connec;
    }

    public void setMax_connec(String max_connec) {
        this.max_connec = max_connec;
    }

    public String getMax_connec_per_torrent() {
        return max_connec_per_torrent;
    }

    public void setMax_connec_per_torrent(String max_connec_per_torrent) {
        this.max_connec_per_torrent = max_connec_per_torrent;
    }

    public String getMax_uploads_per_torrent() {
        return max_uploads_per_torrent;
    }

    public void setMax_uploads_per_torrent(String max_uploads_per_torrent) {
        this.max_uploads_per_torrent = max_uploads_per_torrent;
    }

    public String getUp_limit() {
        return up_limit;
    }

    public void setUp_limit(String up_limit) {
        this.up_limit = up_limit;
    }

    public String getDl_limit() {
        return dl_limit;
    }

    public void setDl_limit(String dl_limit) {
        this.dl_limit = dl_limit;
    }

    public String getAlt_up_limit() {
        return alt_up_limit;
    }

    public void setAlt_up_limit(String alt_up_limit) {
        this.alt_up_limit = alt_up_limit;
    }

    public String getAlt_dl_limit() {
        return alt_dl_limit;
    }

    public void setAlt_dl_limit(String alt_dl_limit) {
        this.alt_dl_limit = alt_dl_limit;
    }

}

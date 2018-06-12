package com.lgallardo.qbittorrentclient;

/**
 * Created by lgallard on 6/11/18.
 */

public class GeneralInfoOld {
    private String path;
    private String creation_date;
    private String piece_size;
    private String comment;
    private String total_wasted;
    private String total_uploaded;
    private String total_downloaded;
    private String up_limit;
    private String dl_limit;
    private String nb_connections;
    private String share_ratio;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getPiece_size() {
        return piece_size;
    }

    public void setPiece_size(String piece_size) {
        this.piece_size = piece_size;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotal_wasted() {
        return total_wasted;
    }

    public void setTotal_wasted(String total_wasted) {
        this.total_wasted = total_wasted;
    }

    public String getTotal_uploaded() {
        return total_uploaded;
    }

    public void setTotal_uploaded(String total_uploaded) {
        this.total_uploaded = total_uploaded;
    }

    public String getTotal_downloaded() {
        return total_downloaded;
    }

    public void setTotal_downloaded(String total_downloaded) {
        this.total_downloaded = total_downloaded;
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

    public String getNb_connections() {
        return nb_connections;
    }

    public void setNb_connections(String nb_connections) {
        this.nb_connections = nb_connections;
    }

    public String getShare_ratio() {
        return share_ratio;
    }

    public void setShare_ratio(String share_ratio) {
        this.share_ratio = share_ratio;
    }
}

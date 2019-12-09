/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Torrent {

//    private String name;
//    private String size;
//    private String info;
//    private String state;
//    private String hash;
//    private String dlspeed;
//    private String upspeed;
//    private String ratio;
//    private String progress;
//    private String downloaded;
//    private String leechs;
//    private String seeds;
//    private String priority;
//    private String eta;
//    private String savePath;
//    private String creationDate;
//    private String comment;
//    private String totalWasted;
//    private String totalUploaded;
//    private String totalDownloaded;
//    private String timeElapsed;
//    private String nbConnections;
//    private String uploadLimit;
//    private String downloadLimit;
//    private boolean sequentialDownload;
//    private boolean firstLastPiecePrio;
//    private String addedOn;
//    private String completionOn;
//    private String label;

    // New format
    @SerializedName("added_on")
    @Expose
    private double added_on;
    @SerializedName("amount_left")
    @Expose
    private double amount_left;
    @SerializedName("auto_tmm")
    @Expose
    private boolean auto_tmm;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("completed")
    @Expose
    private double completed;
    @SerializedName("completion_on")
    @Expose
    private double completion_on;
    @SerializedName("dl_limit")
    @Expose
    private int dl_limit;
    @SerializedName("dlspeed")
    @Expose
    private int dlspeed;
    @SerializedName("downloaded")
    @Expose
    private double downloaded;
    @SerializedName("downloaded_session")
    @Expose
    private int downloaded_session;
    @SerializedName("eta")
    @Expose
    private long eta;
    @SerializedName("f_l_piece_prio")
    @Expose
    private boolean f_l_piece_prio;
    @SerializedName("force_start")
    @Expose
    private boolean force_start;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("last_activity")
    @Expose
    private int last_activity;
    @SerializedName("magnet_uri")
    @Expose
    private String magnet_uri;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("num_complete")
    @Expose
    private int num_complete;
    @SerializedName("num_incomplete")
    @Expose
    private int num_incomplete;
    @SerializedName("num_leechs")
    @Expose
    private int num_leechs;
    @SerializedName("num_seeds")
    @Expose
    private int num_seeds;
    @SerializedName("priority")
    @Expose
    private int priority;
    @SerializedName("progress")
    @Expose
    private double progress;
    @SerializedName("ratio")
    @Expose
    private float ratio;
    @SerializedName("ratio_limit")
    @Expose
    private int ratio_limit;
    @SerializedName("save_path")
    @Expose
    private String save_path;
    @SerializedName("seen_complete")
    @Expose
    private double seen_complete;
    @SerializedName("seq_dl")
    @Expose
    private boolean seq_dl;
    @SerializedName("size")
    @Expose
    private double size;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("super_seeding")
    @Expose
    private boolean super_seeding;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("time_active")
    @Expose
    private int time_active;
    @SerializedName("total_size")
    @Expose
    private double total_size;
    @SerializedName("tracker")
    @Expose
    private String tracker;
    @SerializedName("up_limit")
    @Expose
    private int up_limit;
    @SerializedName("uploaded")
    @Expose
    private double uploaded;
    @SerializedName("uploaded_session")
    @Expose
    private int uploaded_session;
    @SerializedName("upspeed")
    @Expose
    private int upspeed;

    // Extra
    private String info;


//    public Torrent(String name, String size, String state, String hash,
//                   String info, String ratio, String progress, String leechs,
//                   String seeds, String priority, String eta, String dlspeed, String upspeed, boolean sequentialDownload, boolean firstLastPiecePrio, String addedOn, String completionOn, String label) {
//        this.name = name;
//        this.size = size;
//        this.state = state;
//        this.hash = hash;
//        this.info = info;
//        this.ratio = ratio;
//        this.progress = progress;
//        this.leechs = leechs;
//        this.seeds = seeds;
//        this.priority = priority;
//        this.eta = eta;
//        this.dlspeed = dlspeed;
//        this.upspeed = upspeed;
//        this.sequentialDownload = sequentialDownload;
//        this.firstLastPiecePrio = firstLastPiecePrio;
//        this.addedOn = addedOn;
//        this.completionOn = completionOn;
//        this.label = label;
//
//    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

//    /**
//     * @return the size
//     */
//    public String getSize() {
//        return size;
//    }
//
//    /**
//     * @param size the size to set
//     */
//    public void setSize(String size) {
//        this.size = size;
//    }
//

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the state
     */
    public String getState() {

        // Despite documentation some unexpected states are returned by qBittorrent servers
        // Check how is handled by the WebUI: https://github.com/qbittorrent/qBittorrent/blob/de5381856ddf11e7e3a8f29f1ccb7168c108956c/src/webui/www/public/scripts/dynamicTable.js

        if ("metaDL".equals(state) || "forcedDL".equals(state)) {
            return "downloading";
        }
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

//    /**
//     * @return the dlspeed
//     */
//    public String getDlspeed() {
//        return dlspeed;
//    }
//
//    /**
//     * @param dlspeed the dlspeed to set
//     */
//    public void setDlspeed(String dlspeed) {
//        this.dlspeed = dlspeed;
//    }

    private int getSpeedWeight(String type) {

        String scalar = "0";
        String unit = "";
        int weight = 0;

        if (type.equals("upload")) {
            return this.upspeed;
        }
        return this.dlspeed;


//        String speed = this.dlspeed;
//
//        if (type.equals("upload")) {
//            speed = this.upspeed;
//        }
//
//
//        String[] words = speed.split("\\s+");
//
//        if (words.length == 2) {
//
//            try {
//                scalar = words[0].replacthis.dlspeed;e(",", ".");
//            } catch (Exception e) {
//            }
//
//            try {
//                unit = words[1];
//            } catch (Exception e) {
//            }
//
//            weight = (int) (Float.parseFloat(scalar) * 10);
//
//            if (unit.charAt(0) == 'B') {
//                weight = (int) (Float.parseFloat(scalar) / 10);
//            }
//            if (unit.charAt(0) == 'K') {
//                weight = weight * 10;
//            }
//
//            if (unit.charAt(0) == 'M') {
//                weight = weight * 100;
//            }
//
//            if (unit.charAt(0) == 'G') {
//                weight = weight * 1000;
//            }
//
//            if (unit.charAt(0) == 'T') {
//                weight = weight * 10000;
//            }
//
//        }
//
//        return weight;
    }

    public int getDownloadSpeedWeight() {
        return getSpeedWeight("download");
    }

    //
//    /**
//     * @return the upspeed
//     */
//    public String getUpspeed() {
//        return upspeed;
//    }
//
    public int getUploadSpeedWeight() {
        return getSpeedWeight("upload");
    }

    //
//    /**
//     * @param upspeed the upspeed to set
//     */
//    public void setUpspeed(String upspeed) {
//        this.upspeed = upspeed;
//    }
//
//    /**
//     * @return the ratio
//     */
//    public String getRatio() {
//
//        // if it contains ∞, ratio is > 100
//        if(ratio.equals("∞")){
//            return "1000";
//        }
//
//
//        // Format ratio
//        String formatedRatio = "0";
//        try {
//            formatedRatio = String.format("%.2f", Float.parseFloat(ratio)).replace(",", ".");
//        } catch (Exception e) {
//        }
//
//        return formatedRatio;
//    }
//
//    /**
//     * @param ratio the ratio to set
//     */
//    public void setRatio(String ratio) {
//        this.ratio = ratio;
//    }
//    /**
//     * @return the progress
//     */
//    public String getProgress() {
//        return progress;
//    }
//
//    /**
//     * @param progress the progress to set
//     */
//    public void setProgress(String progress) {
//        this.progress = progress;
//    }
//
//    public String getPercentage() {
//
//        int index = progress.indexOf(".");
//
//        if (index == -1) {
//            index = progress.indexOf(",");
//
//            if (index == -1) {
//                index = progress.length();
//            }
//        }
//
//        return progress.substring(0, index);
//
//    }
//
    public int getEtaInMinutes() {

        String[] words = ("" + eta).split("\\s+");
        String minutes = "0";
        String hours = "0";
        String days = "0";
        int etaInMinutes = 0;

        if (words.length == 1) {

            if (words[0].equals("∞")) {
                minutes = "144000";
            } else {
                if (words[0].equals("0")) {
                    minutes = "0";
                } else {

                    minutes = words[0].substring(0, words[0].length() - 1);

                }
            }


        } else {

            if (words[0].substring(words[0].length() - 1, words[0].length()).equals("d")) {

                days = words[0].substring(0, words[0].length() - 1);
                hours = words[1].substring(0, words[1].length() - 1);

            } else {

                hours = words[0].substring(0, words[0].length() - 1);
                minutes = words[1].substring(0, words[1].length() - 1);

            }

        }

        try {
            etaInMinutes = Integer.parseInt(days) * 1440 + Integer.parseInt(hours) * 60 + Integer.parseInt(minutes);
        } catch (Exception e) {
            etaInMinutes = 144000;
            Log.e("Torrent", e.toString());
        }

        return etaInMinutes;
    }
//
//    /**
//     * @return the downloaded size
//     */
//    public String getDownloaded() {
//        return downloaded;
//    }
//
//    /**
//     * @param downloaded the downloaded to set
//     */
//    public void setDownloaded(String downloaded) {
//        this.downloaded = downloaded;
//    }
//
//    /**
//     * @return the leechs
//     */
//    public String getLeechs() {
//        return leechs;
//    }
//
//    /**
//     * @param leechs the leechs to set
//     */
//    public void setLeechs(String leechs) {
//        this.leechs = leechs;
//    }
//
//    /**
//     * @return the seeds
//     */
//    public String getSeeds() {
//        return seeds;
//    }
//
//    /**
//     * @param seeds the seeds to set
//     */
//    public void setSeeds(String seeds) {
//        this.seeds = seeds;
//    }

//    /**
//     * @return the priority
//     */
//    public String getPriority() {
//
//        if(priority.equals("-1") || priority.equals("0")){
//            return "*";
//        }
//        return priority;
//    }
//
//    /**
//     * @param priority the priority to set
//     */
//    public void setPriority(String priority) {
//        this.priority = priority;
//    }

//    /**
//     * @return the eta
//     */
//    public String getEta() {
//
//        if(eta.equals("8640000")){
//            return "∞";
//        }
//
//        return eta;
//    }
//
//    /**
//     * @param eta the eta to set
//     */
//    public void setEta(String eta) {
//        this.eta = eta;
//    }
//
//    /**
//     * @return the path
//     */
//    public String getSavePath() {
//        return savePath;
//    }
//
//    /**
//     * @param savePath the path to set
//     */
//    public void setSavePath(String savePath) {
//        this.savePath = savePath;
//    }
//
//    /**
//     * @return the creationDate
//     */
//    public String getCreationDate() {
//        return creationDate;
//    }
//
//    /**
//     * @param creationDate the creationDate to set
//     */
//    public void setCreationDate(String creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    /**
//     * @return the comment
//     */
//    public String getComment() {
//        return comment;
//    }
//
//    /**
//     * @param comment the comment to set
//     */
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    /**
//     * @return the totalWasted
//     */
//    public String getTotalWasted() {
//        return totalWasted;
//    }
//
//    /**
//     * @param totalWasted the totalWasted to set
//     */
//    public void setTotalWasted(String totalWasted) {
//        this.totalWasted = totalWasted;
//    }
//
//    /**
//     * @return the totalUploaded
//     */
//    public String getTotalUploaded() {
//        return totalUploaded;
//    }
//
//    /**
//     * @param totalUploaded the totalUploaded to set
//     */
//    public void setTotalUploaded(String totalUploaded) {
//        this.totalUploaded = totalUploaded;
//    }
//
//    /**
//     * @return the totalDownloaded
//     */
//    public String getTotalDownloaded() {
//        return totalDownloaded;
//    }
//
//    /**
//     * @param totalDownloaded the totalDownloaded to set
//     */
//    public void setTotalDownloaded(String totalDownloaded) {
//        this.totalDownloaded = totalDownloaded;
//    }
//
//    /**
//     * @return the timeElapsed
//     */
//    public String getTimeElapsed() {
//
//        return timeElapsed;
//    }
//
//    /**
//     * @param timeElapsed the timeElapsed to set
//     */
//    public void setTimeElapsed(String timeElapsed) {
//        this.timeElapsed = timeElapsed;
//    }
//
//    /**
//     * @return the nbConnections
//     */
//    public String getNbConnections() {
//        return nbConnections;
//    }
//
//    /**
//     * @param nbConnections the nbConnections to set
//     */
//    public void setNbConnections(String nbConnections) {
//        this.nbConnections = nbConnections;
//    }
//
//    public String getUploadLimit() {
//
//        if (uploadLimit.equals("-1")) {
//            return "∞";
//        }
//
//        return uploadLimit;
//    }
//
//    public void setUploadLimit(String uploadLimit) {
//        this.uploadLimit = uploadLimit;
//    }
//
//    public String getDownloadLimit() {
//
//        if (downloadLimit.equals("-1")) {
//            return "∞";
//        }
//
//        return downloadLimit;
//    }
//
//    public void setDownloadLimit(String downloadLimit) {
//        this.downloadLimit = downloadLimit;
//    }
//
//
//    public boolean getSequentialDownload() {
//        return sequentialDownload;
//    }
//
//    public void setSequentialDownload(boolean sequentialDownload) {
//        this.sequentialDownload = sequentialDownload;
//    }
//
//    public boolean getisFirstLastPiecePrio() {
//        return firstLastPiecePrio;
//    }
//
//    public void setFirstLastPiecePrio(boolean firstLastPiecePrio) {
//        this.firstLastPiecePrio = firstLastPiecePrio;
//    }
//
//    public String getAddedOn() {
//        return addedOn;
//    }
//
//    public void setAddedOn(String addedOn) {
//        this.addedOn = addedOn;
//    }
//
//    public String getCompletionOn() {
//        return completionOn;
//    }
//
//    public void setCompletionOn(String completionOn) {
//        this.completionOn = completionOn;
//    }
//
//
//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }


    // new format

    public double getAdded_on() {
        return added_on;
    }

    public void setAdded_on(double added_on) {
        this.added_on = added_on;
    }

    public double getAmount_left() {
        return amount_left;
    }

    public void setAmount_left(double amount_left) {
        this.amount_left = amount_left;
    }

    public boolean isAuto_tmm() {
        return auto_tmm;
    }

    public void setAuto_tmm(boolean auto_tmm) {
        this.auto_tmm = auto_tmm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCompleted() {
        return completed;
    }

    public void setCompleted(double completed) {
        this.completed = completed;
    }

    public double getCompletion_on() {
        return completion_on;
    }

    public void setCompletion_on(double completion_on) {
        this.completion_on = completion_on;
    }

    public int getDl_limit() {
        return dl_limit;
    }

    public void setDl_limit(int dl_limit) {
        this.dl_limit = dl_limit;
    }

    public int getDlspeed() {
        return dlspeed;
    }

    public void setDlspeed(int dlspeed) {
        this.dlspeed = dlspeed;
    }

    public double getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(double downloaded) {
        this.downloaded = downloaded;
    }

    public int getDownloaded_session() {
        return downloaded_session;
    }

    public void setDownloaded_session(int downloaded_session) {
        this.downloaded_session = downloaded_session;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public boolean isF_l_piece_prio() {
        return f_l_piece_prio;
    }

    public void setF_l_piece_prio(boolean f_l_piece_prio) {
        this.f_l_piece_prio = f_l_piece_prio;
    }

    public boolean isForce_start() {
        return force_start;
    }

    public void setForce_start(boolean force_start) {
        this.force_start = force_start;
    }

    public int getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(int last_activity) {
        this.last_activity = last_activity;
    }

    public String getMagnet_uri() {
        return magnet_uri;
    }

    public void setMagnet_uri(String magnet_uri) {
        this.magnet_uri = magnet_uri;
    }

    public int getNum_complete() {
        return num_complete;
    }

    public void setNum_complete(int num_complete) {
        this.num_complete = num_complete;
    }

    public int getNum_incomplete() {
        return num_incomplete;
    }

    public void setNum_incomplete(int num_incomplete) {
        this.num_incomplete = num_incomplete;
    }

    public int getNum_leechs() {
        return num_leechs;
    }

    public void setNum_leechs(int num_leechs) {
        this.num_leechs = num_leechs;
    }

    public int getNum_seeds() {
        return num_seeds;
    }

    public void setNum_seeds(int num_seeds) {
        this.num_seeds = num_seeds;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public int getRatio_limit() {
        return ratio_limit;
    }

    public void setRatio_limit(int ratio_limit) {
        this.ratio_limit = ratio_limit;
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public double getSeen_complete() {
        return seen_complete;
    }

    public void setSeen_complete(double seen_complete) {
        this.seen_complete = seen_complete;
    }

    public boolean isSeq_dl() {
        return seq_dl;
    }

    public void setSeq_dl(boolean seq_dl) {
        this.seq_dl = seq_dl;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public boolean isSuper_seeding() {
        return super_seeding;
    }

    public void setSuper_seeding(boolean super_seeding) {
        this.super_seeding = super_seeding;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getTime_active() {
        return time_active;
    }

    public void setTime_active(int time_active) {
        this.time_active = time_active;
    }

    public double getTotal_size() {
        return total_size;
    }

    public void setTotal_size(double total_size) {
        this.total_size = total_size;
    }

    public String getTracker() {
        return tracker;
    }

    public void setTracker(String tracker) {
        this.tracker = tracker;
    }

    public int getUp_limit() {
        return up_limit;
    }

    public void setUp_limit(int up_limit) {
        this.up_limit = up_limit;
    }

    public double getUploaded() {
        return uploaded;
    }

    public void setUploaded(double uploaded) {
        this.uploaded = uploaded;
    }

    public int getUploaded_session() {
        return uploaded_session;
    }

    public void setUploaded_session(int uploaded_session) {
        this.uploaded_session = uploaded_session;
    }

    public int getUpspeed() {
        return upspeed;
    }

    public void setUpspeed(int upspeed) {
        this.upspeed = upspeed;
    }

    @Override
    public String toString() {

        return "Name: " + this.name + " - hash:" + this.hash;
    }
}

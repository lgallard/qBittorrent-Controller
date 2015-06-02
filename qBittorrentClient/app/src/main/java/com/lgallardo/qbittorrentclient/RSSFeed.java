package com.lgallardo.qbittorrentclient;

import java.util.HashMap;

/**
 * Created by lgallard on 02/06/15.
 */
public class RSSFeed {

    public String channel;
    public String title;
    public String link;


    public void RSSFeed() {

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap getItems() {
        return items;
    }

    public void setItems(HashMap items) {
        this.items = items;
    }

    public String description;

    public HashMap items;


}
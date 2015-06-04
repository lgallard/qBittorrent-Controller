package com.lgallardo.qbittorrentclient;

import java.util.List;

/**
 * Created by lgallard on 02/06/15.
 */
public class RSSFeed {

    public String channelTitle;
    public String channelDescription;
    public String channelLink;
    public List<RSSFeedItem> items;

    public void RSSFeed() {

    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public String getChannelLink() {
        return channelLink;
    }

    public void setChannelLink(String channelLink) {
        this.channelLink = channelLink;
    }

    public List<RSSFeedItem> getItems() {
        return items;
    }

    public void setItems(List<RSSFeedItem> items) {
        this.items = items;
    }
}
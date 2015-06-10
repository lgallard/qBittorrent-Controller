package com.lgallardo.qbittorrentclient;

import java.util.List;

/**
 * Created by lgallard on 02/06/15.
 */
public class RSSFeed {

    public String channelTitle;
    public String channelLink;
    public String channelPubDate;
    public List<RSSFeedItem> items;

    public void RSSFeed() {

    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
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

    public String getChannelPubDate() {
        return channelPubDate;
    }

    public void setChannelPubDate(String channelPubDate) {
        this.channelPubDate = channelPubDate;
    }
}
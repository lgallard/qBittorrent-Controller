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
    public boolean autodDownload;
    public boolean notifyNew;
    public int itemCount;
    public boolean resultOk;

    public RSSFeed() {

    }

    public RSSFeed(String title, String link, boolean autodDownload, boolean notifyNew) {
        this.channelTitle = title;
        this.channelLink = link;
        this.autodDownload = autodDownload;
        this.notifyNew = notifyNew;
    }

    public RSSFeed(String title, String link) {
        this(title, link, true, false);
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

    public boolean getAutodDownload() {
        return autodDownload;
    }

    public void setAutodDownload(boolean autodDownload) {
        this.autodDownload = autodDownload;
    }

    public boolean getNotifyNew() {
        return notifyNew;
    }

    public void setNotifyNew(boolean notifyNew) {
        this.notifyNew = notifyNew;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean resultOk) {
        this.resultOk = resultOk;
    }
}
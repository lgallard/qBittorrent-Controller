package com.lgallardo.qbittorrentclient;

public class Tracker {

	private String url;

	public Tracker(String url){
		this.url = url;
		
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

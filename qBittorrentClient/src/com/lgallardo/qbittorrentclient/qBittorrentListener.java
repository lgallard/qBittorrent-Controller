package com.lgallardo.qbittorrentclient;

import com.lgallardo.qbittorrentclient.qBittorrentBinder.myObject;

public interface qBittorrentListener {
	void updateUI(myObject[] result);
	void sendCommandResult(String result);
}

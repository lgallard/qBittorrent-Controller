package com.lgallardo.qbittorrentclient;

import com.lgallardo.qbittorrentclient.torrent;

public interface qBittorrentListener {
	void updateUI(torrent[] result);
	void sendCommandResult(String result);
}

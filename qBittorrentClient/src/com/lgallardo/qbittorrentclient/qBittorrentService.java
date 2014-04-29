package com.lgallardo.qbittorrentclient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class qBittorrentService extends Service {

	private qBittorrentBinder binder = null;

	public void OnCreate() {
		super.onCreate();

		// Binder
		binder = new qBittorrentBinder();
	}

	@Override
	public IBinder onBind(Intent i) {
		return (binder);
	}
	
}

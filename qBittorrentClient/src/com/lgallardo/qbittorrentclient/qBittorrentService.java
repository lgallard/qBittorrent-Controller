package com.lgallardo.qbittorrentclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class qBittorrentService extends Service {

	public void OnCreate() {
		super.onCreate();

		// private qBittorrentBinder binder=null;

		// Binder
		
		//binder=new qBittorrentBinder();


	}

	@Override
	public IBinder onBind(Intent i) {
		// TODO Auto-generated method stub
		return null;
	}

}

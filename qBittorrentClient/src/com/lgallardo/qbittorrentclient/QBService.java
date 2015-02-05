/*******************************************************************************
 * Copyright (c) 2015 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/

package com.lgallardo.qbittorrentclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QBService extends Service {

    private QBServiceBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new QBServiceBinder();

        Log.i("Service", "Service created");


    }

    @Override
    public IBinder onBind(Intent intent) {

        return (binder);
    }
}

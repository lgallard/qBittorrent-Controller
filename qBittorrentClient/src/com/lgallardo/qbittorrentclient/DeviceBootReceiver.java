/*******************************************************************************
 * Copyright (c) 2015 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.util.HashMap;

// This class set the alarm (check of completed torrents) after rebooting the device
public class DeviceBootReceiver extends BroadcastReceiver {


    public static String qb_version = "3.1.x";
    public static String downloading_hashes;
    public static String completed_hashes;
    // Cookie (SID - Session ID)
    public static String cookie = null;
    protected static HashMap<String, Torrent> completed, downloading, notify;
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean https;

    protected static int connection_timeout;
    protected static int data_timeout;

    protected static String lastState;
    protected static long notification_period;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;


    // Alarm manager
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intentReceived) {

        this.context = context;

        if (intentReceived.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            // Get preferences
            getSettings();

            // Set Alarm for checking completed torrents
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotifierService.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * 60,
                    notification_period, alarmIntent);
        }
    }

    // Get settings
    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from preferences
        hostname = sharedPrefs.getString("hostname", "");
        subfolder = sharedPrefs.getString("subfolder", "");

        protocol = sharedPrefs.getString("protocol", "NULL");

        // If user leave the field empty, set 8080 port
        try {
            port = Integer.parseInt(sharedPrefs.getString("port", "8080"));
        } catch (NumberFormatException e) {
            port = 8080;

        }
        username = sharedPrefs.getString("username", "NULL");
        password = sharedPrefs.getString("password", "NULL");
        https = sharedPrefs.getBoolean("https", false);

        // Check https
        if (https) {
            protocol = "https";

        } else {
            protocol = "http";
        }

        // Get connection and data timeouts
        try {
            connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "5"));
        } catch (NumberFormatException e) {
            connection_timeout = 5;
        }

        try {
            data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "8"));
        } catch (NumberFormatException e) {
            data_timeout = 8;
        }

        qb_version = sharedPrefs.getString("qb_version", "3.1.x");


        MainActivity.cookie = sharedPrefs.getString("qbCookie", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);

        // Notification check
        try {
            notification_period = Long.parseLong(sharedPrefs.getString("notification_period", "1200000L"));
        } catch (NumberFormatException e) {
            notification_period = 1200000L;
        }


    }
}

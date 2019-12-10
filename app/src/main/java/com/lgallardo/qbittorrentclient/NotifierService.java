/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

package com.lgallardo.qbittorrentclient;

import android.app.Notification;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lgallard on 2/22/15.
 */
public class NotifierService extends BroadcastReceiver {

    public static String qb_version = "4.1.x";
    public static String completed_hashes;
    // Cookie (SID - Session ID)
    public static String cookie = null;
    protected static HashMap<String, Torrent> last_completed, completed, notify;
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean https;

    protected static int connection_timeout;
    protected static int data_timeout;
    protected static String sortby;

    protected static String lastState;
    protected static int currentServer;
    protected static boolean enable_notifications;

    private static Context context;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;

    // SSID properties
    protected static String ssid;
    protected static String local_hostname;
    protected static int local_port;

    // Keystore for self-signed certificate
    protected static String keystore_path;
    protected static String keystore_password;


    public NotifierService() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        getSettings();

        if (enable_notifications) {
            getCookie();
        }
    }

    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from preferences
        currentServer = Integer.parseInt(sharedPrefs.getString("currentServer", "1"));

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
            connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "10"));
        } catch (NumberFormatException e) {
            connection_timeout = 10;
        }

        try {
            data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "20"));
        } catch (NumberFormatException e) {
            data_timeout = 20;
        }

        qb_version = sharedPrefs.getString("qb_version", "3.2.x");
        cookie = sharedPrefs.getString("qbCookie2", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);

        // Notifications
        enable_notifications = sharedPrefs.getBoolean("enable_notifications", false);
        completed_hashes = sharedPrefs.getString("completed_hashes" + currentServer, "");

        // Get local SSID properties
        ssid = sharedPrefs.getString("ssid", "");
        local_hostname = sharedPrefs.getString("local_hostname", null);


        // If user leave the field empty, set 8080 port
        try {
            local_port = Integer.parseInt(sharedPrefs.getString("local_port", "-1"));
        } catch (NumberFormatException e) {
            local_port = -1;

        }

        // Set SSI and local hostname and port
        if (ssid != null && !ssid.equals("")) {

            // Get SSID if WiFi
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String wifiSSID = wifiInfo.getSSID();

            if (wifiSSID.equals("\"" + ssid + "\"") && wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                if (local_hostname != null && !local_hostname.equals("")) {
                    hostname = local_hostname;
                }

                if (local_port != -1) {
                    port = local_port;
                }
            }
        }

        // Get keystore for self-signed certificate
        keystore_path = sharedPrefs.getString("keystore_path" + currentServer, "");
        keystore_password = sharedPrefs.getString("keystore_password" + currentServer, "");
    }

    // Volley singletons
    protected void addVolleyRequest(StringRequest stringArrayRequest) {
        VolleySingleton.getInstance(context).addToRequestQueue(stringArrayRequest);
    }

    protected void addVolleyRequest(JsonArrayRequest jsArrayRequest) {
        VolleySingleton.getInstance(context).addToRequestQueue(jsArrayRequest);
    }

    // Volley methods
    private void getCookie(final MainActivity.VolleyCallback callback) {

        String url;

        url = protocol + "://" + hostname + ":" + port + "/api/v2/auth/login";

        // New JSONObject request
        CustomStringRequest jsArrayRequest = new CustomStringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Debug", "[NS][getCookie] Response: " + response);

                        JSONObject jsonObject = null;
                        CustomObjectResult customObjectResult = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (Exception e) {
                            Log.e("Debug", "THIS => " + e.getMessage());
                            e.printStackTrace();
                        }

                        Gson gson = new Gson();
                        String cookieString = null;

                        try {
//                            Log.d("Debug", "JSONObject: " + jsonObject.toString());
                            customObjectResult = gson.fromJson(jsonObject.toString(), CustomObjectResult.class);
                            // Get Headers
                            String headers = customObjectResult.getHeaders();

                            // Get set-cookie from headers
                            cookieString = headers.split("set-cookie=")[1].split(";")[0];

                        } catch (Exception e) {
                            Log.e("Debug", "THIS 2 => " + e.getMessage());
                            e.printStackTrace();
                        }


                        // Return value
                        callback.onSuccess(cookieString);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("Debug", "getCookie - Error in JSON response: " + error.getMessage());

                        callback.onSuccess("");

                        // Toast.makeText(context, "Error getting new API version: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Host", hostname + ":" + port);
                params.put("Referer", protocol + "://" + hostname + ":" + port);
                params.put("Content-Type:", "application/x-www-form-urlencoded");

                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Add request to te queue
        addVolleyRequest(jsArrayRequest);
    }

    // Get all torrents
    private List getTorrentList(final String state, final TorrentsListCallBack callback) {

        final List<Torrent> torrents = new ArrayList<>();

        String url = "";

        // if server is publish in a subfolder, fix url
        if (subfolder != null && !subfolder.equals("")) {
            url = subfolder + "/" + url;
        }

        url = protocol + "://" + hostname + ":" + port + url;

        // Command
        url = url + "/api/v2/torrents/info?filter=" + state;

        //Log.d("Debug: ", "GetAllTorrents NS - URL: " + url);
        //Log.d("Debug: ", "GetAllTorrents NS - cookies: " + cookie);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Get list type to parse it
                        Type listType = new TypeToken<List<Torrent>>() {
                        }.getType();

                        // Parse Lists using Gson
                        torrents.addAll((List<Torrent>) new Gson().fromJson(response.toString(), listType));

                        // Return value
                        callback.onSuccess(torrents);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log status code
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.d("Debug", "getAllTorrents NS - statusCode: " + networkResponse.statusCode);
                        }

                        // Log error
                        Log.d("Debug", "getAllTorrents NS - Error in JSON response: " + error.getMessage());
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("User-Agent", "qBittorrent for Android");
                params.put("Host", hostname + ":" + port);
                params.put("Referer", protocol + "://" + hostname + ":" + port);
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Cookie", cookie);
                return params;
            }
        };

        // Add request to te queue
        addVolleyRequest(jsArrayRequest);

        // Return the lists
        return torrents;
    }


    // Volley Wrappers
    private void getCookie() {
        getCookie(new MainActivity.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                //Log.d("Debug: ", ">>>NS Cookie: " + result);

                cookie = result;

                // Save options locally
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                // Save key-values
                editor.putString("qbCookie2", result);

                // Commit changes
                editor.apply();
                getTorrentList("all");

            }
        });
    }

    public void getTorrentList(String state) {

        getTorrentList(state, new TorrentsListCallBack() {
            @Override
            public void onSuccess(List<Torrent> torrents) {

                String size;

                for (int i = 0; i < torrents.size(); i++) {

                    Log.d("Debug", "[NS][getTorrentList] File: " + torrents.get(i).getName());
                    Log.d("Debug", "[NS][getTorrentList]Hash: " + torrents.get(i).getHash());


                    Log.d("Debug", "[NS][getTorrentList] Calculating sizes!!!!");
                    size = Common.calculateSize(torrents.get(i).getSize());

                    Double progress = Double.parseDouble("" + torrents.get(i).getProgress());

                    // Set torrent progress
//                    torrents.get(i).setProgress(String.format("%.1f", (progress * 100)));

                    //Log.d("Debug", "NS> Size: " + size);
                    //Log.d("Debug", "NS> progress: " + (progress * 100));
                    //Log.d("Debug", "NS> progress reported: " + torrents.get(i).getProgress());

                    // Get torrent generic properties
                    try {
                        // Calculate total downloaded
                        Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                        String sizeUnit = size.substring(size.indexOf(" "), size.length());

//                        torrents.get(i).setDownloaded(String.format("%.1f", sizeScalar * progress).replace(",", ".") + sizeUnit);

                    } catch (Exception e) {
//                        torrents.get(i).setDownloaded(size);
                    }

                    String infoString = "";


                    // Info
                    infoString = torrents.get(i).getDownloaded() + " " + Character.toString('\u2193') + " " + torrents.get(i).getDlspeed() + " "
                            + Character.toString('\u2191') + " " + torrents.get(i).getUpspeed() + " " + Character.toString('\u2022') + " "
                            + torrents.get(i).getRatio() + " " + Character.toString('\u2022') + " " + torrents.get(i).getEta();

                    // Set info
//                    torrents.get(i).setInfo(infoString);
                }

                Iterator it;

                last_completed = new HashMap<String, Torrent>();
                completed = new HashMap<String, Torrent>();
                notify = new HashMap<String, Torrent>();

                String[] completedHashesArray = completed_hashes.split("\\|");

                String completedHashes = null;

                String[] completedNames;

                for (int i = 0; i < completedHashesArray.length; i++) {
                    //Log.i("Debug", "NS - Last completed - " + completedHashesArray[i]);
                    last_completed.put(completedHashesArray[i], null);
                }

                if (torrents != null) {

                    // Check torrents
                    for (int i = 0; i < torrents.size(); i++) {

                        // Completed torrents
//                        if (torrents.get(i).getPercentage().equals("100")) {
                        if (("" + torrents.get(i).getProgress()).equals("100")) {
                            completed.put(torrents.get(i).getHash(), torrents.get(i));

                            // Build  completed hashes string here
                            if (completedHashes == null) {
                                completedHashes = torrents.get(i).getHash();
                            } else {
                                completedHashes += "|" + torrents.get(i).getHash();
                            }
                        }
                    }

                    // Save completedHashes
                    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPrefs.edit();

                    // Save hashes
                    editor.putString("completed_hashes" + currentServer, completedHashes);

                    // Commit changes
                    editor.apply();

                    if (completed_hashes.equals("")) {
                        last_completed = completed;
                    }

                    // Check completed torrents not seen last time
                    it = completed.entrySet().iterator();

                    while (it.hasNext()) {

                        HashMap.Entry pairs = (HashMap.Entry) it.next();

                        String key = (String) pairs.getKey();
                        Torrent torrent = (Torrent) pairs.getValue();

                        if (!last_completed.containsKey(key)) {
                            if (!notify.containsKey(key)) {
                                notify.put(key, torrent);
                            }
                        }
                    }

                    // Notify completed torrents
                    if (notify.size() > 0) {

                        String info = "";

                        //Log.i("Debug", "NS Downloads completed");

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("from", "NotifierService");
                        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        it = notify.entrySet().iterator();

                        while (it.hasNext()) {

                            HashMap.Entry pairs = (HashMap.Entry) it.next();

                            Torrent t = (Torrent) pairs.getValue();

                            if (info.equals("")) {
                                info += t.getName();
                            } else {
                                info += ", " + t.getName();
                            }

                        }

                        // Build notification
                        // the addAction re-use the same intent to keep the example short
                        Notification.Builder builder = new Notification.Builder(context)
                                .setContentTitle(NotifierService.context.getString(R.string.notifications_completed_torrents))
                                .setContentText(info)
                                .setNumber(notify.size())
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true);


                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                        Notification notification;

                        if (android.os.Build.VERSION.SDK_INT >= 16) {

                            // Define and Inbox
                            InboxStyle inbox = new Notification.InboxStyle(builder);

                            inbox.setBigContentTitle(NotifierService.context.getString(R.string.notifications_completed_torrents));

                            completedNames = info.split(",");

                            for (int j = 0; j < completedNames.length && j < 4; j++) {

                                if (completedNames[j] != null)
                                inbox.addLine(completedNames[j].trim());
                            }

                            inbox.setSummaryText(NotifierService.context.getString(R.string.notifications_total));
                            notification = inbox.build();

                        } else {
                            notification = builder.getNotification();
                        }

                        notificationManager.notify(0, notification);
                    }
                }

                // Reporting
                if (CustomLogger.isMainActivityReporting()) {
                    CustomLogger.saveReportMessage("Main", "qBittorrentTask - result length: " + torrents.size());
                    //CustomLogger.saveReportMessage("Main", "qBittorrentTask - httpStatusCode: " + httpStatusCode);
                }
            }

        });

    }

    // End of wraps

}


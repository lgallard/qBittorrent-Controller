package com.lgallardo.qbittorrentclient;

import android.app.Notification;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lgallard on 2/22/15.
 */
public class NotifierService extends BroadcastReceiver {

    public static String qb_version = "3.1.x";
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
    protected static int httpStatusCode = 0;
    protected static int currentServer;
    protected static boolean enable_notifications;

    private static String[] params = new String[2];
    private static Context context;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;
    private String qbQueryString = "query";


    public NotifierService() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        getSettings();

//        Log.i("Notifier", "Cookie:" + cookie);
//        Log.i("Notifier", "hostname: " + hostname);
//        Log.i("Notifier", "port: " + port);
//        Log.i("Notifier", "usernmae: " + username);
//        Log.i("Notifier", "password: " + password);
//        Log.i("Notifier", "qb_version: " + qb_version);
//        Log.i("Notifier", "currentServer: " + currentServer);
//        Log.i("Notifier", "enable_notifications: " + enable_notifications);

        if (enable_notifications) {

            String state = "all";

            // Get Settings thr params?

            if (qb_version.equals("2.x")) {
                qbQueryString = "json";
                params[0] = qbQueryString + "/events";
            }

            if (qb_version.equals("3.1.x")) {
                qbQueryString = "json";
                params[0] = qbQueryString + "/torrents";
            }

            if (qb_version.equals("3.2.x")) {
                qbQueryString = "query";
                params[0] = qbQueryString + "/torrents?filter=" + state;

                if (cookie == null || cookie.equals("")) {
                    new qBittorrentCookie().execute();
                }

//            Log.i("onReceive", "Cookie:" + cookie);

            }

            params[1] = state;

//            Log.i("Notifier", "onReceive reached");
            new FetchTorrentListTask().execute(params);

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


        qb_version = sharedPrefs.getString("qb_version", "3.1.x");


        cookie = sharedPrefs.getString("qbCookie2", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);

        // Notifications
        enable_notifications = sharedPrefs.getBoolean("enable_notifications", false);
        completed_hashes = sharedPrefs.getString("completed_hashes" + currentServer, "");


    }

    class FetchTorrentListTask extends AsyncTask<String, Integer, Torrent[]> {

        // Torrent Info TAGs
        protected static final String TAG_NAME = "name";
        protected static final String TAG_SIZE = "size";
        protected static final String TAG_PROGRESS = "progress";
        protected static final String TAG_STATE = "state";
        protected static final String TAG_HASH = "hash";
        protected static final String TAG_DLSPEED = "dlspeed";
        protected static final String TAG_UPSPEED = "upspeed";
        protected static final String TAG_NUMLEECHS = "num_leechs";
        protected static final String TAG_NUMSEEDS = "num_seeds";
        protected static final String TAG_RATIO = "ratio";
        protected static final String TAG_PRIORITY = "priority";
        protected static final String TAG_ETA = "eta";

        @Override
        protected Torrent[] doInBackground(String... params) {

            String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, eta, uploadSpeed, downloadSpeed;

            Torrent[] torrents = null;

            // Get settings
            getSettings();

            JSONParser jParser;

            int httpStatusCode = 0;

//            Log.i("Notifier", "Getting torrents");

            try {
                // Creating new JSON Parser
                jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

                jParser.setCookie(cookie);

                JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);


                if (jArray != null) {

                    torrents = new Torrent[jArray.length()];


                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        name = json.getString(TAG_NAME);
                        size = json.getString(TAG_SIZE).replace(",", ".");
                        progress = String.format("%.2f", json.getDouble(TAG_PROGRESS) * 100) + "%";
                        progress = progress.replace(",", ".");
                        info = "";
                        state = json.getString(TAG_STATE);
                        hash = json.getString(TAG_HASH);
                        ratio = json.getString(TAG_RATIO).replace(",", ".");
                        leechs = json.getString(TAG_NUMLEECHS);
                        seeds = json.getString(TAG_NUMSEEDS);
                        priority = json.getString(TAG_PRIORITY);
                        eta = json.getString(TAG_ETA);
                        downloadSpeed = json.getString(TAG_DLSPEED);
                        uploadSpeed = json.getString(TAG_UPSPEED);

                        torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, leechs, seeds, priority, eta, downloadSpeed, uploadSpeed, false, false);


                        // Get torrent generic properties

                        try {
                            // Calculate total downloaded
                            Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                            String sizeUnit = size.substring(size.indexOf(" "), size.length());

                            torrents[i].setDownloaded(String.format("%.1f", sizeScalar * json.getDouble(TAG_PROGRESS)).replace(",", ".") + sizeUnit);

                        } catch (Exception e) {
                            torrents[i].setDownloaded(size);
                        }

                        // Info
                        torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + torrents[i].getEta());

                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
                torrents = null;
                Log.e("Notifier", e.toString());

            } catch (Exception e) {
                torrents = null;
                Log.e("Notifier:", e.toString());
            }

            return torrents;

        }

        @Override
        protected void onPostExecute(Torrent[] torrents) {

            Iterator it;

            last_completed = new HashMap<String, Torrent>();
            completed = new HashMap<String, Torrent>();
            notify = new HashMap<String, Torrent>();

            String[] completedHashesArray = completed_hashes.split("\\|");

            String completedHashes = null;

            String[] completedNames;


            for (int i = 0; i < completedHashesArray.length; i++) {
//                Log.i("Notifier", "Last completed - " + completedHashesArray[i]);
                last_completed.put(completedHashesArray[i], null);
            }

//            Log.i("Notifier", "LastCompleted Size: " + last_completed.size());
//            Log.i("Notifier", "LastCompleted Hashes: " + completed_hashes);

            if (torrents != null) {

                // Check torrents
                for (int i = 0; i < torrents.length; i++) {

                    // Completed torrents
                    if (torrents[i].getPercentage().equals("100")) {


                        completed.put(torrents[i].getHash(), torrents[i]);

                        // Build  completed hashes string here
                        if (completedHashes == null) {
                            completedHashes = torrents[i].getHash();
                        } else {
                            completedHashes += "|" + torrents[i].getHash();
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

//                    Log.i("Notifier", "Downloads completed");


                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from", "NotifierService");
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    it = notify.entrySet().iterator();

                    while (it.hasNext()) {

                        HashMap.Entry pairs = (HashMap.Entry) it.next();

                        Torrent t = (Torrent) pairs.getValue();

                        if (info.equals("")) {
                            info += t.getFile();
                        } else {
                            info += ", " + t.getFile();
                        }

//                        it.remove(); // avoids a ConcurrentModificationException
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
        }

    }

    private class qBittorrentCookie extends AsyncTask<Void, Integer, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {

            // Get values from preferences
            getSettings();


            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String cookie = "";
            String api = "";


            try {

                cookie = jParser.getNewCookie();
//                api = jParser.getApiVersion();

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();
                Log.i("Notifier", "httpStatusCode: " + httpStatusCode);

            }

            if (cookie == null) {
                cookie = "";

            }

            if (api == null) {
                api = "";

            }

            return new String[]{cookie, api};

        }

        @Override
        protected void onPostExecute(String[] result) {


            cookie = result[0];


            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qbCookie2", result[0]);


            // Commit changes
            editor.apply();

        }
    }

}


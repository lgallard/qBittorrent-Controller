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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by lgallard on 2/22/15.
 */
public class RSSService extends BroadcastReceiver {

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
    private static String rss_feeds;


    public RSSService() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        getSettings();


        String state = "all";

        // Get Settings params?

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

        // Refresh all RSS feeds
        new rssFeedsTask().execute();


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

        // Get values from options
        rss_feeds = sharedPrefs.getString("rss_feeds", "");


    }


    public void saveRssFeed(String title, String link, String pubDate, boolean autoDownload, boolean notifyNew) {

        String autoDownloadValue = Boolean.toString(autoDownload);
        String notifyNewValue = Boolean.toString(notifyNew);


        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Save rss_feeds
        if (rss_feeds.equals("")) {

            rss_feeds = title + ";" + link + ";" + pubDate + ";" + autoDownloadValue + ";" + notifyNewValue;

        } else {
            rss_feeds = rss_feeds + "|" + title + ";" + link + ";" + pubDate + ";" + autoDownloadValue + ";" + notifyNewValue;

        }

        Log.d("Debug", "rss_feeds: " + rss_feeds);

        editor.putString("rss_feeds", rss_feeds);
        // Commit changes
        editor.apply();


    }


    private class qBittorrentCommand extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            com.lgallardo.qbittorrentclient.JSONParser jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            jParser.setCookie(cookie);

            try {

                jParser.postCommand(params[0], params[1]);

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();

            }

            return params[0];

        }

        @Override
        protected void onPostExecute(String result) {

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

    // Here is where the action happens
    private class rssFeedsTask extends AsyncTask<String, Integer, ArrayList<RSSFeed>> {
        @Override
        protected ArrayList<RSSFeed> doInBackground(String... params) {


            ArrayList<RSSFeed> feeds = new ArrayList<RSSFeed>();

            String[] rss_feeds_lines = rss_feeds.split("\\|");


            for (int i = 0; rss_feeds_lines.length > i; i++) {


                String[] feedValues = rss_feeds_lines[i].split(";");


                // Retrieve feed
                if (feedValues.length > 0 && !feedValues[0].isEmpty()) {

                    if (feedValues.length > 1) {

                        RSSFeed rssFeed = new RSSFeed();

                        String filter = "";

                        if(feedValues.length == 6){
                            filter = feedValues[5];
                        }

                        try {

                            RSSFeedParser rssFeedParser = new RSSFeedParser();
                            rssFeed.setChannelFilter(filter);
                            rssFeed = rssFeedParser.getRSSFeed(feedValues[0], feedValues[1], filter);
                            rssFeed.setAutodDownload(Boolean.parseBoolean(feedValues[3]));
                            rssFeed.setNotifyNew(Boolean.parseBoolean(feedValues[4]));

                        } catch (Exception e) {
                            Log.e("Debug", e.getMessage());
                            Log.e("Debug", e.toString());
                            e.printStackTrace();
                        }

                        // Add feed, no matter if it's empty
                        feeds.add(rssFeed);


                    }

                }

            }

            return feeds;
        }

        @Override
        protected void onPostExecute(ArrayList<RSSFeed> result) {

            boolean gotNewItems= false;

            if (result != null && result.size() > 0) {

                String info = "";

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("from", "RSSService");
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Build notification
                // the addAction re-use the same intent to keep the example short
                Notification.Builder builder = new Notification.Builder(context)
                        .setContentTitle(RSSService.context.getString(R.string.notifications_rss_available))
                        .setContentText(info)
                        .setSmallIcon(R.drawable.ic_rss_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);


                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                Notification notification = null;

                if (android.os.Build.VERSION.SDK_INT >= 16) {

                    // Define and Inbox
                    InboxStyle inbox = new InboxStyle(builder);

                    inbox.setBigContentTitle(RSSService.context.getString(R.string.notifications_rss_available));

                    for (int i = 0; i < result.size(); i++) {

                        // Notify new torrents
                        RSSFeed rssFeed = result.get(i);

                        ArrayList<RSSFeedItem> items = rssFeed.getItems();

                        for (int j = 0; items != null && j < items.size(); j++) {

                            // Notify new available torrents
                            if (rssFeed.getNotifyNew() && j < 4) {


                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                                boolean notifyFeed = false;

                                try {

                                    Date channelPubDate = sdf.parse(rssFeed.getChannelPubDate());
                                    Date itemPubDate = sdf.parse(items.get(j).getPubDate());

                                    // itemPubDate is after channelPubDate
                                    if (itemPubDate.compareTo(channelPubDate) > 0) {
                                        notifyFeed = true;
                                        gotNewItems = true;
                                    }

                                } catch (Exception e) {
                                    Log.e("Debug", "RSS Service: " + e.toString());
                                }

                                if (notifyFeed) {
                                    inbox.addLine(items.get(j).getTitle());

                                }
                            }


                            // Send torrent for autodownload
                            if (rssFeed.getAutodDownload()) {

                                // Execute the task in background
                                qBittorrentCommand qtc = new qBittorrentCommand();
                                qtc.execute(new String[]{"addTorrent", items.get(j).getTorrentUrl()});

                            }

                            // Save new Channel pubDate
                            rssFeed.setChannelPubDate(items.get(0).getPubDate());

                            //Save modified feed into result
                            result.set(i, rssFeed);


                        }

                    }

                    // Save all;
                    rss_feeds = "";
                    for (int k = 0; k < result.size(); k++) {
                        RSSFeed rssFeed = result.get(k);

                        saveRssFeed(rssFeed.getChannelTitle(), rssFeed.getChannelLink(), rssFeed.getChannelPubDate(), rssFeed.getAutodDownload(), rssFeed.getNotifyNew());
                    }


                    if(gotNewItems) {
                        inbox.setSummaryText(result.get(0).getChannelTitle());
                        notification = inbox.build();
                    }

                } else {
                    notification = builder.getNotification();

                    for (int i = 0; i < result.size(); i++) {
                        // Notify new torrents
                        RSSFeed rssFeed = result.get(i);

                        ArrayList<RSSFeedItem> items = rssFeed.getItems();

                        // Save new Channel pubDate
                        rssFeed.setChannelPubDate(items.get(0).getPubDate());

                        //Save modified feed into result
                        result.set(i, rssFeed);
                    }


                    // Save all;
                    rss_feeds = "";
                    for (int k = 0; k < result.size(); k++) {
                        RSSFeed rssFeed = result.get(k);

                        saveRssFeed(rssFeed.getChannelTitle(), rssFeed.getChannelLink(), rssFeed.getChannelPubDate(), rssFeed.getAutodDownload(), rssFeed.getNotifyNew());
                    }
                }

                if(gotNewItems && notification != null) {
                    notificationManager.notify(0, notification);
                }

            }

        }
    }


}


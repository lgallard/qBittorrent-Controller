package com.lgallardo.qbittorrentclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RSSFeedActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static RSSFeedChannelListAdapter myadapter;
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;
    private static String rss_feeds;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static RSSFeed rssInfo;

    public static ArrayList<RSSFeed> rssFeeds = new ArrayList<RSSFeed>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssfeed);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (MainActivity.dark_ui) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Theme_Dark_primary));
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Set App title
        setTitle(R.string.action_rss);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.rss_refresh_layout);


        if (mSwipeRefreshLayout != null) {

            mSwipeRefreshLayout.setColorSchemeColors(R.color.primary, R.color.primary_dark, R.color.primary_text);

            mSwipeRefreshLayout.setRefreshing(true);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    // Refresh all RSS feeds
                    new rssFeedsTask().execute();
                }
            });
        }



        ArrayList<RSSFeed> rssChannels = new ArrayList<RSSFeed>();
//        RSSFeed rssFeed;
//
//        // Preferences stuff
//        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        builderPrefs = new StringBuilder();
//
//        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));
//
//        // Get values from options
//        rss_feeds = sharedPrefs.getString("rss_feeds", "");
//
//        Log.d("Debug", "rss_feeds: " + rss_feeds.toString());
//
//        String[] rss_feeds_lines = rss_feeds.split("\\|");
//
//
//        for (int i = 0; rss_feeds_lines.length > i; i++) {
//
//
//            String[] feedValues = rss_feeds_lines[i].split(";");
//
//            // New RSSFeed
//
//            rssFeed = new RSSFeed();
//
//
//            // Add line
//            if (feedValues.length > 0 && !feedValues[0].isEmpty()) {
//
//                rssFeed.setChannelTitle(feedValues[0]);
//
//                if (feedValues.length > 1) {
//                    rssFeed.setChannelLink(feedValues[1]);
//                }
//
//                if (feedValues.length > 2) {
//                    rssFeed.setChannelPubDate(feedValues[2]);
//                }
//
//                if (feedValues.length > 3) {
//
//                    if(feedValues[3].equals("true")){
//                        rssFeed.setAutodDownload(true);
//                    }
//                    else{
//                        rssFeed.setAutodDownload(false);
//                    }
//                }
//
//                if (feedValues.length > 4) {
//
//                    if(feedValues[4].equals("true")){
//                        rssFeed.setNotifyNew(true);
//                    }
//                    else{
//                        rssFeed.setNotifyNew(false);
//                    }
//                }
//
//
//                rssChannels.add(rssFeed);
//
//            }
//
//
//        }

        // Get ListView object from xml
        ListView listView = (ListView) findViewById(R.id.channel_list);

        myadapter = new RSSFeedChannelListAdapter(this, rssChannels);

        listView.setAdapter(myadapter);

//        myadapter.notifyDataSetChanged();



        new rssFeedsTask().execute();


        // If it were awaked from an intent-filter,
        // get intent from the intent filter and Add URL torrent
        handleIntent(getIntent());
    }


    public void saveRssFeed(String title, String link, String pubDate, boolean autoDownload, boolean notifyNew) {

        String autoDownloadValue= Boolean.toString(autoDownload);
        String notifyNewValue = Boolean.toString(notifyNew);



        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Save rss_feeds


        if(rss_feeds == null || rss_feeds.equals("")){
            editor.putString("rss_feeds", title + ";" + link + ";" +pubDate +  ";" + autoDownloadValue + ";" + notifyNew);

        }
        else{
            editor.putString("rss_feeds", rss_feeds + "|" + title + ";" + link + ";" +pubDate +  ";" + autoDownloadValue + ";" + notifyNew);

        }

        // Commit changes
        editor.commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rssfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_addRss:
                // Add URL torrent
                addRssFeed(new RSSFeed());
                return true;
            case R.id.action_refreshRss:

                new rssFeedsTask().execute();

                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRssFeed(RSSFeed rssFeed) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View addTorrentView = li.inflate(R.layout.add_rss, null);

        // URL input
        final EditText rssFeedUrlInput = (EditText) addTorrentView.findViewById(R.id.rssFeedUrl);
        final EditText rssFeedNameInput = (EditText) addTorrentView.findViewById(R.id.rssFeedName);
        final CheckBox rssFeedAutoDownloadInput = (CheckBox) addTorrentView.findViewById(R.id.rssFeedAutodownload);
        final CheckBox rssFeedNotifyNewInput = (CheckBox) addTorrentView.findViewById(R.id.rssFeedNotifyNew);

        if (rssFeed != null) {

            rssFeedUrlInput.setText(rssFeed.getChannelLink());

            if(rssFeed.getChannelTitle() != null){
                rssFeedNameInput.setText(rssFeed.getChannelTitle());
            }


            if (!isFinishing()) {
                // Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Set add_torrent.xml to AlertDialog builder
                builder.setView(addTorrentView);

                // Cancel
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Ok
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User accepted the dialog
                        Log.d("Debug", "RSS feed: " + rssFeedUrlInput.getText().toString());

                        // Get values from dialog view
                        String title = rssFeedNameInput.getText().toString();
                        String link = rssFeedUrlInput.getText().toString();
                        boolean autoDownload = rssFeedAutoDownloadInput.isChecked() ? true:false;
                        boolean notifyNew = rssFeedNotifyNewInput.isChecked() ? true:false;


                        //Save RSS feed

                        if (link != null && !link.isEmpty()) {

                            if (title == null || title.isEmpty()) {
                                title = link;
                            }

                            saveRssFeed(title, link, "", autoDownload, notifyNew);

                            // Add channel to adapter

                            myadapter.addChannel(new RSSFeed(title, link, autoDownload, notifyNew));
                            myadapter.notifyDataSetChanged();


                        }


                    }
                });

                // Create dialog
                AlertDialog dialog = builder.create();

                // Show dialog
                dialog.show();
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {


        if (intent != null) {

            // Add Rss from url
            String rssUrl = intent.getDataString();

            Log.d("Debug", "RSS url: " + rssUrl);

            if(rssUrl != null) {

                RSSChannelInfoTask rssInfoTask = new RSSChannelInfoTask();

                rssInfoTask.execute(rssUrl);
            }

        }

    }

    // Here is where the action happens
    private class RSSChannelInfoTask extends AsyncTask<String, Integer, RSSFeed> {
        @Override
        protected RSSFeed doInBackground(String... params) {

            RSSFeed rssFeed = new RSSFeed();

            try {

                RSSFeedParser rssFeedParser = new RSSFeedParser();
                rssFeed = rssFeedParser.getRSSChannelInfo(params[0]);
            } catch (Exception e) {
                Log.e("Debug", e.getMessage());
                Log.e("Debug", e.toString());
                e.printStackTrace();

                rssFeed = null;
            }

            // Set rss link
            rssFeed.setChannelLink(params[0]);

            return rssFeed;
        }

        @Override
        protected void onPostExecute(RSSFeed result) {


            if (result != null) {
                Log.d("Debug", "> Channel Title: " + result.getChannelTitle());
                Log.d("Debug", "> Channel Link: " + result.getChannelLink());


                rssInfo = result;

                // Add rssInfoTask result
                addRssFeed(rssInfo);


//                myadapter.addChannel(result);
//                myadapter.notifyDataSetChanged();

            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }

        }


    }

    // Here is where the action happens
    private class rssFeedsTask extends AsyncTask<String, Integer, ArrayList<RSSFeed>> {
        @Override
        protected ArrayList<RSSFeed> doInBackground(String... params) {


            // Preferences stuff
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

            builderPrefs = new StringBuilder();

            builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

            // Get values from options
            rss_feeds = sharedPrefs.getString("rss_feeds", "");

            ArrayList<RSSFeed> feeds = new ArrayList<RSSFeed>();

            String[] rss_feeds_lines = rss_feeds.split("\\|");


            for (int i = 0; rss_feeds_lines.length > i; i++) {


                String[] feedValues = rss_feeds_lines[i].split(";");

//                Log.d("3Debug", "rss_feeds_lines[i]: " + rss_feeds_lines[i]);
//                Log.d("3Debug", "feedValues[0]: " + feedValues[0]);
//
//                try {
//                    Log.d("3Debug", "feedValues[1]: " + feedValues[1]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    Log.d("3Debug", "feedValues[2]: " + feedValues[2]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                // Retrive feed
                if (feedValues.length > 0 && !feedValues[0].isEmpty()) {

                    if (feedValues.length > 1) {

                        RSSFeed rssFeed = new RSSFeed();

                        try {

                            RSSFeedParser rssFeedParser = new RSSFeedParser();
                            rssFeed = rssFeedParser.getRSSFeed(feedValues[0], feedValues[1]);

                        } catch (Exception e) {
                            Log.e("3Debug", e.getMessage());
                            Log.e("3Debug", e.toString());
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

            if (result != null) {

                myadapter.setRssChannels(result);
                myadapter.notifyDataSetChanged();


            }


            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}


package com.lgallardo.qbittorrentclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
    private AdView adView;

    // Values from MainActivity
    private String packageName;
    private boolean dark_ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If it were awaken from an intent-filter,
        // get intent from the intent filter and Add URL torrent
        handleIntent(getIntent());

//        Log.d("Debug", "RSSFeedActivity - onCreate ");

        // Set Theme (It must be fore inflating or setContentView)
        if (dark_ui) {
            this.setTheme(R.style.Theme_Dark);

            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.Theme_Dark_toolbarBackground));
                getWindow().setStatusBarColor(getResources().getColor(R.color.Theme_Dark_toolbarBackground));
            }
        } else {
            this.setTheme(R.style.Theme_Light);

            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
            }

        }

        setContentView(R.layout.activity_rssfeed);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (dark_ui) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Theme_Dark_primary));
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Set App title
        setTitle(R.string.action_rss);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.rss_refresh_layout);


        if (mSwipeRefreshLayout != null) {

            TypedValue typed_value = new TypedValue();
            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) * 2);

            mSwipeRefreshLayout.setEnabled(false);

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

        // Get ListView object from xml
        ListView listView = (ListView) findViewById(R.id.channel_list);

        // Listener for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v, pos, id);
            }
        });


        myadapter = new RSSFeedChannelListAdapter(this, rssChannels);

        listView.setAdapter(myadapter);

        // This is need for the Contextual menu
        registerForContextMenu(listView);



        // Load Ads
        loadBanner();
    }



    private void onListItemClick(View v, int pos, long id) {
//        Log.i("Debug", "onListItemClick id=" + id);
//        Log.i("Debug", "onListItemClick pos=" + pos);

        if(myadapter.getRssChannels().get(pos).getItemCount() > 0) {

            Intent intent = new Intent(getBaseContext(), com.lgallardo.qbittorrentclient.RSSItemActivity.class);
            intent.putExtra("position", pos);
            intent.putExtra("packageName", packageName);
            intent.putExtra("dark_ui", dark_ui);
            startActivity(intent);
        }

    }


    public void saveRssFeed(String title, String link, String pubDate, boolean autoDownload, boolean notifyNew) {

        String autoDownloadValue = Boolean.toString(autoDownload);
        String notifyNewValue = Boolean.toString(notifyNew);


        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Save rss_feeds
        if(rss_feeds == null){
            // Get values from options
            rss_feeds = sharedPrefs.getString("rss_feeds", "");
        }

        // Encode link
        try {

            link = URLEncoder.encode(link, "UTF-8");

//            Log.d("Debug", "RSSFeedActivity - Uri.decoded: " + URLDecoder.decode(link_encoded,"UTF-8"));

        } catch (UnsupportedEncodingException e) {
            Log.d("Debug", "RSSFeedActivity - Error encoding link: " + link);
        }


        if (rss_feeds.equals("")) {

            rss_feeds = title + ";" + link + ";" + pubDate + ";" + autoDownloadValue + ";" + notifyNewValue;

        } else {
            rss_feeds = rss_feeds + "|" + title + ";" + link + ";" + pubDate + ";" + autoDownloadValue + ";" + notifyNewValue;

        }

//        Log.d("Debug", "rss_feeds: " + rss_feeds);

        editor.putString("rss_feeds", rss_feeds);
        // Commit changes
        editor.apply();


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            return;
        }

        if (v.getId() == R.id.channel_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;


//            Log.d("Debug", "Chosen: " + info.id);
//            Log.d("Debug", "Chosen: " + info.position);


            getMenuInflater().inflate(R.menu.menu_rssrow_contextual, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

//        Log.d("Debug", "Item: " + getResources().getResourceEntryName(item.getItemId()));


        ArrayList<RSSFeed> rssChannels = myadapter.getRssChannels();

        switch (item.getItemId()) {

            case R.id.action_edit:
//                Log.d("Debug", "Edit!");

                    editRssFeed(info.position, rssChannels.get(info.position));

                return true;
            case R.id.action_delete:
//                Log.d("Debug", "Delete!");

                rssChannels.remove(info.position);

                myadapter.setRssChannels(rssChannels);
                myadapter.notifyDataSetChanged();

                // Save all;
                rss_feeds = "";
                for (int i = 0; i < rssChannels.size(); i++) {
                    RSSFeed rssFeed = rssChannels.get(i);
                    saveRssFeed(rssFeed.getChannelTitle(), rssFeed.getChannelLink(), rssFeed.getChannelPubDate(), rssFeed.getAutodDownload(), rssFeed.getNotifyNew());
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_addRss:
                // Add URL torrent
                addRssFeed(new RSSFeed());
                return true;
            case R.id.action_refreshRss:
                mSwipeRefreshLayout.setRefreshing(true);
                new rssFeedsTask().execute();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_edit:
                return true;
            case R.id.action_delete:
//                Log.d("Debug", "Delete!");
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

            if (rssFeed.getChannelTitle() != null) {
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
                        mSwipeRefreshLayout.setRefreshing(true);
                        new rssFeedsTask().execute();

                    }
                });

                // Ok
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User accepted the dialog
//                        Log.d("Debug", "RSS feed: " + rssFeedUrlInput.getText().toString());

                        // Get values from dialog view
                        String title = rssFeedNameInput.getText().toString();
                        String link = rssFeedUrlInput.getText().toString();
                        boolean autoDownload = rssFeedAutoDownloadInput.isChecked() ? true : false;
                        boolean notifyNew = rssFeedNotifyNewInput.isChecked() ? true : false;


                        //Save RSS feed

                        if (link != null && !link.isEmpty()) {

                            if (title == null || title.isEmpty()) {
                                title = link;
                            }

                            saveRssFeed(title, link, "", autoDownload, notifyNew);


                            // Refresh channel list
                            mSwipeRefreshLayout.setRefreshing(true);
                            new rssFeedsTask().execute();


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

    private void editRssFeed(final int position, final RSSFeed rssFeed) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View addTorrentView = li.inflate(R.layout.add_rss, null);

        // URL input
        final EditText rssFeedUrlInput = (EditText) addTorrentView.findViewById(R.id.rssFeedUrl);
        final EditText rssFeedNameInput = (EditText) addTorrentView.findViewById(R.id.rssFeedName);
        final CheckBox rssFeedAutoDownloadInput = (CheckBox) addTorrentView.findViewById(R.id.rssFeedAutodownload);
        final CheckBox rssFeedNotifyNewInput = (CheckBox) addTorrentView.findViewById(R.id.rssFeedNotifyNew);


        // Set dialog's values
        rssFeedUrlInput.setText(rssFeed.getChannelLink());
        rssFeedNameInput.setText(rssFeed.getChannelTitle());
        rssFeedAutoDownloadInput.setChecked(rssFeed.getAutodDownload());
        rssFeedNotifyNewInput.setChecked(rssFeed.getNotifyNew());

        if (rssFeed != null) {


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
//                        Log.d("Debug", "RSS feed: " + rssFeedUrlInput.getText().toString());

                        // Get values from dialog view
                        String title = rssFeedNameInput.getText().toString();
                        String link = rssFeedUrlInput.getText().toString();
                        boolean autoDownload = rssFeedAutoDownloadInput.isChecked() ? true : false;
                        boolean notifyNew = rssFeedNotifyNewInput.isChecked() ? true : false;


                        //Save RSS feed

                        if (link != null && !link.isEmpty()) {

                            if (title == null || title.isEmpty()) {
                                title = link;
                            }

//                            saveRssFeed(title, link, "", autoDownload, notifyNew);

                            rssFeed.setChannelTitle(title);
                            rssFeed.setChannelLink(link);
                            rssFeed.setAutodDownload(autoDownload);
                            rssFeed.setNotifyNew(notifyNew);
//
//
//                            // Refresh channel list
//                            mSwipeRefreshLayout.setRefreshing(true);
//                            new rssFeedsTask().execute();

                            ArrayList<RSSFeed> rssChannels = myadapter.getRssChannels();

                            rssChannels.set(position, rssFeed);


                            myadapter.setRssChannels(rssChannels);
                            myadapter.notifyDataSetChanged();

                            // Save all;
                            rss_feeds = "";
                            for (int i = 0; i < rssChannels.size(); i++) {
                                RSSFeed rssFeed = rssChannels.get(i);

//                                Log.d("Debug", "Saving: " + rssFeed.getChannelTitle() + ";" +  rssFeed.getChannelLink() + ";" +  rssFeed.getChannelPubDate() + ";" +  rssFeed.getAutodDownload() + ";" + rssFeed.getNotifyNew());


                                saveRssFeed(rssFeed.getChannelTitle(), rssFeed.getChannelLink(), rssFeed.getChannelPubDate(), rssFeed.getAutodDownload(), rssFeed.getNotifyNew());
                            }


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

//        Log.d("Debug", "RSSFeedActivity - handleIntent ");

        if (intent != null) {

//            Log.d("Debug", "RSSFeedActivity - intent is not null ");

            // Get package name
            packageName = intent.getStringExtra("packageName");

//            Log.d("Debug", "RSSFeedActivity - packageName: " + packageName);

            // Get theme UI preference
            dark_ui = intent.getBooleanExtra("dark_ui", false);

//            Log.d("Debug", "RSSFeedActivity - dark_ui: " + dark_ui);

            // Add Rss from url
            String rssUrl = intent.getDataString();

            if (rssUrl != null) {
                RSSChannelInfoTask rssInfoTask = new RSSChannelInfoTask();
                rssInfoTask.execute(rssUrl);
            }
            else {

                new rssFeedsTask().execute();
            }

        }

    }

    // Load Banner
    public void loadBanner() {

        try {
            if (packageName.equals("com.lgallardo.qbittorrentclient")) {

                // Look up the AdView as a resource and load a request.
                adView = (AdView) this.findViewById(R.id.adViewRssFeed);
                AdRequest adRequest = new AdRequest.Builder().build();

                // Start loading the ad in the background.
                adView.loadAd(adRequest);
            }
        } catch (Exception e) {

            Log.e("Debug", e.getMessage());
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
//                Log.d("Debug", "> Channel Title: " + result.getChannelTitle());
//                Log.d("Debug", "> Channel Link: " + result.getChannelLink());


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


                // Retrieve feed
                if (feedValues.length > 0 && !feedValues[0].isEmpty()) {

                    if (feedValues.length > 1) {

                        RSSFeed rssFeed = new RSSFeed();

                        try {

                            RSSFeedParser rssFeedParser = new RSSFeedParser();
                            rssFeed = rssFeedParser.getRSSFeed(feedValues[0], feedValues[1]);

//                            saveRssFeed(String title, String link, String pubDate, boolean autoDownload, boolean notifyNew) {
                            // Set downlaod and notify flags

//                            Log.d("Debug", "feedValues[0]: " + feedValues[0]);
//                            Log.d("Debug", "feedValues[1]: " + feedValues[1]);
//                            Log.d("Debug", "feedValues[2]: " + feedValues[2]);
//                            Log.d("Debug", "feedValues[3]: " + feedValues[3]);
//                            Log.d("Debug", "feedValues[4]: " + feedValues[4]);


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

            if (result != null) {

                myadapter.setRssChannels(result);
                myadapter.notifyDataSetChanged();


            }


            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);

        }
    }

}


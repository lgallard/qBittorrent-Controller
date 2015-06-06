package com.lgallardo.qbittorrentclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RSSFeedActivity extends AppCompatActivity {

    Toolbar toolbar;
    public RSSFeedChannelListAdapter myadapter;

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


        ArrayList<String> channels = new ArrayList<String>();


        // Get ListView object from xml
        ListView listView = (ListView) findViewById(R.id.channel_list);

        myadapter = new RSSFeedChannelListAdapter(this, channels);

        listView.setAdapter(myadapter);

        myadapter.notifyDataSetChanged();

        // If it were awaked from an intent-filter,
        // get intent from the intent filter and Add URL torrent
        handleIntent(getIntent());
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
                addRssFeed("");
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRssFeed(String url) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View addTorrentView = li.inflate(R.layout.add_rss, null);

        // URL input
        final EditText urlInput = (EditText) addTorrentView.findViewById(R.id.url);

        if (url != null) {
            urlInput.setText(url);


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
                        Log.d("Debug", "RSS feed: " + urlInput.getText().toString());

                        // Get Rss info and update listview
                        RSSChannelInfoTask rssInfo = new RSSChannelInfoTask();
                        rssInfo.execute(urlInput.getText().toString());

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

            addRssFeed(rssUrl);

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

            return rssFeed;
        }

        @Override
        protected void onPostExecute(RSSFeed result) {

            if (result != null) {

                Log.d("4Debug", "> Channel Title: " + result.getChannelTitle());
                Log.d("4Debug", "> Channel Link: " + result.getChannelLink());
                Log.d("4Debug", "> Channel Description: " + result.getChannelDescription());

                myadapter.addChannel(result.getChannelTitle());
                myadapter.notifyDataSetChanged();

            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }

        }


    }

    // Here is where the action happens
    private class rssFeedTask extends AsyncTask<String, Integer, RSSFeed> {
        @Override
        protected RSSFeed doInBackground(String... params) {

            RSSFeed rssFeed = new RSSFeed();

            try {

                RSSFeedParser rssFeedParser = new RSSFeedParser();
                rssFeed = rssFeedParser.getRSSFeed("https://yts.to/rss/0/all/all/7");

            } catch (Exception e) {
                Log.e("Debug", e.getMessage());
                Log.e("Debug", e.toString());
                e.printStackTrace();
            }

            return rssFeed;
        }

        @Override
        protected void onPostExecute(RSSFeed result) {

            if (result != null) {

                Log.d("4Debug", "> Channel Title: " + result.getChannelTitle());
                Log.d("4Debug", "> Channel Link: " + result.getChannelLink());
                Log.d("4Debug", "> Channel Description: " + result.getChannelDescription());


                for (RSSFeedItem item : result.getItems()) {

                    Log.d("4Debug", "    > Title: " + item.getTitle());
                    Log.d("4Debug", "    > Description: " + item.getDescription());
                    Log.d("4Debug", "    > Link: " + item.getLink());
                    Log.d("4Debug", "    > Torrent: " + item.getTorrentUrl());

                }
            }
        }

    }

}

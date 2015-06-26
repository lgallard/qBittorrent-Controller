package com.lgallardo.qbittorrentclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class RSSItemActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static int rssChannelPosition;
    public static RSSFeedItemListAdapter myItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Set Theme (It must be fore inflating or setContentView)
        if (MainActivity.dark_ui) {
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

        setContentView(R.layout.activity_rssitem);


        toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (MainActivity.dark_ui) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Theme_Dark_primary));

            // Force backgroud (for some weird reason this activity is not taking the dark background)

            // Find the root view
            View root = toolbar.getRootView();

            // Set the color
            root.setBackgroundColor(getResources().getColor(R.color.Theme_Dark_windowBackground));
        }



        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);


        // Set App title
        setTitle(R.string.action_rss);

        Intent intent = getIntent();

        if (intent != null) {
            rssChannelPosition = intent.getIntExtra("position", 0);
        }


        // Get Channel Feed
        RSSFeed rssFeed = (RSSFeedActivity.myadapter.getRssChannels()).get(rssChannelPosition);

        ArrayList<RSSFeedItem> items = rssFeed.getItems();

//        Log.d("Debug", "Items size: " + items.size());
//
//        for(int i=0; i < items.size(); i++){
//
//            Log.d("Debug", "Item Title: " + items.get(i).getTitle());
//        }


        // Get ListView object from xml
        ListView listView = (ListView) findViewById(R.id.items_list);

        // Listener for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v, pos, id);
            }
        });


        myItemsAdapter = new RSSFeedItemListAdapter(this, items);

        listView.setAdapter(myItemsAdapter);


    }

    @Override
    protected void onNewIntent(Intent intent) {

        rssChannelPosition = 0;

        if (intent != null) {
            rssChannelPosition = intent.getIntExtra("position", 0);
        }


    }

    private void onListItemClick(View v, int pos, long id) {
//        Log.i("Debug", "RSS Item - onListItemClick id=" + id);
//        Log.i("Debug", "RSS Item - onListItemClick pos=" + pos);

        RSSFeedItem item= myItemsAdapter.getItems().get(pos);

//        Log.i("Debug", "RSS Item - Title =" + item.getTitle());
//        Log.i("Debug", "RSS Item - Torrent URL =" + item.getTorrentUrl());
//        Log.i("Debug", "RSS Item - PubDate =" + item.getPubDate());

        Uri uri = Uri.parse(item.getTorrentUrl());


        // Send torrent to qBittorrent App
        Intent intent = new Intent(getBaseContext(), com.lgallardo.qbittorrentclient.MainActivity.class);

//        // Send torrent to generic torrent app
//        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(uri);

        intent.putExtra("from", "RSSItemActivity");
        startActivity(intent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_rssitem, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

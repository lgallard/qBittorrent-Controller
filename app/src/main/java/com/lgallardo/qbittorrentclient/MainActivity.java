/**
 * ****************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * Luis M. Gallardo D.
 * ****************************************************************************
 */
package com.lgallardo.qbittorrentclient;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

interface RefreshListener {
    public void swipeRefresh();
}

public class MainActivity extends AppCompatActivity implements RefreshListener {

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
    protected static final String TAG_SEQDL = "seq_dl";
    protected static final String TAG_FLPIECEPRIO = "f_l_piece_prio";
    protected static final String TAG_GLOBAL_MAX_NUM_CONNECTIONS = "max_connec";
    protected static final String TAG_MAX_NUM_CONN_PER_TORRENT = "max_connec_per_torrent";
    protected static final String TAG_MAX_NUM_UPSLOTS_PER_TORRENT = "max_uploads_per_torrent";
    protected static final String TAG_GLOBAL_UPLOAD = "up_limit";
    protected static final String TAG_GLOBAL_DOWNLOAD = "dl_limit";
    protected static final String TAG_ALT_UPLOAD = "alt_up_limit";
    protected static final String TAG_ALT_DOWNLOAD = "alt_dl_limit";
    protected static final String TAG_TORRENT_QUEUEING = "queueing_enabled";
    protected static final String TAG_MAX_ACT_DOWNLOADS = "max_active_downloads";
    protected static final String TAG_MAX_ACT_UPLOADS = "max_active_uploads";
    protected static final String TAG_MAX_ACT_TORRENTS = "max_active_torrents";
    protected static final String TAG_URL = "url";
    protected static final String TAG_SCHEDULER_ENABLED = "scheduler_enabled";
    protected static final String TAG_SCHEDULE_FROM_HOUR = "schedule_from_hour";
    protected static final String TAG_SCHEDULE_FROM_MIN = "schedule_from_min";
    protected static final String TAG_SCHEDULE_TO_HOUR = "schedule_to_hour";
    protected static final String TAG_SCHEDULE_TO_MIN = "schedule_to_min";
    protected static final String TAG_SCHEDULER_DAYS = "scheduler_days";


    protected static final int SETTINGS_CODE = 0;
    protected static final int OPTION_CODE = 1;
    protected static final int GETPRO_CODE = 2;
    protected static final int HELP_CODE = 3;

    // Cookie (SID - Session ID)
    public static String cookie = null;
    public static String qb_version = "3.2.x";
    public static LinearLayout headerInfo;

    // Current state
    public static String currentState;

    protected static com.lgallardo.qbittorrentclient.JSONParser jParser;

    // Preferences properties
    protected static String currentServer;
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean https;
    protected static boolean auto_refresh;
    protected static int refresh_period;
    protected static int connection_timeout;
    protected static int data_timeout;
    protected static String sortby;
    protected static boolean reverse_order;
    protected static boolean dark_ui;
    protected static String lastState;
    protected static long notification_period;
    protected static boolean header;

    // Option
    protected static String global_max_num_connections;
    protected static String max_num_conn_per_torrent;
    protected static String max_num_upslots_per_torrent;
    protected static String global_upload;
    protected static String global_download;
    protected static String alt_upload;
    protected static String alt_download;
    protected static boolean torrent_queueing;
    protected static String max_act_downloads;
    protected static String max_act_uploads;
    protected static String max_act_torrents;
    protected static long uploadSpeedCount;
    protected static long downloadSpeedCount;
    protected static int uploadCount;
    protected static int downloadCount;
    protected static boolean schedule_alternative_rate_limits;
    protected static String alt_from_hour;
    protected static String alt_from_min;
    protected static String alt_to_hour;
    protected static String alt_to_min;
    protected static String scheduler_days;

    static Torrent[] lines;
    static String[] names;

    // Params to get JSON Array
    private static String[] params = new String[2];
    public com.lgallardo.qbittorrentclient.ItemstFragment firstFragment;

    // myAdapter myadapter
    public TorrentListAdapter myadapter;

    // Http status code
    public int httpStatusCode = 0;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;

    // Drawer properties
    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] navigationDrawerItemTitles;
    private String[] navigationDrawerServerItems;
    //    private ListView drawerList;
    public static DrawerItemRecyclerViewAdapter rAdapter;
    protected RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    public static DrawerLayout drawerLayout;
    public static ActionBarDrawerToggle drawerToggle;
    public static final int DRAWER_ITEM_ACTIONS = 1;
    public static final int DRAWER_ITEM_SERVERS = 3;
    public static final int DRAWER_CATEGORY = 5;
    public static final int DRAWER_ITEM_TAGS = 6;

    // Fragments
    private AboutFragment secondFragment;
    private HelpFragment helpTabletFragment;
    private AboutFragment aboutFragment;

    private boolean okay = false;

    // Auto-refresh
    private Handler handler;
    private boolean canrefresh = true;

    // Ads View
    private AdView adView;

    // For checking if the app is visible
    private boolean activityIsVisible = true;

    // Item list position
    private int itemPosition = 0;

    // Searching field
    private String searchField = "";
    private String qbQueryString = "query";

    // Alarm manager
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    // New ToolBar in Material Desing
    Toolbar toolbar;
    public static boolean listViewRefreshing;

    // Search bar in Material Design
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText editSearch;

    // Packge info
    public static String packageName;

    // Action (states)
    public static final ArrayList<String> actionStates = new ArrayList<>(Arrays.asList("all", "downloading", "completed", "pause", "active", "inactive"));

    // Connection error counter
    private int connection400ErrorCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get preferences
        getSettings();

        // Set alarm for checking completed torrents, if not set
        if (PendingIntent.getBroadcast(getApplication(), 0, new Intent(getApplication(), NotifierService.class), PendingIntent.FLAG_NO_CREATE) == null) {

            // Set Alarm for checking completed torrents
            alarmMgr = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplication(), NotifierService.class);
            alarmIntent = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 5000,
                    notification_period, alarmIntent);
        }

        // Set alarm for RSS checking, if not set
        if (PendingIntent.getBroadcast(getApplication(), 0, new Intent(getApplication(), RSSService.class), PendingIntent.FLAG_NO_CREATE) == null) {

            // Set Alarm for checking completed torrents
            alarmMgr = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplication(), RSSService.class);
            alarmIntent = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 5000,
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }

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

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (dark_ui) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Theme_Dark_primary));
        }

        setSupportActionBar(toolbar);

        // Set App title
        setTitle(R.string.app_shortname);

        // Drawer menu
        navigationDrawerServerItems = getResources().getStringArray(R.array.qBittorrentServers);
        navigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//        drawerList = (ListView) findViewById(R.id.left_drawer);


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are


        ArrayList<ObjectDrawerItem> serverItems = new ArrayList<ObjectDrawerItem>();
        ArrayList<ObjectDrawerItem> actionItems = new ArrayList<ObjectDrawerItem>();
        ArrayList<ObjectDrawerItem> settingsItems = new ArrayList<ObjectDrawerItem>();


        // Add server category
        serverItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_servers, getResources().getString(R.string.drawer_servers_category), DRAWER_CATEGORY, false, null));

        // Server items
        int currentServerValue = 1;

        try {
            currentServerValue = Integer.parseInt(MainActivity.currentServer);
        } catch (NumberFormatException e) {

        }

        for (int i = 0; i < navigationDrawerServerItems.length; i++) {
            serverItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_subitem, navigationDrawerServerItems[i], DRAWER_ITEM_SERVERS, ((i + 1) == currentServerValue), "changeCurrentServer"));

        }

        // Add actions
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_all, navigationDrawerItemTitles[0], DRAWER_ITEM_ACTIONS, lastState.equals("all"), "refreshAll"));
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_downloading, navigationDrawerItemTitles[1], DRAWER_ITEM_ACTIONS, lastState.equals("downloading"), "refreshDownloading"));
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_completed, navigationDrawerItemTitles[2], DRAWER_ITEM_ACTIONS, lastState.equals("completed"), "refreshCompleted"));
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_paused, navigationDrawerItemTitles[3], DRAWER_ITEM_ACTIONS, lastState.equals("pause"), "refreshPaused"));
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_active, navigationDrawerItemTitles[4], DRAWER_ITEM_ACTIONS, lastState.equals("active"), "refreshActive"));
        actionItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_inactive, navigationDrawerItemTitles[5], DRAWER_ITEM_ACTIONS, lastState.equals("inactive"), "refreshInactive"));


        // Add settings actions
        settingsItems.add(new ObjectDrawerItem(R.drawable.ic_action_options, navigationDrawerItemTitles[6], DRAWER_ITEM_ACTIONS, false, "openOptions"));
        settingsItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_settings, navigationDrawerItemTitles[7], DRAWER_ITEM_ACTIONS, false, "openSettings"));

        if (packageName.equals("com.lgallardo.qbittorrentclient")) {
            settingsItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_pro, navigationDrawerItemTitles[8], DRAWER_ITEM_ACTIONS, false, "getPro"));
            settingsItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_help, navigationDrawerItemTitles[9], DRAWER_ITEM_ACTIONS, false, "openHelp"));
        } else {
            settingsItems.add(new ObjectDrawerItem(R.drawable.ic_drawer_help, navigationDrawerItemTitles[8], DRAWER_ITEM_ACTIONS, false, "openHelp"));
        }


        rAdapter = new DrawerItemRecyclerViewAdapter(getApplicationContext(), this, serverItems, actionItems, settingsItems, null);
        rAdapter.notifyDataSetChanged();

//        drawerList.setAdapter(adapter);
        mRecyclerView.setAdapter(rAdapter);

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        // Set selection according to last state
        setSelectionAndTitle(lastState);

        // Get drawer title
        title = drawerTitle = getTitle();

        // Add the application icon control code inside MainActivity onCreate

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // New ActionBarDrawerToggle for Google Material Desing (v7)
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getSupportActionBar().setTitle(title);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getSupportActionBar().setTitle(drawerTitle);
                // setTitle(R.string.app_shortname);

            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);


        // Get options and save them as shared preferences
        qBittorrentOptions qso = new qBittorrentOptions();
        qso.execute(new String[]{qbQueryString + "/preferences", "getSettings"});

        // If it were awaked from an intent-filter,
        // get intent from the intent filter and Add URL torrent
        addTorrentByIntent(getIntent());

        // Fragments

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first
        // fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            // if (savedInstanceState != null) {
            // return;
            // }

            // This fragment will hold the list of torrents
            if (firstFragment == null) {
                firstFragment = new com.lgallardo.qbittorrentclient.ItemstFragment();
            }

            // This fragment will hold the list of torrents
            helpTabletFragment = new HelpFragment();

            // Set the second fragments container
            firstFragment.setSecondFragmentContainer(R.id.content_frame);

            // This i the second fragment, holding a default message at the
            // beginning
            secondFragment = new AboutFragment();

            // Add the fragment to the 'list_frame' FrameLayout
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (fragmentManager.findFragmentByTag("firstFragment") == null) {
                fragmentTransaction.add(R.id.list_frame, helpTabletFragment, "firstFragment");
            } else {
                fragmentTransaction.replace(R.id.list_frame, helpTabletFragment, "firstFragment");
            }

            if (fragmentManager.findFragmentByTag("secondFragment") == null) {
                fragmentTransaction.add(R.id.content_frame, secondFragment, "secondFragment");
            } else {
                fragmentTransaction.replace(R.id.content_frame, secondFragment, "secondFragment");
            }

            fragmentTransaction.commit();

            // Second fragment will be added in ItemsFRagment's onListItemClick
            // method

        } else {

            // Phones handle just one fragment

            // Create an instance of ItemsFragments
            if (firstFragment == null) {
                firstFragment = new com.lgallardo.qbittorrentclient.ItemstFragment();
            }
            firstFragment.setSecondFragmentContainer(R.id.one_frame);

            // This is the about fragment, holding a default message at the
            // beginning
            secondFragment = new AboutFragment();

            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//
//                // Handle Item list empty due to Fragment stack
//                try {
//                    FragmentManager fm = getFragmentManager();
//
//                    if (fm.getBackStackEntryCount() == 1 && fm.findFragmentById(R.id.one_frame) instanceof com.lgallardo.qbittorrentclient.TorrentDetailsFragment) {
//
//                        refreshCurrent();
//
//                    }
//                }
//                catch (Exception e) {
//                }
//
//                return;
//            }

            // Add the fragment to the 'list_frame' FrameLayout
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (fragmentManager.findFragmentByTag("firstFragment") == null) {
                fragmentTransaction.add(R.id.one_frame, secondFragment, "firstFragment");
            } else {
                fragmentTransaction.replace(R.id.one_frame, secondFragment, "firstFragment");
            }

            // if torrent details was loaded reset back button stack
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }

            fragmentTransaction.commit();
        }

        // Activity is visible
        activityIsVisible = true;

        // First refresh
        refreshCurrent();

        handler = new Handler();
        handler.postDelayed(m_Runnable, refresh_period);

        // Load banner
        loadBanner();

    }

    // Search bar in Material Design
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    // Set selection and title on drawer
    public void setSelectionAndTitle(String state) {
        // Set selection according to last state
        if (state != null) {

            currentState = state;

            if (state.equals("all")) {
//                drawerList.setItemChecked(0, true);
                setTitle(navigationDrawerItemTitles[0]);
            }

            if (state.equals("downloading")) {
//                drawerList.setItemChecked(1, true);
                setTitle(navigationDrawerItemTitles[1]);
            }

            if (state.equals("completed")) {
//                drawerList.setItemChecked(2, true);
                setTitle(navigationDrawerItemTitles[2]);
            }

            if (state.equals("pause")) {
//                drawerList.setItemChecked(3, true);
                setTitle(navigationDrawerItemTitles[3]);
            }

            if (state.equals("active")) {
//                drawerList.setItemChecked(4, true);
                setTitle(navigationDrawerItemTitles[4]);
            }

            if (state.equals("inactive")) {
//                drawerList.setItemChecked(5, true);
                setTitle(navigationDrawerItemTitles[5]);
            }

        } else {
            // Set "All" checked
//            drawerList.setItemChecked(0, true);

            // Set title to All
            setTitle(navigationDrawerItemTitles[0]);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityIsVisible = true;

        // Handle Item list empty due to Fragment stack
        try {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            if (fm.getBackStackEntryCount() == 0 && firstFragment.getSecondFragmentContainer() == R.id.one_frame && fm.findFragmentById(R.id.one_frame) instanceof com.lgallardo.qbittorrentclient.ItemstFragment) {

                com.lgallardo.qbittorrentclient.ItemstFragment fragment = (com.lgallardo.qbittorrentclient.ItemstFragment) fm.findFragmentById(R.id.one_frame);

                if (fragment.getListView().getCount() == 0) {

                    // Create the about fragment
                    aboutFragment = new AboutFragment();

                    fragmentTransaction.replace(R.id.one_frame, aboutFragment, "firstFragment");

                    fragmentTransaction.commit();

                    // Se title
//                    setTitle(navigationDrawerItemTitles[drawerList.getCheckedItemPosition()]);
                    setTitle(navigationDrawerItemTitles[DrawerItemRecyclerViewAdapter.actionPosition]);

                    // Close Contextual Action Bar
                    if (firstFragment != null && firstFragment.mActionMode != null) {
                        firstFragment.mActionMode.finish();
                    }

                    // Refresh current list
                    refreshCurrent();
                }

            }
            if (fm.getBackStackEntryCount() == 0 && firstFragment.getSecondFragmentContainer() == R.id.content_frame && (fm.findFragmentByTag("secondFragment") instanceof AboutFragment)) {

                // Create the about fragment
                aboutFragment = new AboutFragment();

                fragmentTransaction.replace(R.id.content_frame, aboutFragment, "secondFragment");

                fragmentTransaction.commit();

                // Se title
//                setTitle(navigationDrawerItemTitles[drawerList.getCheckedItemPosition()]);
                setTitle(navigationDrawerItemTitles[DrawerItemRecyclerViewAdapter.actionPosition]);

                // Close Contextual Action Bar
                if (firstFragment != null && firstFragment.mActionMode != null) {
                    firstFragment.mActionMode.finish();
                }

                // Refresh current list
                refreshCurrent();

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activityIsVisible = false;
    }

    // Load Banner
    public void loadBanner() {

        if (packageName.equals("com.lgallardo.qbittorrentclient")) {

            // Look up the AdView as a resource and load a request.
            adView = (AdView) this.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Delete
        outState.putInt("itemPosition", itemPosition);
    }

    // Auto-refresh runnable
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {

            if (auto_refresh == true && canrefresh == true && activityIsVisible == true) {

                refreshCurrent();
            }

            MainActivity.this.handler.postDelayed(m_Runnable, refresh_period);
        }

    };// runnable

    public void refreshCurrent() {
//        if (!hostname.equals("")) {

//            switch (drawerList.getCheckedItemPosition()) {
        switch (actionStates.indexOf(currentState)) {
            case 0:
                refresh("all");
                break;
            case 1:
                refresh("downloading");
                break;
            case 2:
                refresh("completed");
                break;
            case 3:
                refresh("pause");
                break;
            case 4:
                refresh("active");
                break;
            case 5:
                refresh("inactive");
                break;
            default:
                refresh();
                break;
        }
//        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.isRefreshing()) {
            return;
        }

//        if (v.getId() == R.id.theList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;


//            Log.d("Debug", "Chosen: " + menuInfo.toString());
//            Log.d("Debug", "Chosen: " + info.position);


            getMenuInflater().inflate(R.menu.menu_file_contextual, menu);
//        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

//        Log.d("Debug", "Item name: " + getResources().getResourceEntryName(item.getItemId()));
//        Log.d("Debug", "Item position: " + TorrentDetailsFragment.fileContentRowPosition);


        switch (item.getItemId()) {

            case R.id.action_file_dont_download:
//                Log.d("Debug", "Don't download");
                setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition,0);
                return true;
            case R.id.action_file_normal_priority:
//                Log.d("Debug", "Normal priority");
                setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 1);
                return true;
            case R.id.action_file_high_priority:
//                Log.d("Debug", "High priority");
                setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 2);
                return true;
            case R.id.action_file_maximum_priority:
//                Log.d("Debug", "Maximum priority");
                setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 7);

                return true;
            default:
//                Log.d("Debug", "default priority?");
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        // If drawer is opened, close it
//        if(drawerLayout.isDrawerOpen(drawerList)){
//            drawerLayout.closeDrawer(drawerList);
//            return;
//        }
        if (drawerLayout.isDrawerOpen(mRecyclerView)) {
            drawerLayout.closeDrawer(mRecyclerView);
            return;
        }

        FragmentManager fm = getFragmentManager();
        com.lgallardo.qbittorrentclient.ItemstFragment fragment = null;

        // Close Contextual Action Bar
        if (com.lgallardo.qbittorrentclient.ItemstFragment.mActionMode != null) {

            com.lgallardo.qbittorrentclient.ItemstFragment.mActionMode.finish();

        } else {


            if (fm.getBackStackEntryCount() == 0) {

                // Set About first load to true
                AboutFragment.isFragmentFirstLoaded = true;

                // Close the app
                this.finish();


            } else {

                // Disable refreshing
                disableRefreshSwipeLayout();

                // Enable toolbar title
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                fm.popBackStack();
            }

        }

        if (findViewById(R.id.one_frame) != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(true);
            MainActivity.drawerToggle.setDrawerIndicatorEnabled(true);
            MainActivity.drawerToggle.setToolbarNavigationClickListener(ItemstFragment.originalListener);

            // Set title
            setSelectionAndTitle(MainActivity.currentState);


            if (headerInfo != null) {
                if (header) {
                    headerInfo.setVisibility(View.VISIBLE);
                } else {
                    headerInfo.setVisibility(View.GONE);
                }
            }

        }


    }


    protected void refreshFromDrawerAction(String state, String title) {
        setTitle(title);
        refreshSwipeLayout();
        refresh(state);
        saveLastState(state);

//        // Mark item as active
//        ObjectDrawerItem drawerItem = DrawerItemRecyclerViewAdapter.items.get(position);
//        drawerItem.setActive(true);
//        DrawerItemRecyclerViewAdapter.items.set(position+1,drawerItem);
////        mRecyclerView.getAdapter().notifyItemChanged(position-1);
//        mRecyclerView.getAdapter().notifyItemChanged(DrawerItemRecyclerViewAdapter.oldActionPosition);


    }

    private void refresh() {

        refresh("all");

    }

    private void refresh(String state) {

        // If Contextual Action Bar is open, don't refresh
        if (firstFragment != null && firstFragment.mActionMode != null) {
            return;
        }

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

        }

        params[1] = state;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && !networkInfo.isFailover()) {

            if (hostname.equals("")) {
                qBittorrentNoSettingsFoundDialog(R.string.info, R.string.about_help1);
            } else {

                if (qb_version.equals("3.2.x") && (cookie == null || cookie.equals(""))) {
                    // Request new cookie and execute task in background
                    new qBittorrentCookieTask().execute(params);

                } else {
                    // Execute the task in background
                    new qBittorrentTask().execute(params);
                }

            }

        } else {

            // Connection Error message
            if (connection400ErrorCounter > 1) {
                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Use the query to search your data somehow
            searchField = intent.getStringExtra(SearchManager.QUERY);

            // Autorefresh
            refreshCurrent();
        }

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            // Add torrent (file, url or magnet)
            addTorrentByIntent(intent);

            // // Activity is visble
            activityIsVisible = true;

            // Autorefresh
            refreshCurrent();

        }

        try {
            if (intent.getStringExtra("from").equals("NotifierService")) {

                saveLastState("completed");
                refresh("completed");

            }

            if (intent.getStringExtra("from").equals("RSSItemActivity")) {

                // Add torrent (file, url or magnet)
                addTorrentByIntent(intent);

                // // Activity is visble
                activityIsVisible = true;

                // Autorefresh
                refreshCurrent();
            }


        } catch (NullPointerException npe) {

        }
    }

    // Get path from content reference, such as content//downloads/0
    // Taken from here http://stackoverflow.com/questions/9194361/how-to-use-android-downloadmanager
    public static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }

    private void addTorrentByIntent(Intent intent) {

        String urlTorrent = intent.getDataString();

        if (urlTorrent != null && urlTorrent.length() != 0) {

            if (urlTorrent.substring(0, 7).equals("content")) {
                urlTorrent = "file://" + getFilePathFromUri(this, Uri.parse(urlTorrent));
            }

            if (urlTorrent.substring(0, 4).equals("file")) {

                // File
                addTorrentFile(Uri.parse(urlTorrent).getPath());

            } else {

                try {
                    addTorrent(Uri.decode(URLEncoder.encode(urlTorrent, "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    Log.e("Debug", "Check URL: " + e.toString());
                }

            }

        }

        try {
            if (intent.getStringExtra("from").equals("NotifierService")) {
                saveLastState("completed");
                refresh("completed");
            }
        } catch (NullPointerException npe) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        }

        return true;
    }

    public void popBackStackPhoneView() {

        // Set default toolbar behaviour
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        actionBar.setDisplayHomeAsUpEnabled(false);
        drawerToggle.setDrawerIndicatorEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        drawerToggle.setToolbarNavigationClickListener(ItemstFragment.originalListener);

        // Set title
        setSelectionAndTitle(MainActivity.currentState);

        // Show herderInfo in phone's view
        if (findViewById(R.id.one_frame) != null) {

            if (headerInfo != null) {
                if (header) {
                    headerInfo.setVisibility(View.VISIBLE);
                } else {
                    headerInfo.setVisibility(View.GONE);
                }
            }

        } else {
            headerInfo.setVisibility(View.VISIBLE);
        }


        getFragmentManager().popBackStack();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Builder builder;
        AlertDialog dialog;

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Enable title (just in case)
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        switch (item.getItemId()) {

            case R.id.action_search:
                onSearchRequested();
                return true;
            case R.id.action_refresh:
                swipeRefresh();
                return true;
            case R.id.action_add:
                // Add URL torrent
                addUrlTorrent();
                return true;
            case R.id.action_rss:
                // Open RSS Activity
                Intent intent = new Intent(getBaseContext(), com.lgallardo.qbittorrentclient.RSSFeedActivity.class);
//                intent.putExtra("packageName", packageName);
//                intent.putExtra("dark_ui", dark_ui);
                startActivity(intent);
                return true;
            case R.id.action_pause:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    pauseTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_resume:
                if (TorrentDetailsFragment.hashToUpdate != null) {
                    startTorrent(TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_delete:

                okay = false;

                if (!isFinishing()) {

                    builder = new Builder(this);

                    // Message
                    builder.setMessage(R.string.dm_deleteTorrent).setTitle(R.string.dt_deleteTorrent);

                    // Cancel
                    builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                            okay = false;
                        }
                    });

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog

                            if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                                deleteTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                                if (findViewById(R.id.one_frame) != null) {
                                    popBackStackPhoneView();
                                }
                            }

                        }
                    });

                    // Create dialog
                    dialog = builder.create();

                    // Show dialog
                    dialog.show();
                }
                return true;
            case R.id.action_delete_drive:

                if (!isFinishing()) {
                    builder = new Builder(this);

                    // Message
                    builder.setMessage(R.string.dm_deleteDriveTorrent).setTitle(R.string.dt_deleteDriveTorrent);

                    // Cancel
                    builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User canceled the dialog
                        }
                    });

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog
                            if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                                deleteDriveTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                                if (findViewById(R.id.one_frame) != null) {
                                    popBackStackPhoneView();
                                }
                            }

                        }
                    });

                    // Create dialog
                    dialog = builder.create();

                    // Show dialog
                    dialog.show();

                }
                return true;
            case R.id.action_increase_prio:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    increasePrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_decrease_prio:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    decreasePrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_max_prio:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    maxPrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_min_prio:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    minPrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_resume_all:
                resumeAllTorrents();
                return true;
            case R.id.action_pause_all:
                pauseAllTorrents();
                return true;
            case R.id.action_upload_rate_limit:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    uploadRateLimitDialog(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;

            case R.id.action_download_rate_limit:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    downloadRateLimitDialog(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_recheck:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    recheckTorrents(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_firts_last_piece_prio:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    toggleFirstLastPiecePrio(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_sequential_download:
                if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                    toggleSequentialDownload(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);

                    if (findViewById(R.id.one_frame) != null) {
                        popBackStackPhoneView();
                    }
                }
                return true;
            case R.id.action_sortby_name:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[0]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_size:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[1]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_eta:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[2]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_priority:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[3]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_progress:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[4]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_ratio:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[5]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_downloadSpeed:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[6]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_uploadSpeed:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[7]);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            case R.id.action_sortby_reverse_order:
                saveReverseOrder(!reverse_order);
                invalidateOptionsMenu();
                swipeRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Change current server
    protected void changeCurrentServer() {

        connection400ErrorCounter = 0;

        // Get values from preferences
        getSettings();

        // redraw menu
        invalidateOptionsMenu();

        // Get options from server and save them as shared preferences
        // locally
//            qBittorrentOptions qso = new qBittorrentOptions();
//            qso.execute(new String[]{qbQueryString + "/preferences", "getSettings"});

        // Save completedHashes
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sharedPrefs.edit();

        // Save hashes
        editor.putString("completed_hashes", "");

        // Commit changes
        editor.apply();

        canrefresh = true;
        refreshSwipeLayout();

//        refreshCurrent();

        // Get new token and cookie
        MainActivity.cookie = null;
        new qBittorrentApiTask().execute(new Intent());

//        Log.d("Debug", "MainActivity - changeCurrentServer called");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SETTINGS_CODE) {

//            Log.d("Debug", "Notification alarm set");

            alarmMgr = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplication(), NotifierService.class);
            alarmIntent = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 5000,
                    notification_period, alarmIntent);
        }

        if (requestCode == OPTION_CODE) {

            String json = "";

            // Get Options
            getOptions();

            /***************************************
             * Save qBittorrent's options remotely *
             ****************************************/

            // Maximum global number of simultaneous connections
            json += "\"max_connec\":" + global_max_num_connections;

            // Maximum number of simultaneous connections per torrent
            json += ",\"max_connec_per_torrent\":" + max_num_conn_per_torrent;

            // Maximum number of upload slots per torrent
            json += ",\"max_uploads_per_torrent\":" + max_num_upslots_per_torrent;

            // Global upload speed limit in KiB/s; -1 means no limit is applied
            json += ",\"up_limit\":" + global_upload;

            // Global download speed limit in KiB/s; -1 means no limit is
            // applied
            json += ",\"dl_limit\":" + global_download;

            // alternative global upload speed limit in KiB/s
            json += ",\"alt_up_limit\":" + alt_upload;

            // alternative global upload speed limit in KiB/s
            json += ",\"alt_dl_limit\":" + alt_download;

            // Is torrent queuing enabled ?
            json += ",\"queueing_enabled\":" + torrent_queueing;

            // Maximum number of active simultaneous downloads
            json += ",\"max_active_downloads\":" + max_act_downloads;

            // Maximum number of active simultaneous uploads
            json += ",\"max_active_uploads\":" + max_act_uploads;

            // Maximum number of active simultaneous downloads and uploads
            json += ",\"max_active_torrents\":" + max_act_torrents;

            // Schedule alternative rate limits
            json += ",\"scheduler_enabled\":" + schedule_alternative_rate_limits;

            // Scheduler starting hour
            json += ",\"schedule_from_hour\":" + alt_from_hour;

            // Scheduler starting min
            json += ",\"schedule_from_min\":" + alt_from_min;

            // Scheduler ending hour
            json += ",\"schedule_to_hour\":" + alt_to_hour;

            // Scheduler ending min
            json += ",\"schedule_to_min\":" + alt_to_min;

            // Scheduler scheduler days
            json += ",\"scheduler_days\":" + scheduler_days;

            // Put everything in an json object
            json = "{" + json + "}";

            // Set preferences using this json object
            setQBittorrentPrefefrences(json);

            // Now it can be refreshed
            canrefresh = true;

        }

        if (requestCode == HELP_CODE) {
            // Now it can be refreshed
            canrefresh = true;
        }


        if (requestCode == SETTINGS_CODE && resultCode == RESULT_OK) {

            // Change current server (from settings or drawer menu)
            changeCurrentServer();

        }

    }

    private void addUrlTorrent() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View addTorrentView = li.inflate(R.layout.add_torrent, null);

        // URL input
        final EditText urlInput = (EditText) addTorrentView.findViewById(R.id.url);

        if (!isFinishing()) {
            // Dialog
            Builder builder = new Builder(MainActivity.this);

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
                    addTorrent(urlInput.getText().toString());
                }
            });

            // Create dialog
            AlertDialog dialog = builder.create();

            // Show dialog
            dialog.show();
        }

    }

    protected void openSettings() {
        canrefresh = false;

        Intent intent = new Intent(getBaseContext(), com.lgallardo.qbittorrentclient.SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_CODE);

    }

    protected void openHelp() {
        canrefresh = false;

        Intent intent = new Intent(getBaseContext(), HelpActivity.class);
        intent.putExtra("current", lastState);
        startActivityForResult(intent, HELP_CODE);

    }

    private void openOptions() {
        canrefresh = false;
        // Retrieve preferences for options
        Intent intent = new Intent(getBaseContext(), OptionsActivity.class);
        startActivityForResult(intent, OPTION_CODE);

    }

    // This get qBitorrent options to save them in shared preferences variables and then open the Option activity
    protected void getAndOpenOptions() {

        // Options - Execute the task in background
        Toast.makeText(getApplicationContext(), R.string.getQBittorrentPrefefrences, Toast.LENGTH_SHORT).show();
        qBittorrentOptions qso = new qBittorrentOptions();
        qso.execute(new String[]{qbQueryString + "/preferences", "setOptions"});

    }




    protected void getPRO() {
        Intent intent = new Intent(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lgallardo.qbittorrentclientpro")));
        startActivityForResult(intent, GETPRO_CODE);
    }

    public void startTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"start", hash});
    }

    public void startSelectedTorrents(String hashes) {
        // Execute the task in background

        String[] hashesArray = hashes.split("\\|");

        for (int i = 0; hashesArray.length > i; i++) {
            qBittorrentCommand qtc = new qBittorrentCommand();
            qtc.execute(new String[]{"startSelected", hashesArray[i]});
        }

        Toast.makeText(getApplicationContext(), R.string.torrentsSelectedStarted, Toast.LENGTH_SHORT).show();

        // Delay of 3 seconds
        refreshAfterCommand(3);
    }

    public void pauseTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"pause", hash});
    }

    public void pauseSelectedTorrents(String hashes) {
        // Execute the task in background

        String[] hashesArray = hashes.split("\\|");

        for (int i = 0; hashesArray.length > i; i++) {
            qBittorrentCommand qtc = new qBittorrentCommand();
            qtc.execute(new String[]{"pauseSelected", hashesArray[i]});
        }

        Toast.makeText(getApplicationContext(), R.string.torrentsSelectedPaused, Toast.LENGTH_SHORT).show();

        // Delay of 1 second
        refreshAfterCommand(1);

    }

    public void deleteTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"delete", hash});
    }

    public void deleteSelectedTorrents(String hashes) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"deleteSelected", hashes});

        Toast.makeText(getApplicationContext(), R.string.torrentsSelectedDeleted, Toast.LENGTH_SHORT).show();

        // Delay of 1 second
        refreshAfterCommand(1);
    }

    public void deleteDriveTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"deleteDrive", hash});
    }

    public void deleteDriveSelectedTorrents(String hashes) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"deleteDriveSelected", hashes});

        Toast.makeText(getApplicationContext(), R.string.torrentsSelectedDeletedDrive, Toast.LENGTH_SHORT).show();

        // Delay of 1 second
        refreshAfterCommand(1);
    }

    public void addTorrent(String url) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"addTorrent", url});
    }

    public void addTorrentFile(String url) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"addTorrentFile", url});
    }

    public void pauseAllTorrents() {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();

        if (qb_version.equals("3.2.x")) {
            qtc.execute(new String[]{"pauseAll", null});
        } else {
            qtc.execute(new String[]{"pauseall", null});
        }
    }

    public void resumeAllTorrents() {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();

        if (qb_version.equals("3.2.x")) {
            qtc.execute(new String[]{"resumeAll", null});
        } else {
            qtc.execute(new String[]{"resumeall", null});
        }
    }

    public void increasePrioTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"increasePrio", hash});

    }

    public void decreasePrioTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"decreasePrio", hash});

    }

    public void maxPrioTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"maxPrio", hash});

    }

    public void minPrioTorrent(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"minPrio", hash});

    }

    public void setFilePrio(String hash, int id, int priority) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();

        hash = hash + "&" + id + "&" + priority;

        qtc.execute(new String[]{"setFilePrio", hash });

    }

    public void recheckTorrents(String hashes) {
        // Execute the task in background

        String[] hashesArray = hashes.split("\\|");

        for (int i = 0; hashesArray.length > i; i++) {
            qBittorrentCommand qtc = new qBittorrentCommand();
            qtc.execute(new String[]{"recheckSelected", hashesArray[i]});
        }

        Toast.makeText(getApplicationContext(), R.string.torrentsRecheck, Toast.LENGTH_SHORT).show();

        // Delay of 3 seconds
        refreshAfterCommand(3);
    }

    public void toggleFirstLastPiecePrio(String hashes) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"toggleFirstLastPiecePrio", hashes});

    }

    public void toggleSequentialDownload(String hashes) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"toggleSequentialDownload", hashes});

    }

    public void setQBittorrentPrefefrences(String hash) {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"setQBittorrentPrefefrences", hash});

    }

    public void uploadRateLimitDialog(final String hash) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View view = li.inflate(R.layout.upload_rate_limit, null);

        // URL input
        final EditText uploadRateLimit = (EditText) view.findViewById(R.id.upload_rate_limit);

        if (!isFinishing()) {
            // Dialog
            Builder builder = new Builder(MainActivity.this);

            // Set add_torrent.xml to AlertDialog builder
            builder.setView(view);

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
                    setUploadRateLimit(hash, uploadRateLimit.getText().toString());
                }
            });

            // Create dialog
            AlertDialog dialog = builder.create();

            // Show dialog
            dialog.show();
        }
    }

    public void downloadRateLimitDialog(final String hash) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View view = li.inflate(R.layout.download_rate_limit, null);

        // URL input
        final EditText downloadRateLimit = (EditText) view.findViewById(R.id.download_rate_limit);

        if (!isFinishing()) {
            // Dialog
            Builder builder = new Builder(MainActivity.this);

            // Set add_torrent.xml to AlertDialog builder
            builder.setView(view);

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
                    setDownloadRateLimit(hash, downloadRateLimit.getText().toString());
                }
            });

            // Create dialog
            AlertDialog dialog = builder.create();

            // Show dialog
            dialog.show();
        }
    }

    public void setUploadRateLimit(String hash, String uploadRateLimit) {
        int limit;

        if (uploadRateLimit != null && !uploadRateLimit.equals("")) {

            if (global_upload != null) {

                if (Integer.parseInt(global_upload) > 0) {

                    limit = (Integer.parseInt(uploadRateLimit) > Integer.parseInt(global_upload) && Integer.parseInt(global_upload) != 0) ? Integer
                            .parseInt(global_upload) : Integer.parseInt(uploadRateLimit);

                } else {
                    limit = Integer.parseInt(uploadRateLimit);
                }

                String[] hashesArray = hash.split("\\|");

                for (int i = 0; hashesArray.length > i; i++) {
                    qBittorrentCommand qtc = new qBittorrentCommand();
                    qtc.execute(new String[]{"setUploadRateLimit", hashesArray[i] + "&" + limit * 1024});
                }

                Toast.makeText(getApplicationContext(), R.string.setUploadRateLimit, Toast.LENGTH_SHORT).show();

                // Delay of 1 second
                refreshAfterCommand(1);

            } else {
                genericOkDialog(R.string.error, R.string.global_value_error);

            }
        }

    }

    public void setDownloadRateLimit(String hash, String downloadRateLimit) {

        int limit;

        if (downloadRateLimit != null && !downloadRateLimit.equals("")) {

            if (global_download != null) {

                if (Integer.parseInt(global_download) > 0) {
                    limit = (Integer.parseInt(downloadRateLimit) > Integer.parseInt(global_download)) ? Integer.parseInt(global_download) : Integer
                            .parseInt(downloadRateLimit);
                } else {
                    limit = Integer.parseInt(downloadRateLimit);
                }

                String[] hashesArray = hash.split("\\|");

                for (int i = 0; hashesArray.length > i; i++) {
                    qBittorrentCommand qtc = new qBittorrentCommand();
                    qtc.execute(new String[]{"setDownloadRateLimit", hashesArray[i] + "&" + limit * 1024});
                }

                Toast.makeText(getApplicationContext(), R.string.setDownloadRateLimit, Toast.LENGTH_SHORT).show();

                // Delay of 1 second
                refreshAfterCommand(1);

            } else {
                genericOkDialog(R.string.error, R.string.global_value_error);
            }
        }
    }

    public void refreshAfterCommand(int delay) {

//        switch (drawerList.getCheckedItemPosition()) {
        switch (actionStates.indexOf(currentState)) {
            case 0:
                refreshWithDelay("all", delay);
                break;
            case 1:
                refreshWithDelay("downloading", delay);
                break;
            case 2:
                refreshWithDelay("completed", delay);
                break;
            case 3:
                refreshWithDelay("pause", delay);
                break;
            case 4:
                refreshWithDelay("active", delay);
                break;
            case 5:
                refreshWithDelay("inactive", delay);
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                refreshWithDelay("all", delay);
                break;
        }

    }

    public void genericOkDialog(int title, int message) {

        if (!isFinishing()) {

            Builder builder = new Builder(this);

            // Message
            builder.setMessage(message).setTitle(title);

            // Ok
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    // User accepted the dialog

                }
            });

            // Create dialog
            AlertDialog dialog = builder.create();

            // Show dialog
            dialog.show();
        }

    }


    public void qBittorrentNoSettingsFoundDialog(int title, int message) {

        if (!isFinishing()) {

            Builder builder = new Builder(this);

            // Message
            builder.setMessage(message).setTitle(title);

            // Ok
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    // User accepted the dialog
                }
            });

            // Settings
            builder.setPositiveButton(R.string.navigation_drawer_settins, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    // User accepted the dialog
                    openSettings();
                }
            });


            // Create dialog
            AlertDialog dialog = builder.create();

            // Show dialog
            dialog.show();
        }

    }

    // Delay method
    public void refreshWithDelay(final String state, int seconds) {

        seconds *= 1000;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                refresh(state);
            }
        }, seconds);
    }

    // Get settings
    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from preferences
        currentServer = sharedPrefs.getString("currentServer", "1");
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

        // Get refresh info
        auto_refresh = sharedPrefs.getBoolean("auto_refresh", true);

        try {
            refresh_period = Integer.parseInt(sharedPrefs.getString("refresh_period", "120000"));
        } catch (NumberFormatException e) {
            refresh_period = 120000;
        }

        // Get connection and data timeouts
        try {
            connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "10"));

            // New default value to make it work with qBittorrent 3.2.x
            if (connection_timeout < 10) {
                connection_timeout = 10;
            }
        } catch (NumberFormatException e) {
            connection_timeout = 10;
        }

        try {
            data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "20"));

            // New default value to make it work with qBittorrent 3.2.x
            if (data_timeout < 20) {
                data_timeout = 20;
            }

        } catch (NumberFormatException e) {
            data_timeout = 20;
        }

        sortby = sharedPrefs.getString("sortby", "NULL");
        reverse_order = sharedPrefs.getBoolean("reverse_order", false);

        dark_ui = sharedPrefs.getBoolean("dark_ui", false);

        qb_version = sharedPrefs.getString("qb_version", "3.2.x");

        MainActivity.cookie = sharedPrefs.getString("qbCookie", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", "all");

        // Notification check
        try {
            notification_period = Long.parseLong(sharedPrefs.getString("notification_period", "120000L"));
        } catch (NumberFormatException e) {
            notification_period = 120000L;
        }

        header = sharedPrefs.getBoolean("header", true);

        // Get package info
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Get package name
        packageName = pInfo.packageName;

    }

    // Get Options
    protected void getOptions() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from options
        global_max_num_connections = sharedPrefs.getString("global_max_num_connections", "0");

        max_num_conn_per_torrent = sharedPrefs.getString("max_num_conn_per_torrent", "0");
        max_num_upslots_per_torrent = sharedPrefs.getString("max_num_upslots_per_torrent", "0");

        global_upload = sharedPrefs.getString("global_upload", "0");
        global_download = sharedPrefs.getString("global_download", "0");

        alt_upload = sharedPrefs.getString("alt_upload", "0");
        alt_download = sharedPrefs.getString("alt_download", "0");

        // This will used for checking if the torrent queuing option are used
        torrent_queueing = sharedPrefs.getBoolean("torrent_queueing", false);

        max_act_downloads = sharedPrefs.getString("max_act_downloads", "0");
        max_act_uploads = sharedPrefs.getString("max_act_uploads", "0");
        max_act_torrents = sharedPrefs.getString("max_act_torrents", "0");

        schedule_alternative_rate_limits = sharedPrefs.getBoolean("schedule_alternative_rate_limits", false);

        alt_from_hour = "" + TimePreference.getHour(sharedPrefs.getString("alt_from", "8:00"));
        alt_from_min = "" + TimePreference.getMinute(sharedPrefs.getString("alt_from", "8:00"));

        alt_to_hour = "" + TimePreference.getHour(sharedPrefs.getString("alt_to", "20:00"));
        alt_to_min = "" + TimePreference.getMinute(sharedPrefs.getString("alt_to", "20:00"));

        scheduler_days = sharedPrefs.getString("scheduler_days", "NULL");


    }

    protected void notifyCompleted(HashMap completedTorrents) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("qBittorrent")
                .setContentText("Torrent(s) completed")
                .setSmallIcon(R.drawable.ic_stat_completed)
                .setNumber(completedTorrents.size())
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification;

        notification = builder.getNotification();

        notificationManager.notify(0, notification);


    }

    private void saveLastState(String state) {

        currentState = state;

        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Editor editor = sharedPrefs.edit();

        // Save key-values
        editor.putString("lastState", state);

        // Commit changes
        editor.apply();

    }

    private void saveSortBy(String sortBy) {
        MainActivity.sortby = sortBy;
        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Editor editor = sharedPrefs.edit();

        // Save key-values
        editor.putString("sortby", sortBy);

        // Commit changes
        editor.apply();

    }

    private void saveReverseOrder(boolean reverse_order) {
        MainActivity.reverse_order = reverse_order;
        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Editor editor = sharedPrefs.edit();

        // Save key-values
        editor.putBoolean("reverse_order", reverse_order);

        // Commit changes
        editor.apply();

    }

    private void selectItem(int position) {


        if (findViewById(R.id.one_frame) != null) {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager.findFragmentByTag("firstFragment") instanceof com.lgallardo.qbittorrentclient.TorrentDetailsFragment) {
                // Reset back button stack
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }
            }
        }
    }

    @Override
    public void swipeRefresh() {


        if (hostname.equals("")) {
            qBittorrentNoSettingsFoundDialog(R.string.info, R.string.about_help1);
            disableRefreshSwipeLayout();

        } else {

            // Set the refresh layout (refresh icon, etc)
            refreshSwipeLayout();

            // Actually refresh data
            refreshCurrent();

            // Load banner
            loadBanner();

        }

    }

    public static void disableRefreshSwipeLayout() {

        if (com.lgallardo.qbittorrentclient.AboutFragment.mSwipeRefreshLayout != null) {
            com.lgallardo.qbittorrentclient.AboutFragment.mSwipeRefreshLayout.setRefreshing(false);
            com.lgallardo.qbittorrentclient.AboutFragment.mSwipeRefreshLayout.clearAnimation();
            com.lgallardo.qbittorrentclient.AboutFragment.mSwipeRefreshLayout.setEnabled(true);
        }

        if (com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout != null) {
            com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.setRefreshing(false);
            com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.clearAnimation();
            com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.setEnabled(true);
        }

        if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout != null) {
            com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.setRefreshing(false);
            com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.clearAnimation();
            com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.setEnabled(true);
        }

        listViewRefreshing = false;
    }


    public void refreshSwipeLayout() {

        if (!hostname.equals("")) {

            listViewRefreshing = true;

            if (AboutFragment.mSwipeRefreshLayout != null) {
                AboutFragment.mSwipeRefreshLayout.setRefreshing(true);
            }

            if (com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout != null) {
                com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.setRefreshing(true);
                com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.setEnabled(false);
            }

            if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout != null) {
                com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.setRefreshing(true);
            }
        }

    }

    // Here is where the action happens
    private class qBittorrentCookieTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            com.lgallardo.qbittorrentclient.JSONParser jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String newCookie = "";
            String api = "";

            try {
                newCookie = jParser.getNewCookie();

            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
            }

            if (newCookie == null) {
                newCookie = "";
            }

            if (api == null) {
                api = "";

            }

            return new String[]{newCookie, api};

        }

        @Override
        protected void onPostExecute(String[] result) {


            MainActivity.cookie = result[0];


            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qbCookie", result[0]);


            // Commit changes
            editor.apply();

            // Execute the task in background
            new qBittorrentTask().execute(params);

        }
    }

    // Here is where the action happens
    private class qBittorrentApiTask extends AsyncTask<Intent, Integer, String[]> {

        @Override
        protected String[] doInBackground(Intent... intents) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            com.lgallardo.qbittorrentclient.JSONParser jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String apiVersion = "";

            httpStatusCode = 0;

            // Try to get the API number
            try {

                apiVersion = jParser.getApi();

            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
            }

            // If < 3.2.x, get qBittorrent version
            if (httpStatusCode > 200 || apiVersion == null) {

                try {

                    apiVersion = jParser.getVersion();

                } catch (JSONParserStatusCodeException e) {
                    httpStatusCode = e.getCode();
                }

            }


            return new String[]{apiVersion, intents[0].getStringExtra("currentState")};

        }

        @Override
        protected void onPostExecute(String[] result) {

            String apiVersion = result[0];

            int api = 0;

            try {

                api = Integer.parseInt(apiVersion);

            } catch (Exception e) {
                api = 0;
            }

            if (apiVersion != null && (api > 1 || apiVersion.contains("3.2") || apiVersion.contains("3.3"))) {

                qb_version = "3.2.x";

                // Get new cookie
                cookie = null;

            } else if (apiVersion.contains("3.1")) {

                qb_version = "3.1.x";

            } else {

                qb_version = "2.x";

            }

            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qb_version", qb_version);

            // Commit changes
            editor.apply();


            // Refresh
            String stateBefore = result[1];

            if (stateBefore != null) {

                // Set selection according to last state
                setSelectionAndTitle(stateBefore);

                // Set the refresh layout (refresh icon, etc)
                refreshSwipeLayout();

                // Refresh state
                refresh(stateBefore);

                // load banner
                loadBanner();

            } else {

                swipeRefresh();

            }


        }
    }


    // Here is where the action happens
    private class qBittorrentCommand extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            com.lgallardo.qbittorrentclient.JSONParser jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            jParser.setCookie(cookie);

            try {

                httpStatusCode = 0;

                jParser.postCommand(params[0], params[1]);

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();

            }

            return params[0];

        }

        @Override
        protected void onPostExecute(String result) {

            // Handle HTTP status code

            if (httpStatusCode == 1) {
                Toast.makeText(getApplicationContext(), R.string.error1, Toast.LENGTH_SHORT).show();
                httpStatusCode = 0;
                return;
            }

            if (httpStatusCode == 401) {
                Toast.makeText(getApplicationContext(), R.string.error401, Toast.LENGTH_LONG).show();
                httpStatusCode = 0;
                return;
            }

            if (httpStatusCode == 403 || httpStatusCode == 404) {

                if (qb_version.equals("3.2.x")) {
                    cookie = null;
                }

                Toast.makeText(getApplicationContext(), R.string.error403, Toast.LENGTH_SHORT).show();

                httpStatusCode = 0;
                return;
            }

            // This delay is needed for resume action. Other actions have a
            // fewer delay (1 second).
            int delay = 1;

            int messageId = R.string.connection_error;

            if (result == null) {
                messageId = R.string.connection_error;
            }

            if ("start".equals(result)) {
                messageId = R.string.torrentStarted;

                // Needed to refresh after a resume and see the change
                delay = 3;
            }

            if ("pause".equals(result)) {
                messageId = R.string.torrentPaused;
            }

            if ("delete".equals(result)) {
                messageId = R.string.torrentDeleted;
            }

            if ("deleteDrive".equals(result)) {
                messageId = R.string.torrentDeletedDrive;
            }

            if ("addTorrent".equals(result)) {
                messageId = R.string.torrentAdded;
            }

            if ("addTorrentFile".equals(result)) {
                messageId = R.string.torrentFileAdded;
            }

            if ("pauseAll".equals(result)) {
                messageId = R.string.AllTorrentsPaused;
            }

            if ("resumeAll".equals(result)) {
                messageId = R.string.AllTorrentsResumed;

                // Needed to refresh after a "resume all" and see the changes
                delay = 3;
            }

            if ("increasePrio".equals(result)) {
                messageId = R.string.increasePrioTorrent;
            }

            if ("decreasePrio".equals(result)) {
                messageId = R.string.decreasePrioTorrent;
            }

            if ("maxPrio".equals(result)) {
                messageId = R.string.priorityUpdated;
            }

            if ("minPrio".equals(result)) {
                messageId = R.string.priorityUpdated;
            }

            if ("setFilePrio".equals(result)) {
                messageId = R.string.priorityUpdated;
            }


            if ("setQBittorrentPrefefrences".equals(result)) {
                messageId = R.string.setQBittorrentPrefefrences;
            }

            if ("setUploadRateLimit".equals(result)) {
                messageId = R.string.setUploadRateLimit;
                if (findViewById(R.id.one_frame) != null) {
                    popBackStackPhoneView();
                }
            }

            if ("setDownloadRateLimit".equals(result)) {
                messageId = R.string.setDownloadRateLimit;
                if (findViewById(R.id.one_frame) != null) {
                    popBackStackPhoneView();
                }
            }

            if ("recheckSelected".equals(result)) {
                messageId = R.string.torrentsRecheck;
            }

            if ("toggleFirstLastPiecePrio".equals(result)) {
                messageId = R.string.torrentstogglefisrtLastPiecePrio;
            }

            if ("toggleSequentialDownload".equals(result)) {
                messageId = R.string.torrentstoggleSequentialDownload;
            }

            if (!("startSelected".equals(result)) && !("pauseSelected".equals(result)) && !("deleteSelected".equals(result)) && !("deleteDriveSelected".equals(result)) && !("setUploadRateLimit".equals(result)) && !("setDownloadRateLimit".equals(result)) && !("recheckSelected".equals(result))) {
                Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();

                // Refresh
                refreshAfterCommand(delay);
            }
        }
    }

    // Here is where the action happens
    private class qBittorrentTask extends AsyncTask<String, Integer, Torrent[]> {

        @Override
        protected Torrent[] doInBackground(String... params) {

            String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, eta, uploadSpeed, downloadSpeed;
            boolean sequentialDownload = false;
            boolean firstLastPiecePrio = false;

            Torrent[] torrents = null;

            // Get settings
            getSettings();

            try {

                // Creating new JSON Parser
                jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

                jParser.setCookie(MainActivity.cookie);

                JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);

                if (jArray != null) {

                    torrents = new Torrent[jArray.length()];

                    MainActivity.names = new String[jArray.length()];

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

                        if (qb_version.equals("3.2.x")) {

                            size = Common.calculateSize(size);
                            eta = Common.secondsToEta(eta);
                            downloadSpeed = Common.calculateSize(downloadSpeed) + "/s";
                            uploadSpeed = Common.calculateSize(uploadSpeed) + "/s";

                            try {
                                sequentialDownload = json.getBoolean(TAG_SEQDL);
                            } catch (Exception e) {
                                firstLastPiecePrio = false;
                            }


                            try {
                                firstLastPiecePrio = json.getBoolean(TAG_FLPIECEPRIO);
                            } catch (Exception e) {
                                firstLastPiecePrio = false;
                            }
                        }

                        torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, leechs, seeds, priority, eta, downloadSpeed, uploadSpeed, sequentialDownload, firstLastPiecePrio);

                        MainActivity.names[i] = name;

                        // Get torrent generic properties

                        try {
                            // Calculate total downloaded
                            Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                            String sizeUnit = size.substring(size.indexOf(" "), size.length());

                            torrents[i].setDownloaded(String.format("%.1f", sizeScalar * json.getDouble(TAG_PROGRESS)).replace(",", ".") + sizeUnit);

                        } catch (Exception e) {
                            torrents[i].setDownloaded(size);
                        }

                        if (packageName.equals("com.lgallardo.qbittorrentclient")) {
                            // Info free
                            torrents[i].setInfo(torrents[i].getDownloaded() + " / " + torrents[i].getSize() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                    + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                    + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + progress + " " + Character.toString('\u2022') + " "
                                    + torrents[i].getEta());

                        } else {
                            // Info pro
                            torrents[i].setInfo(torrents[i].getDownloaded() + " / " + torrents[i].getSize() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                    + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                    + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + torrents[i].getEta());
                        }
                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();

                if (httpStatusCode == 400) {
                    cookie = null;
                }

                torrents = null;
                Log.e("JSONParserStatusCode", e.toString());

            } catch (Exception e) {
                torrents = null;
                Log.e("MAIN:", e.toString());
            }

            return torrents;

        }

        @Override
        protected void onPostExecute(Torrent[] result) {

            if (result == null) {

                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();

                // Handle HTTP status code

                if (httpStatusCode == 1) {
                    Toast.makeText(getApplicationContext(), R.string.error1, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;
                    connection400ErrorCounter = 2;
                }

                if (httpStatusCode == 401) {
                    Toast.makeText(getApplicationContext(), R.string.error401, Toast.LENGTH_LONG).show();
                    httpStatusCode = 0;
                    connection400ErrorCounter = 2;
                }
                if (httpStatusCode == 400) {
                    connection400ErrorCounter = connection400ErrorCounter + 1;
                    httpStatusCode = 0;
                    return;
                }

                if (httpStatusCode == 403 || httpStatusCode == 404) {

//                    Log.d("Debug","MainActivity - refresh - qb_version:" +qb_version );

                    if (qb_version.equals("3.2.x")) {
                        // Get new Cookie
                        cookie = null;
                    }

                    Toast.makeText(getApplicationContext(), R.string.error403, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;
                    connection400ErrorCounter = 2;

                }


            } else {

                connection400ErrorCounter = 0;

                ArrayList<Torrent> torrentsFiltered = new ArrayList<Torrent>();

                for (int i = 0; i < result.length; i++) {

                    if (params[1].equals("all") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        torrentsFiltered.add(result[i]);
                    }

                    if (params[1].equals("downloading") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        if ("downloading".equals(result[i].getState()) || "stalledDL".equals(result[i].getState()) || "pausedDL".equals(result[i].getState())
                                || "queuedDL".equals(result[i].getState()) || "checkingDL".equals(result[i].getState())) {
                            torrentsFiltered.add(result[i]);
                        }
                    }

                    if (params[1].equals("completed") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        if ("uploading".equals(result[i].getState()) || "stalledUP".equals(result[i].getState()) || "pausedUP".equals(result[i].getState())
                                || "queuedUP".equals(result[i].getState()) || "checkingUP".equals(result[i].getState())) {
                            torrentsFiltered.add(result[i]);
                        }
                    }

                    if (params[1].equals("pause") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        if ("pausedDL".equals(result[i].getState()) || "pausedUP".equals(result[i].getState())) {
                            torrentsFiltered.add(result[i]);
                        }
                    }

                    if (params[1].equals("active") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        if ("uploading".equals(result[i].getState()) || "downloading".equals(result[i].getState())) {
                            torrentsFiltered.add(result[i]);
                        }
                    }

                    if (params[1].equals("inactive") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
                        if ("pausedUP".equals(result[i].getState()) || "pausedDL".equals(result[i].getState()) || "queueUP".equals(result[i].getState())
                                || "queueDL".equals(result[i].getState()) || "stalledUP".equals(result[i].getState())
                                || "stalledDL".equals(result[i].getState())) {
                            torrentsFiltered.add(result[i]);
                        }
                    }

                }


                // Sort by filename
                if (sortby.equals("Name")) {
                    Collections.sort(torrentsFiltered, new TorrentNameComparator(reverse_order));
                }
                // Sort by size
                if (sortby.equals("Size")) {
                    Collections.sort(torrentsFiltered, new TorrentSizeComparator(reverse_order));
                }
                // Sort by priority
                if (sortby.equals("Priority")) {
                    Collections.sort(torrentsFiltered, new TorrentPriorityComparator(reverse_order));
                }
                // Sort by progress
                if (sortby.equals("Progress")) {
                    Collections.sort(torrentsFiltered, new TorrentProgressComparator(reverse_order));
                }
                // Sort by Eta
                if (sortby.equals("ETA")) {
                    Collections.sort(torrentsFiltered, new TorrentEtaComparator(reverse_order));
                }

                // Sort by download speed
                if (sortby.equals("Ratio")) {
                    Collections.sort(torrentsFiltered, new TorrentRatioComparator(reverse_order));
                }

                // Sort by upload speed
                if (sortby.equals("DownloadSpeed")) {
                    Collections.sort(torrentsFiltered, new TorrentDownloadSpeedComparator(reverse_order));
                }

                // Sort by Ratio
                if (sortby.equals("UploadSpeed")) {
                    Collections.sort(torrentsFiltered, new TorrentUploadSpeedComparator(reverse_order));
                }

                // Get names (delete in background method)
                MainActivity.names = new String[torrentsFiltered.size()];
                MainActivity.lines = new Torrent[torrentsFiltered.size()];

                uploadSpeedCount = 0;
                downloadSpeedCount = 0;

                uploadCount = 0;
                downloadCount = 0;

                try {

                    Torrent torrentToUpdate = null;

                    for (int i = 0; i < torrentsFiltered.size(); i++) {

                        Torrent torrent = torrentsFiltered.get(i);

                        MainActivity.names[i] = torrent.getFile();
                        MainActivity.lines[i] = torrent;

                        if (torrent.getHash().equals(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate)) {
                            torrentToUpdate = torrent;
                        }

                        uploadSpeedCount += (int) Common.humanSizeToBytes(torrent.getUploadSpeed());
                        downloadSpeedCount += (int) Common.humanSizeToBytes(torrent.getDownloadSpeed());

                        if ("uploading".equals(torrent.getState())) {
                            uploadCount = uploadCount + 1;
                        }

                        if ("downloading".equals(torrent.getState())) {
                            downloadCount = downloadCount + 1;
                        }

                    }

                    // Update torrent list
                    try {
                        myadapter.setNames(names);
                        myadapter.setData(lines);
                        myadapter.notifyDataSetChanged();
                    } catch (NullPointerException ne)

                    {
                        myadapter = new TorrentListAdapter(MainActivity.this, names, lines);
                        firstFragment.setListAdapter(myadapter);

                        myadapter.setNames(names);
                        myadapter.setData(lines);
                        myadapter.notifyDataSetChanged();

                    } catch (IllegalStateException le) {

                        Log.e("Debug", "IllegalStateException: " + le.toString());
                    }

                    // Create the about fragment
                    aboutFragment = new AboutFragment();

                    // Add the fragment to the 'list_frame' FrameLayout
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Got some results
                    if (torrentsFiltered.size() > 0) {

                        // Set headerInfo
                        TextView uploadSpeedTextView = (TextView) findViewById(R.id.uploadSpeed);
                        TextView downloadSpeedTextView = (TextView) findViewById(R.id.downloadSpeed);

                        headerInfo = (LinearLayout) findViewById(R.id.header);

                        if (header) {
                            headerInfo.setVisibility(View.VISIBLE);
                        } else {
                            headerInfo.setVisibility(View.GONE);
                        }

                        uploadSpeedTextView.setText(Character.toString('\u2191') + " " + Common.calculateSize("" + uploadSpeedCount) + "/s " + "(" + uploadCount + ")");
                        downloadSpeedTextView.setText(Character.toString('\u2193') + " " + Common.calculateSize("" + downloadSpeedCount) + "/s " + "(" + downloadCount + ")");


                        //Set first and second fragments
                        if (findViewById(R.id.fragment_container) != null) {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.content_frame);

                            // Set first fragment
                            if (fragmentManager.findFragmentByTag("firstFragment") instanceof HelpFragment) {
                                fragmentTransaction.replace(R.id.list_frame, firstFragment, "firstFragment");
                            }

                            // Set second fragment
                            if (!(fragmentManager.findFragmentByTag("secondFragment") instanceof AboutFragment)) {

                                com.lgallardo.qbittorrentclient.TorrentDetailsFragment detailsFragment = (com.lgallardo.qbittorrentclient.TorrentDetailsFragment) fragmentManager.findFragmentByTag("secondFragment");

                                if (torrentToUpdate != null) {
                                    // Update torrent details
                                    detailsFragment.updateDetails(torrentToUpdate);
                                } else {

                                    // Torrent no longer found

                                    // Set second fragment with About fragment
                                    fragmentTransaction.replace(R.id.content_frame, aboutFragment, "secondFragment");

                                    // Reset back button stack
                                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                        fragmentManager.popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }
                                }


                            } else {
                                // Reset back button stack
                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                    fragmentManager.popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }

                            }

                        } else {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.one_frame);

                            // Set first fragment
                            if (fragmentManager.findFragmentByTag("firstFragment") instanceof AboutFragment) {
                                fragmentTransaction.replace(R.id.one_frame, firstFragment, "firstFragment");
                            }

                            if (fragmentManager.findFragmentByTag("firstFragment") instanceof com.lgallardo.qbittorrentclient.TorrentDetailsFragment) {

                                com.lgallardo.qbittorrentclient.TorrentDetailsFragment detailsFragment = (com.lgallardo.qbittorrentclient.TorrentDetailsFragment) fragmentManager.findFragmentByTag("firstFragment");

                                if (torrentToUpdate != null) {
                                    // Update torrent
                                    detailsFragment.updateDetails(torrentToUpdate);
                                } else {

                                    // Torrent no longer found

                                    // Reset back button stack
                                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                        fragmentManager.popBackStack();
                                    }

                                }
                            }
                        }
                    } else {

                        // No results

                        myadapter.setNames(null);
                        myadapter.setData(null);

                        myadapter.notifyDataSetChanged();

                        // Hide headerInfo
                        TextView uploadSpeedTextView = (TextView) findViewById(R.id.uploadSpeed);
                        TextView downloadSpeedTextView = (TextView) findViewById(R.id.downloadSpeed);

                        uploadSpeedTextView.setText("");
                        downloadSpeedTextView.setText("");


                        //Set first and second fragments
                        if (findViewById(R.id.fragment_container) != null) {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.content_frame);

                            // Set first fragment
                            if (fragmentManager.findFragmentByTag("firstFragment") instanceof HelpFragment) {
                                fragmentTransaction.replace(R.id.list_frame, firstFragment, "firstFragment");
                            }

                            // Set second fragment
                            if (!(fragmentManager.findFragmentByTag("secondFragment") instanceof AboutFragment)) {
                                fragmentTransaction.replace(R.id.content_frame, aboutFragment, "secondFragment");

                                // Reset back button stack
                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                    fragmentManager.popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }

                            }
                        } else {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.one_frame);

                            // Set first fragment
                            if (fragmentManager.findFragmentByTag("firstFragment") instanceof AboutFragment) {
                                fragmentTransaction.replace(R.id.one_frame, firstFragment, "firstFragment");
                            }

                            // Reset back button stack
                            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack();
                            }
                        }
                    }

                    // Commit
                    fragmentTransaction.commit();

                } catch (Exception e) {
                    Log.e("ADAPTER", e.toString());
                }

                // Clear serch field
                searchField = "";

            }

            // Disable refreshSwipeLayout
            disableRefreshSwipeLayout();
        }
    }

    // Here is where the action happens
    private class qBittorrentOptions extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get settings
            getSettings();

            // Creating new JSON Parser
            com.lgallardo.qbittorrentclient.JSONParser jParser = new com.lgallardo.qbittorrentclient.JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            jParser.setCookie(cookie);

            // Get the Json object
            JSONObject json = null;
            try {
                json = jParser.getJSONFromUrl(params[0]);

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();
                Log.e("JSONParserStatusCode", e.toString());
            }

            if (json != null) {

                try {

                    global_max_num_connections = json.getString(TAG_GLOBAL_MAX_NUM_CONNECTIONS);
                    max_num_conn_per_torrent = json.getString(TAG_MAX_NUM_CONN_PER_TORRENT);
                    max_num_upslots_per_torrent = json.getString(TAG_MAX_NUM_UPSLOTS_PER_TORRENT);
                    global_upload = json.getString(TAG_GLOBAL_UPLOAD);
                    global_download = json.getString(TAG_GLOBAL_DOWNLOAD);
                    alt_upload = json.getString(TAG_ALT_UPLOAD);
                    alt_download = json.getString(TAG_ALT_DOWNLOAD);
                    torrent_queueing = json.getBoolean(TAG_TORRENT_QUEUEING);
                    max_act_downloads = json.getString(TAG_MAX_ACT_DOWNLOADS);
                    max_act_uploads = json.getString(TAG_MAX_ACT_UPLOADS);
                    max_act_torrents = json.getString(TAG_MAX_ACT_TORRENTS);

                    schedule_alternative_rate_limits = json.getBoolean(TAG_SCHEDULER_ENABLED);
                    alt_from_hour = json.getString(TAG_SCHEDULE_FROM_HOUR);
                    alt_from_min = json.getString(TAG_SCHEDULE_FROM_MIN);
                    alt_to_hour = json.getString(TAG_SCHEDULE_TO_HOUR);
                    alt_to_min = json.getString(TAG_SCHEDULE_TO_MIN);
                    scheduler_days = json.getString(TAG_SCHEDULER_DAYS);

                    // Save options locally
                    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    Editor editor = sharedPrefs.edit();

                    // Save key-values
                    editor.putString("global_max_num_connections", global_max_num_connections);
                    editor.putString("max_num_conn_per_torrent", max_num_conn_per_torrent);
                    editor.putString("max_num_upslots_per_torrent", max_num_upslots_per_torrent);
                    editor.putString("global_upload", global_upload);
                    editor.putString("global_download", global_download);
                    editor.putString("alt_upload", alt_upload);
                    editor.putString("alt_download", alt_download);
                    editor.putBoolean("torrent_queueing", torrent_queueing);
                    editor.putString("max_act_downloads", max_act_downloads);
                    editor.putString("max_act_uploads", max_act_uploads);
                    editor.putString("max_act_torrents", max_act_torrents);

                    editor.putBoolean("schedule_alternative_rate_limits", schedule_alternative_rate_limits);
                    editor.putString("alt_from", alt_from_hour + ":" + alt_from_min);
                    editor.putString("alt_to", alt_to_hour + ":" + alt_to_min);
                    editor.putString("scheduler_days", scheduler_days);


                    // Commit changes
                    editor.commit();

                } catch (Exception e) {
                    Log.e("MAIN:", e.toString());
                    return null;
                }

            }

            // Return getSettings or setSettings
            return params[1];

        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {

                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();

                // Handle HTTP status code

                if (httpStatusCode == 1) {
                    Toast.makeText(getApplicationContext(), R.string.error1, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;
                }

                if (httpStatusCode == 401) {
                    Toast.makeText(getApplicationContext(), R.string.error401, Toast.LENGTH_LONG).show();
                    httpStatusCode = 0;
                }

                if (httpStatusCode == 403 || httpStatusCode == 404) {
                    Toast.makeText(getApplicationContext(), R.string.error403, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;

                    if (qb_version.equals("3.2.x")) {
                        // Get new Cookie
                        cookie = null;
                    }
                }

            } else {

                // Set options with the preference UI

                if (result.equals("setOptions")) {

                    // Open options activity
                    openOptions();
                }

                // Get options only
                if (result.equals("getOptions")) {

                    // Do nothing

                }
            }
        }
    }

    // Drawer classes
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }
}

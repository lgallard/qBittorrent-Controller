/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    // Params to get JSON Array
    private static String[] params = new String[2];

    // JSON Node Names
//    protected static final String TAG_USER = "user";
//    protected static final String TAG_ID = "id";
//    protected static final String TAG_ALTDWLIM = "alt_dl_limit";
//    protected static final String TAG_DWLIM = "dl_limit";

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

    protected static JSONParser jParser;

    protected static final int SETTINGS_CODE = 0;
    protected static final int OPTION_CODE = 1;
    protected static final int GETPRO_CODE = 2;

    // Preferences properties
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean oldVersion;
    protected static boolean https;
    protected static boolean auto_refresh;
    protected static int refresh_period;
    protected static int connection_timeout;
    protected static int data_timeout;
    protected static String sortby;
    protected static boolean reverse_order;
    protected static boolean dark_ui;
    protected static String lastState;

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

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;

    static Torrent[] lines;
    static String[] names;

    TextView name1, size1;

    // Drawer properties
    private String[] navigationDrawerItemTitles;
    protected DrawerLayout drawerLayout;
    private ListView drawerList;
    private CharSequence drawerTitle;
    private CharSequence title;
    // For app icon control for navigation drawer, add new property on
    // MainActivity
    private ActionBarDrawerToggle drawerToggle;

    private ItemstFragment firstFragment;
    private AboutFragment secondFragment;
    private HelpFragment helpTabletFragment;
    private AboutFragment aboutFragment;

    private boolean okay = false;

    // Auto-refresh
    private Handler handler;
    private boolean canrefresh = true;

    private AdView adView;

    // For checking if the app is visible
    private boolean activityIsVisible = true;

    // Item list position
    private int itemPosition = 0;

    // Searching field
    private String searchField = "";

    // Progress bar
    protected static ProgressBar progressBar;

    // myAdapter myadapter
    TorrentListAdapter myadapter;

    // Http status code
    public int httpStatusCode = 0;

    // Cookie (SID - Session ID)
    public static String cookie = null;
    // qb Version
    public static String qb_version = "3.1.x";

    // Service
    private State qbServiceConnection = null;

    private String qbQueryString = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Test service
//        qbServiceConnection = new State();
//
//        getApplicationContext().bindService(new Intent(this, QBService.class), qbServiceConnection, BIND_AUTO_CREATE);
//
//        qbServiceConnection.attach(this);


        // Get preferences
        getSettings();

        if (qb_version.equals("3.2.x")) {
            if (cookie == null || cookie.equals("")) {
                new qBittorrentCookie().execute();
            }
        } else {
            cookie = "";
        }

        // Set Theme (It must be fore inflating or setContentView)
        if (dark_ui) {
            this.setTheme(R.style.Theme_Dark);
        } else {
            this.setTheme(R.style.Theme_Light);
        }

        setContentView(R.layout.activity_main);

        // Get progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBarConnecting);

        // Set App title
        setTitle(R.string.app_shortname);

        // Drawer menu
        navigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Drawer item list objects
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[9];

        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_drawer_all, navigationDrawerItemTitles[0]);
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_drawer_downloading, navigationDrawerItemTitles[1]);
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_drawer_completed, navigationDrawerItemTitles[2]);
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_drawer_paused, navigationDrawerItemTitles[3]);
        drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_drawer_active, navigationDrawerItemTitles[4]);
        drawerItem[5] = new ObjectDrawerItem(R.drawable.ic_drawer_inactive, navigationDrawerItemTitles[5]);
        drawerItem[6] = new ObjectDrawerItem(R.drawable.ic_action_options, navigationDrawerItemTitles[6]);
        drawerItem[7] = new ObjectDrawerItem(R.drawable.ic_drawer_settings, navigationDrawerItemTitles[7]);
        drawerItem[8] = new ObjectDrawerItem(R.drawable.ic_drawer_pro, navigationDrawerItemTitles[8]);

        // Create object for drawer item OnbjectDrawerItem
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        drawerList.setAdapter(adapter);

        // Set selecction according to last state
        if (lastState != null) {

            if (lastState.equals("all")) {
                drawerList.setItemChecked(0, true);
                setTitle(navigationDrawerItemTitles[0]);
            }

            if (lastState.equals("downloading")) {
                drawerList.setItemChecked(1, true);
                setTitle(navigationDrawerItemTitles[1]);
            }

            if (lastState.equals("completed")) {
                drawerList.setItemChecked(2, true);
                setTitle(navigationDrawerItemTitles[2]);
            }

            if (lastState.equals("paused")) {
                drawerList.setItemChecked(3, true);
                setTitle(navigationDrawerItemTitles[3]);
            }

            if (lastState.equals("active")) {
                drawerList.setItemChecked(4, true);
                setTitle(navigationDrawerItemTitles[4]);
            }

            if (lastState.equals("inactive")) {
                drawerList.setItemChecked(5, true);
                setTitle(navigationDrawerItemTitles[5]);
            }

        } else {
            // Set "All" checked
            drawerList.setItemChecked(0, true);

            // Set title to All
            setTitle(navigationDrawerItemTitles[0]);
        }
        // Set the item click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Get drawer title
        title = drawerTitle = getTitle();

        // Add the application icon control code inside MainActivity onCreate

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle(title);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(drawerTitle);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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
                firstFragment = new ItemstFragment();
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

            fragmentTransaction.add(R.id.list_frame, helpTabletFragment, "firstFragment");
            fragmentTransaction.add(R.id.content_frame, secondFragment, "secondFragment");
            // .addToBackStack("secondFragment");

            fragmentTransaction.commit();

            // Second fragment will be added in ItemsFRagment's onListItemClick
            // method

        } else {

            // Phones handle just one fragment

            // Create an instance of ItemsFragments
            if (firstFragment == null) {
                firstFragment = new ItemstFragment();
            }
            firstFragment.setSecondFragmentContainer(R.id.one_frame);

            // This i the about fragment, holding a default message at the
            // beginning

            secondFragment = new AboutFragment();

            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {

                // Handle Item list empty due to Fragment stack
                try {
                    FragmentManager fm = getFragmentManager();

                    if (fm.getBackStackEntryCount() == 1 && fm.findFragmentById(R.id.one_frame) instanceof TorrentDetailsFragment) {

                        refreshCurrent();

                    }
                } catch (Exception e) {

                }

                return;
            }

            // Add the fragment to the 'list_frame' FrameLayout
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.one_frame, secondFragment, "firstFragment");

            fragmentTransaction.commit();
        }

        // Activity is visble
        activityIsVisible = true;

        // // Autorefresh
        refreshCurrent();

        handler = new Handler();
        handler.postDelayed(m_Runnable, refresh_period);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        getApplicationContext().unbindService(qbServiceConnection);
    }


    @Override
    public void onResume() {
        super.onResume();
        activityIsVisible = true;

        // Handle Item list empty due to Fragment stack
        try {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            if (fm.getBackStackEntryCount() == 0 && fm.findFragmentById(R.id.one_frame) instanceof ItemstFragment) {

                ItemstFragment fragment = (ItemstFragment) fm.findFragmentById(R.id.one_frame);

                if (fragment.getListView().getCount() == 0) {

                    // Create the about fragment
                    aboutFragment = new AboutFragment();

                    fragmentTransaction.replace(R.id.one_frame, aboutFragment, "firstFragment");

                    fragmentTransaction.commit();

                    // Se titile
                    setTitle(navigationDrawerItemTitles[drawerList.getCheckedItemPosition()]);

                    // Close Contextual Action Bar
                    if (firstFragment != null && firstFragment.mActionMode != null) {
                        firstFragment.mActionMode.finish();
                    }

                    // Refresh current list
                    refreshCurrent();
                }

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

        // Look up the AdView as a resource and load a request.
        adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("itemPosition", itemPosition);
    }

    // Auto-refresh runnable
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            // Toast.makeText(MainActivity.this, "Refresh period: " +
            // refresh_period, Toast.LENGTH_SHORT).show();

            if (auto_refresh == true && canrefresh == true && activityIsVisible == true) {

                if (findViewById(R.id.fragment_container) != null) {
                    refreshCurrent();
                } else {

                    FragmentManager fm = getFragmentManager();

                    if (fm.findFragmentById(R.id.one_frame) instanceof ItemstFragment || fm.findFragmentById(R.id.one_frame) instanceof AboutFragment) {
                        refreshCurrent();
                    }

                }
            }

            MainActivity.this.handler.postDelayed(m_Runnable, refresh_period);
        }

    };// runnable

    public void refreshCurrent() {
        if (!hostname.equals("")) {

            switch (drawerList.getCheckedItemPosition()) {
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
                    refresh("paused");
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
        }

    }

    // Drawer's method

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
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


            if (cookie == null || cookie.equals("")) {
                new qBittorrentCookie().execute();
            }

//            Log.i("REFRESH", "Cookie:" + cookie);

        }

        params[1] = state;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && !networkInfo.isFailover()) {

            // Load banner
            loadBanner();

            if (hostname.equals("")) {
                // Hide progressBar
                if (progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                //
                genericOkDialog(R.string.info, R.string.about_help1);
            } else {

                // Show progressBar
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                // Execute the task in background
                qBittorrentTask qtt = new qBittorrentTask();

                qtt.execute(params);

                // Close Contextual Action Bar
                if (firstFragment != null && firstFragment.mActionMode != null) {
                    firstFragment.mActionMode.finish();
                }

            }
        } else {

            // Connection Error message
            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();

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

            // Search results
            refreshCurrent();
        }

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            // Add torrent (file, url or magnet)
            addTorrentByIntent(intent);

            // // // Autorefresh
            refreshCurrent();

        }
    }

    private void addTorrentByIntent(Intent intent) {

        String urlTorrent = intent.getDataString();

        if (urlTorrent != null && urlTorrent.length() != 0) {

            if (urlTorrent.substring(0, 4).equals("file")) {

                // File
                addTorrentFile(Uri.parse(urlTorrent).getPath());

            } else {

                // Web
                addTorrent(Uri.decode(urlTorrent));
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public TorrentDetailsFragment getTorrentDetailsFragment() {

        TorrentDetailsFragment tf = null;

        if (findViewById(R.id.fragment_container) != null) {
            tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.content_frame);
        } else {

            if (getFragmentManager().findFragmentById(R.id.one_frame) instanceof TorrentDetailsFragment) {

                tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.one_frame);
            }

        }
        return tf;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        TorrentDetailsFragment tf = null;
        int position;
        String hash;
        AlertDialog.Builder builder;
        AlertDialog dialog;

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Refresh option clicked.
                switch (drawerList.getCheckedItemPosition()) {
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
                        refresh("paused");
                        break;
                    case 4:
                        refresh("active");
                        break;
                    case 5:
                        refresh("inactive");
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    default:
                        selectItem(0);
                        break;
                }
                return true;
            case R.id.action_add:
                // Add URL torrent
                addUrlTorrent();
                return true;

            case R.id.action_pause:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    pauseTorrent(hash);

                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;
            case R.id.action_resume:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    startTorrent(hash);

                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;
            case R.id.action_delete:

                okay = false;

                if (!isFinishing()) {

                    builder = new AlertDialog.Builder(this);

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

                            TorrentDetailsFragment tf = null;
                            int position;
                            String hash;

                            if (findViewById(R.id.fragment_container) != null) {
                                tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.content_frame);
                            } else {

                                if (getFragmentManager().findFragmentById(R.id.one_frame) instanceof TorrentDetailsFragment) {

                                    tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.one_frame);
                                }

                            }

                            if (tf != null) {
                                position = tf.position;
                                hash = MainActivity.lines[position].getHash();
                                deleteTorrent(hash);
                                if (findViewById(R.id.one_frame) != null) {
                                    getFragmentManager().popBackStack();
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

                    builder = new AlertDialog.Builder(this);

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

                            TorrentDetailsFragment tf = null;
                            int position;
                            String hash;

                            if (findViewById(R.id.fragment_container) != null) {
                                tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.content_frame);
                            } else {

                                if (getFragmentManager().findFragmentById(R.id.one_frame) instanceof TorrentDetailsFragment) {

                                    tf = (TorrentDetailsFragment) getFragmentManager().findFragmentById(R.id.one_frame);
                                }

                            }

                            if (tf != null) {
                                position = tf.position;
                                hash = MainActivity.lines[position].getHash();
                                deleteDriveTorrent(hash);
                                if (findViewById(R.id.one_frame) != null) {
                                    getFragmentManager().popBackStack();
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

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    increasePrioTorrent(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;
            case R.id.action_decrease_prio:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    decreasePrioTorrent(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;
            case R.id.action_max_prio:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    maxPrioTorrent(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;
            case R.id.action_min_prio:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    minPrioTorrent(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
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

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    uploadRateLimitDialog(hash);
                }
                return true;

            case R.id.action_download_rate_limit:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    downloadRateLimitDialog(hash);
                }
                return true;
            case R.id.action_recheck:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    recheckTorrents(hash);
                    // if (findViewById(R.id.one_frame) != null) {
                    // getFragmentManager().popBackStack();
                    // }
                }
                return true;
            case R.id.action_firts_last_piece_prio:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    toggleFirstLastPiecePrio(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;


            case R.id.action_sequential_download:

                tf = this.getTorrentDetailsFragment();

                if (tf != null) {
                    position = tf.position;
                    hash = MainActivity.lines[position].getHash();
                    toggleSequentialDownload(hash);
                    if (findViewById(R.id.one_frame) != null) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;


            case R.id.action_sortby_name:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[0]);
                invalidateOptionsMenu();
                refreshCurrent();
                return true;
            case R.id.action_sortby_eta:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[1]);
                invalidateOptionsMenu();
                refreshCurrent();
                return true;
            case R.id.action_sortby_priority:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[2]);
                refreshCurrent();
                return true;
            case R.id.action_sortby_progress:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[3]);
                refreshCurrent();
                return true;
            case R.id.action_sortby_ratio:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[4]);
                refreshCurrent();
                return true;
            case R.id.action_sortby_downloadSpeed:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[5]);
                refreshCurrent();
                return true;
            case R.id.action_sortby_uploadSpeed:
                saveSortBy(getResources().getStringArray(R.array.sortByValues)[6]);
                refreshCurrent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Set the drawer menu's item to All
        drawerList.setItemChecked(0, true);
        setTitle(navigationDrawerItemTitles[0]);

        if (requestCode == SETTINGS_CODE) {

            // Get values from preferences
            getSettings();

            if (qb_version.equals("3.2.x")) {
                if (cookie == null || cookie.equals("")) {
                    new qBittorrentCookie().execute();
                }
            } else {
                cookie = "";
            }


            // redraw menu
            invalidateOptionsMenu();

            // Select "All" torrents list
//            selectItem(0);
            refresh();


            // Get options from server and save them as shared preferences
            // locally
            qBittorrentOptions qso = new qBittorrentOptions();
            qso.execute(new String[]{qbQueryString + "/preferences", "getSettings"});

            // Now it can be refreshed
            canrefresh = true;

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

            // Put everything in an json object

            json = "{" + json + "}";

            // Set preferences using this json object
            setQBittorrentPrefefrences(json);

            // Now it can be refreshed
            canrefresh = true;

        }

        if (requestCode == GETPRO_CODE) {
            // Select "All" torrents list
            // selectItem(0);|
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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

    private void openSettings() {
        canrefresh = false;
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_CODE);

    }

    private void openOptions() {
        canrefresh = false;
        // Retrieve preferences for options
        Intent intent = new Intent(getBaseContext(), OptionsActivity.class);
        startActivityForResult(intent, OPTION_CODE);

    }

    private void getPRO() {
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
        qtc.execute(new String[]{"pauseAll", null});
    }

    public void resumeAllTorrents() {
        // Execute the task in background
        qBittorrentCommand qtc = new qBittorrentCommand();
        qtc.execute(new String[]{"resumeAll", null});
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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

        switch (drawerList.getCheckedItemPosition()) {
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
                refreshWithDelay("paused", delay);
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

            Builder builder = new AlertDialog.Builder(this);

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
        oldVersion = sharedPrefs.getBoolean("old_version", false);
        https = sharedPrefs.getBoolean("https", false);

        // Check https
        if (https) {

            protocol = "https";

        } else {
            protocol = "http";
        }

        auto_refresh = sharedPrefs.getBoolean("auto_refresh", true);
        refresh_period = Integer.parseInt(sharedPrefs.getString("refresh_period", "120000"));

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

        sortby = sharedPrefs.getString("sortby", "NULL");
        reverse_order = sharedPrefs.getBoolean("reverse_order", false);

        dark_ui = sharedPrefs.getBoolean("dark_ui", false);

        qb_version = sharedPrefs.getString("qb_version", "3.1.x");

        MainActivity.cookie = sharedPrefs.getString("qbCookie", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);


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


//            Log.i("qBittorrentCookie =>", "qBittorrentCookie");

            try {

                cookie = jParser.getNewCookie();
//                api = jParser.getApiVersion();

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();

//                Log.i("qBittorrentCookie", "httpStatusCode: " + httpStatusCode);
//                Log.e("qBittorrentCookie", e.toString());

            }

            if (cookie == null) {
                cookie = "";

            }

            if (api == null) {
                api = "";

            }
//
//            Log.i("qBittorrentCookie", "COOKIE: " + ">" + cookie + "<");
//            Log.i("qBittorrentCookie", "API: >" + api + "<");

            return new String[]{cookie, api};

        }

        @Override
        protected void onPostExecute(String[] result) {
//            Log.i("qBittorrentCookie", "httpStatusCode:" + httpStatusCode);


            MainActivity.cookie = result[0];


            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qbCookie", result[0]);


            // Commit changes
            editor.apply();

        }
    }

    // Here is where the action happens
    private class qBittorrentCommand extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            jParser.setCookie(cookie);

            try {

                jParser.postCommand(params[0], params[1]);

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();
                Log.e("JSONParserStatusCodeException", e.toString());

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

            if (httpStatusCode == 403) {
                if (qb_version.equals("3.2.x")) {

                    // Get new Cookie
                    cookie = "";
                    new qBittorrentCookie().execute();

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

                // Needed to refresh after a resume and see the change
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


            if ("setQBittorrentPrefefrences".equals(result)) {
                messageId = R.string.setQBittorrentPrefefrences;
            }

            if ("setUploadRateLimit".equals(result)) {
                messageId = R.string.setUploadRateLimit;
                if (findViewById(R.id.one_frame) != null) {
                    getFragmentManager().popBackStack();
                }
            }

            if ("setDownloadRateLimit".equals(result)) {
                messageId = R.string.setDownloadRateLimit;
                if (findViewById(R.id.one_frame) != null) {
                    getFragmentManager().popBackStack();
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

            String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, downloaded, eta, uploadSpeed, downloadSpeed;
            boolean sequentialDownload = false;
            boolean firstLastPiecePrio = false;

            Torrent[] torrents = null;

            // Get settings
            getSettings();

            try {
                // Creating new JSON Parser
                jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

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

                        // Info
                        torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + progress + " " + Character.toString('\u2022') + " "
                                + torrents[i].getEta());

                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
                torrents = null;
                Log.e("JSONParserStatusCodeException", e.toString());
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
                }

                if (httpStatusCode == 401) {
                    Toast.makeText(getApplicationContext(), R.string.error401, Toast.LENGTH_LONG).show();
                    httpStatusCode = 0;
                }

                if (httpStatusCode == 403) {

                    if (qb_version.equals("3.2.x")) {

                        // Get new Cookie
                        cookie = "";
                        new qBittorrentCookie().execute();
                    }

                    Toast.makeText(getApplicationContext(), R.string.error403, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;
                }

//                // Set App title
//                setTitle(R.string.app_shortname);
//
//                // Uncheck any item on the drawer menu
//                for (int i = 0; i < drawerList.getCount(); i++) {
//                    drawerList.setItemChecked(i, false);
//                }

            } else {

                ArrayList<Torrent> torrentsFiltered;

                if (qb_version.equals("3.2.x")) {
                    torrentsFiltered = new ArrayList<Torrent>(Arrays.asList(result));
                } else {


                    torrentsFiltered = new ArrayList<Torrent>();

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

                        if (params[1].equals("paused") && (searchField == "" || result[i].getFile().toUpperCase().contains(searchField.toUpperCase()))) {
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
                }
                // Sort by filename
                if (sortby.equals("Name")) {
                    Collections.sort(torrentsFiltered, new TorrentNameComparator(reverse_order));
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

                try {

                    for (int i = 0; i < torrentsFiltered.size(); i++) {

                        Torrent torrent = torrentsFiltered.get(i);

                        MainActivity.names[i] = torrent.getFile();
                        MainActivity.lines[i] = torrent;
                    }

                    firstFragment = new ItemstFragment();

//                    if (myadapter == null) {
                    myadapter = new TorrentListAdapter(MainActivity.this, names, lines);
                    firstFragment.setListAdapter(myadapter);

//                    }else{

                    myadapter.setNames(names);
                    myadapter.setData(lines);
                    myadapter.notifyDataSetChanged();

//                    }
                    // Create the about fragment
                    aboutFragment = new AboutFragment();

                    // Add the fragment to the 'list_frame' FrameLayout
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Got some results
                    if (torrentsFiltered.size() > 0) {

                        // Assign the first and second fragment, and
                        // set the second fragment container
                        if (findViewById(R.id.fragment_container) != null) {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.content_frame);

                            // Set first fragment
                            fragmentTransaction.replace(R.id.list_frame, firstFragment);

                            // Reset back button stack
                            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                            // Set second fragment with About fragment
                            fragmentTransaction.replace(R.id.content_frame, aboutFragment);

                        } else {

                            // Set where is the second container
                            firstFragment.setSecondFragmentContainer(R.id.one_frame);

                            // Set first and only fragment
                            fragmentTransaction.replace(R.id.one_frame, firstFragment, "firstFragment");

                            // Destroy About fragment
                            fragmentTransaction.remove(secondFragment);

                            // Reset back button stack
                            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack();
                            }
                        }

                    } else {

                        // No results
                        String[] emptyList = new String[]{getString(R.string.no_results)};
                        firstFragment.setListAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.no_items_found, R.id.no_results, emptyList));

                        // Set the second fragments container
                        if (findViewById(R.id.fragment_container) != null) {
                            firstFragment.setSecondFragmentContainer(R.id.content_frame);
                            fragmentTransaction.replace(R.id.list_frame, firstFragment);
                            fragmentTransaction.replace(R.id.content_frame, aboutFragment);

                        } else {
                            firstFragment.setSecondFragmentContainer(R.id.one_frame);
                            fragmentTransaction.replace(R.id.one_frame, firstFragment, "firstFragment");

                            // Destroy About fragment
                            fragmentTransaction.remove(secondFragment);

                            // Reset back button stack
                            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack();
                            }
                        }

                    }

                    // Commit
                    fragmentTransaction.commit();

                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("ADAPTER", e.toString());
                }

                // Clear search field

                searchField = "";

            }

            // Hide progressBar
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    // Here is where the action happens
    private class qBittorrentOptions extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get settings
            getSettings();

            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            jParser.setCookie(cookie);

            // Get the Json object
            JSONObject json = null;
            try {
                json = jParser.getJSONFromUrl(params[0]);

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();
                Log.e("JSONParserStatusCodeException", e.toString());
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

                if (httpStatusCode == 403) {
                    Toast.makeText(getApplicationContext(), R.string.error403, Toast.LENGTH_SHORT).show();
                    httpStatusCode = 0;

                    if (qb_version.equals("3.2.x")) {

                        // Get new Cookie
                        cookie = "";
                        new qBittorrentCookie().execute();

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

    // Print torrent list from Service
    protected void printTorrentList(Torrent[] torrents) {

        try {
            for (int i = 0; i < torrents.length; i++) {
                Log.i("QBService", "Name: " + torrents[i].getFile());
            }
        } catch (Exception e) {
            Log.e("QBService", e.toString());
        }
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


//        // Notify individually and remove form completed list
//        Iterator it = completedTorrents.entrySet().iterator();
//        while (it.hasNext()) {
//
//            HashMap.Entry pairs = (HashMap.Entry) it.next();
//
//            Torrent t = (Torrent) pairs.getValue();
//
//            Log.i("Completed", t.getFile() + " - completed");
//
//            // Remove it
//            completedTorrents.remove(pairs.getKey());
//
//            it.remove(); // avoids a ConcurrentModificationException
//        }
    }


    private void saveLastState(String state) {
        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Editor editor = sharedPrefs.edit();

        // Save key-values
        editor.putString("lastState", state);


        // Commit changes
        editor.apply();

    }


    private void saveSortBy(String sortBy) {
        // Save options locally
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Editor editor = sharedPrefs.edit();

        // Save key-values
        editor.putString("sortby", sortBy);


        // Commit changes
        editor.apply();

    }
    private void selectItem(int position) {

        // Fragment fragment = null;

        switch (position) {
            case 0:
                refresh("all");
                saveLastState("all");
                break;
            case 1:
                refresh("downloading");
                saveLastState("downloading");
                break;
            case 2:
                refresh("completed");
                saveLastState("completed");
                break;
            case 3:
                refresh("paused");
                saveLastState("paused");
                break;
            case 4:
                refresh("active");
                saveLastState("active");
                break;
            case 5:
                refresh("inactive");
                saveLastState("inactive");
                break;
            case 6:
                // Options - Execute the task in background
                Toast.makeText(getApplicationContext(), R.string.getQBittorrentPrefefrences, Toast.LENGTH_SHORT).show();
                qBittorrentOptions qso = new qBittorrentOptions();
                qso.execute(new String[]{qbQueryString + "/preferences", "setOptions"});
                break;
            case 7:
                // Settings
                openSettings();
                break;
            case 8:
                // Get Pro version
                getPRO();
                break;
            default:
                break;
        }

        // if (fragment != null || listFragment != null || contentFragment !=
        // null) {
        // // FragmentManager fragmentManager = getFragmentManager();
        // // fragmentManager.beginTransaction()
        // // .replace(R.id.content_frame, fragment).commit();

        if (position < 6) {
            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            setTitle(navigationDrawerItemTitles[position]);
        }

        drawerLayout.closeDrawer(drawerList);

    }

    static class State implements QBServiceListener, ServiceConnection {

        QBServiceBinder binder = null;
        MainActivity activity = null;


        void attach(MainActivity activity) {
            this.activity = activity;

            Log.i("State", "Attached");

        }

        // This method update the torrent list (it prints the torrents names for now)
        public void updateTorrentList(Torrent[] torrents) {
            activity.printTorrentList(torrents);
        }

        @Override
        public void notifyCompleted(HashMap completedTorrents) {
            activity.notifyCompleted(completedTorrents);
        }

        public void onServiceConnected(ComponentName className, IBinder rawBinder) {


            Log.i("State", "Trying to connect to service");


            binder = (QBServiceBinder) rawBinder;

            Log.i("State", "Connected to service");


            // Here we are connected to the service, so we can ask for the torrent list
            binder.getTorrentList(this);

        }

        public void onServiceDisconnected(ComponentName className) {
            binder = null;
        }


    }
}

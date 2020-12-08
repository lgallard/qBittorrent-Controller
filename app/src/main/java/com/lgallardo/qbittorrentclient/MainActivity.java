 /*
  *   Copyright (c) 2014-2015 Luis M. Gallardo D.
  *   All rights reserved. This program and the accompanying materials
  *   are made available under the terms of the GNU Lesser General Public License v3.0
  *   which accompanies this distribution, and is available at
  *   http://www.gnu.org/licenses/lgpl.html
  *
  */
 package com.lgallardo.qbittorrentclient;

 import android.Manifest;
 import android.app.AlarmManager;
 import android.app.AlertDialog;
 import android.app.AlertDialog.Builder;
 import android.app.FragmentManager;
 import android.app.FragmentTransaction;
 import android.app.Notification;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.app.SearchManager;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.content.SharedPreferences.Editor;
 import android.content.pm.PackageInfo;
 import android.content.pm.PackageManager;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.net.Uri;
 import android.net.wifi.WifiInfo;
 import android.net.wifi.WifiManager;
 import android.os.AsyncTask;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.SystemClock;
 import android.preference.PreferenceManager;
 import android.provider.Settings;
 import android.support.v4.app.ActivityCompat;
 import android.support.v4.content.ContextCompat;
 import android.support.v4.view.GravityCompat;
 import android.support.v4.view.MenuItemCompat;
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
 import android.view.inputmethod.InputMethodManager;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.AutoCompleteTextView;
 import android.widget.CheckBox;
 import android.widget.EditText;
 import android.widget.LinearLayout;
 import android.widget.ListView;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.android.volley.AuthFailureError;
 import com.android.volley.NetworkResponse;
 import com.android.volley.NoConnectionError;
 import com.android.volley.Request;
 import com.android.volley.Response;
 import com.android.volley.TimeoutError;
 import com.android.volley.VolleyError;
 import com.android.volley.toolbox.JsonArrayRequest;
 import com.android.volley.toolbox.JsonObjectRequest;
 import com.android.volley.toolbox.StringRequest;
 import com.google.android.gms.ads.AdRequest;
 import com.google.android.gms.ads.AdView;
 import com.google.gson.Gson;
 import com.google.gson.reflect.TypeToken;

 import com.nbsp.materialfilepicker.ui.FilePickerActivity;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Type;
 import java.net.URI;
 import java.net.URLEncoder;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.regex.Pattern;

 import static com.lgallardo.qbittorrentclient.DrawerItemRecyclerViewAdapter.actionItems;

 interface RefreshListener {
     void swipeRefresh();
 }

 interface TorrentsListCallBack {
     void onSuccess(List<Torrent> list);
 }

 interface CategoriesListCallBack {
     void onSuccess(List<Category> list);
 }

 interface ServerStateCallBack {
     void onSuccess(ServerState serverState);
 }

 interface TransferInfoCallback {
     void onSuccess(TransferInfo transferInfo);
 }

 interface ContentsListCallback {
     void onSuccess(List<ContentFile> list);
 }

 interface TrackersListCallback {
     void onSuccess(List<Tracker> list);
 }

 interface GeneralInfoCallback {
     void onSuccess(GeneralInfo generalInfo);
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
     protected static final String TAG_ADDEDON = "added_on";
     protected static final String TAG_COMPLETIONON = "completion_on";
     protected static final String TAG_CATEGORY = "category";

     protected static final String TAG_NUMLEECHS = "num_leechs";
     protected static final String TAG_NUMSEEDS = "num_seeds";
     protected static final String TAG_RATIO = "ratio";
     protected static final String TAG_PRIORITY = "priority";
     protected static final String TAG_ETA = "eta";
     protected static final String TAG_SEQDL = "seq_dl";
     protected static final String TAG_FLPIECEPRIO = "f_l_piece_prio";
     protected static final String TAG_GLOBAL_MAX_NUM_CONNECTIONS = "max_connec";
     protected static final String TAG_MAX_NUM_CONN_PER_TORRENT = "max_connec_per_torrent";
     protected static final String TAG_MAX_UPLOADS = "max_uploads";
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


     protected static final String TAG_MAX_RATIO_ENABLED = "max_ratio_enabled";
     protected static final String TAG_MAX_RATIO = "max_ratio";
     protected static final String TAG_MAX_RATIO_ACT = "max_ratio_act";

     protected static final int SETTINGS_CODE = 0;
     protected static final int OPTION_CODE = 1;
     protected static final int GETPRO_CODE = 2;
     protected static final int HELP_CODE = 3;
     protected static final int ADDFILE_CODE = 4;
     private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

     protected static final int SORTBY_NAME = 1;
     protected static final int SORTBY_SIZE = 2;
     protected static final int SORTBY_ETA = 3;
     protected static final int SORTBY_PRIORITY = 4;
     protected static final int SORTBY_PROGRESS = 5;
     protected static final int SORTBY_RATIO = 6;
     protected static final int SORTBY_DOWNLOAD = 7;
     protected static final int SORTBY_UPLOAD = 8;
     protected static final int SORTBY_ADDEDON = 9;
     protected static final int SORTBY_COMPLETEDON = 10;

     // Cookie (SID - Session ID)
     public static String cookie = null;
     public static String qb_version = "4.2.x";
     public static int qb_api = 230;

     public static String qbittorrentServer = "";
     public static LinearLayout headerInfo;

     // Current state
     public static String currentState;

     // Current category
     public static String currentCategory;

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
     protected static int sortby_value;
     protected static boolean reverse_order;
     protected static boolean dark_ui;
     protected static String lastState;
     protected static String lastCategory;
     protected static long notification_period;
     protected static boolean header;
     public static boolean alternative_speeds;
     public static String freeSpaceOnDisk;

     // Option
     protected static String global_max_num_connections;
     protected static String max_num_conn_per_torrent;
     protected static String max_uploads;
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
     protected static int seedingCount;
     protected static int pauseCount;
     protected static int activeCount;
     protected static int inactiveCount;
     protected static boolean schedule_alternative_rate_limits;
     protected static String alt_from_hour;
     protected static String alt_from_min;
     protected static String alt_to_hour;
     protected static String alt_to_min;
     protected static String scheduler_days;
     protected static boolean max_ratio_enabled;
     protected static String max_ratio;
     protected static String max_ratio_act;

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
     public static final int DRAWER_ITEM_ACTION = 1;
     public static final int DRAWER_ITEM_SERVER = 3;
     public static final int DRAWER_SERVERS = 5;
     public static final int DRAWER_CATEGORY = 6;
     public static final int DRAWER_CATEGORIES = 8;

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

     // Package info
     public static String packageName;
     public static String packageVersion;


     // Action (states)
     public static final ArrayList<String> actionStates = new ArrayList<>(Arrays.asList("all", "downloading", "completed", "seeding", "pause", "active", "inactive"));

     // Authentication error counter
     private int connection403ErrorCounter = 0;

     // Alternative rate
     private MenuItem altSpeedLimitsMenuItem;
     private boolean enable_notifications;

     // SSID properties
     protected static String ssid;
     protected static String local_hostname;
     protected static int local_port;

     // Keystore for self-signed certificate
     protected static String keystore_path;
     protected static String keystore_password;

     // Torrent url
     private Intent handledIntent;
     private String urlTorrent;


     // Path and category history
     private Set<String> path_history;
     private Set<String> category_history;

     public static String path2Set;
     public static String category2Set;
     protected static boolean pathAndCategoryDialog = true;


     private Toast toast;
     private AsyncTask<String, Integer, Torrent[]> qbTask;

     // This is the delay before refreshing
     private int delay = 1;

     // Multipart
     private final String twoHyphens = "--";
     private final String lineEnd = "\r\n";

     // Replacement for params[0]
     String urlPrefix = "";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

//        Log.d("Debug", "[onCreate] OK");

         // Get preferences
         getSettings();

//        Log.d("Debug", "[onCreate] getSettings OK");

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

//        // Set alarm for RSS checking, if not set
//        if (PendingIntent.getBroadcast(getApplication(), 0, new Intent(getApplication(), RSSService.class), PendingIntent.FLAG_NO_CREATE) == null) {
//
//            // Set Alarm for checking completed torrents
//            alarmMgr = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(getApplication(), RSSService.class);
//            alarmIntent = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);
//
//            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + 5000,
//                    AlarmManager.INTERVAL_DAY, alarmIntent);
//        }

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


         ArrayList<DrawerItem> serverItems = new ArrayList<>();
         ArrayList<DrawerItem> actionItems = new ArrayList<>();
         ArrayList<DrawerItem> settingsItems = new ArrayList<>();


         // Add server
         serverItems.add(new DrawerItem(R.drawable.ic_drawer_servers, getResources().getString(R.string.drawer_servers_category), DRAWER_SERVERS, false, null));

         // Server items
         int currentServerValue;

         try {
             currentServerValue = Integer.parseInt(MainActivity.currentServer);
         } catch (NumberFormatException e) {
             currentServerValue = 1;
         }

         for (int i = 0; i < navigationDrawerServerItems.length; i++) {
             serverItems.add(new DrawerItem(R.drawable.ic_drawer_subitem, navigationDrawerServerItems[i], DRAWER_ITEM_SERVER, ((i + 1) == currentServerValue), "changeCurrentServer"));
         }

         // Add actions
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_all, navigationDrawerItemTitles[0], DRAWER_ITEM_ACTION, lastState.equals("all"), "refreshAll"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_downloading, navigationDrawerItemTitles[1], DRAWER_ITEM_ACTION, lastState.equals("downloading"), "refreshDownloading"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_completed, navigationDrawerItemTitles[2], DRAWER_ITEM_ACTION, lastState.equals("completed"), "refreshCompleted"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_seeding, navigationDrawerItemTitles[3], DRAWER_ITEM_ACTION, lastState.equals("seeding"), "refreshSeeding"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_paused, navigationDrawerItemTitles[4], DRAWER_ITEM_ACTION, lastState.equals("pause"), "refreshPaused"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_active, navigationDrawerItemTitles[5], DRAWER_ITEM_ACTION, lastState.equals("active"), "refreshActive"));
         actionItems.add(new DrawerItem(R.drawable.ic_drawer_inactive, navigationDrawerItemTitles[6], DRAWER_ITEM_ACTION, lastState.equals("inactive"), "refreshInactive"));

         // Add categories

         // Add settings actions
         //settingsItems.add(new DrawerItem(R.drawable.ic_action_options, navigationDrawerItemTitles[7], DRAWER_ITEM_ACTION, false, "openOptions"));
         settingsItems.add(new DrawerItem(R.drawable.ic_drawer_settings, navigationDrawerItemTitles[8], DRAWER_ITEM_ACTION, false, "openSettings"));

         if (packageName.equals("com.lgallardo.qbittorrentclient")) {
             settingsItems.add(new DrawerItem(R.drawable.ic_drawer_pro, navigationDrawerItemTitles[9], DRAWER_ITEM_ACTION, false, "getPro"));
             settingsItems.add(new DrawerItem(R.drawable.ic_drawer_help, navigationDrawerItemTitles[10], DRAWER_ITEM_ACTION, false, "openHelp"));
         } else {
             settingsItems.add(new DrawerItem(R.drawable.ic_drawer_help, navigationDrawerItemTitles[9], DRAWER_ITEM_ACTION, false, "openHelp"));
         }


         rAdapter = new DrawerItemRecyclerViewAdapter(getApplicationContext(), this, serverItems, actionItems, settingsItems, null);
         rAdapter.notifyDataSetChanged();

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


//        // Get options and save them as shared preferences
//        qBittorrentOptions qso = new qBittorrentOptions();
//        qso.execute(new String[]{qbQueryString + "/preferences", "getSettings"});

         // If it was awoken from an intent-filter,
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

             // Second fragment will be added in ItemsFragment's onListItemClick method

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

//        Log.d("Debug", "[onCreate] Finished");

     }

     // Search bar in Material Design
     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         altSpeedLimitsMenuItem = menu.findItem(R.id.action_toggle_alternative_rate);
         altSpeedLimitsMenuItem.setEnabled(true);
         altSpeedLimitsMenuItem.setChecked(alternative_speeds);
         return super.onPrepareOptionsMenu(menu);
     }

     // Set selection and title on drawer
     public void setSelectionAndTitle(String state) {
         // Set selection according to last state
         if (state != null) {

             currentState = state;

             if (state.equals("all")) {
                 setTitle(navigationDrawerItemTitles[0].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("downloading")) {
                 setTitle(navigationDrawerItemTitles[1].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("completed")) {
                 setTitle(navigationDrawerItemTitles[2].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("seeding")) {
                 setTitle(navigationDrawerItemTitles[3].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("pause")) {
                 setTitle(navigationDrawerItemTitles[4].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("active")) {
                 setTitle(navigationDrawerItemTitles[5].split("\\Q(\\E")[0].trim());
             }

             if (state.equals("inactive")) {
                 setTitle(navigationDrawerItemTitles[6].split("\\Q(\\E")[0].trim());
             }

         } else {
             // Set title to All
             setTitle(navigationDrawerItemTitles[0].split("\\Q(\\E")[0].trim());
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
//                    setTitle(navigationDrawerItemTitles[DrawerItemRecyclerViewAdapter.actionPosition]);
                     setSelectionAndTitle(lastState);

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
//                setTitle(navigationDrawerItemTitles[DrawerItemRecyclerViewAdapter.actionPosition]);
                 setSelectionAndTitle(lastState);

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
         public void run() {

             if (auto_refresh && canrefresh && activityIsVisible) {
                 refreshCurrent();
             }

             MainActivity.this.handler.postDelayed(m_Runnable, refresh_period);
         }

     };// runnable

     public void refreshCurrent() {
         switch (actionStates.indexOf(currentState)) {
             case 0:
                 refresh("all", currentCategory);
                 break;
             case 1:
                 refresh("downloading", currentCategory);
                 break;
             case 2:
                 refresh("completed", currentCategory);
                 break;
             case 3:
                 refresh("seeding", currentCategory);
                 break;
             case 4:
                 refresh("paused", currentCategory);
                 break;
             case 5:
                 refresh("active", currentCategory);
                 break;
             case 6:
                 refresh("inactive", currentCategory);
                 break;
             default:
                 refresh();
                 break;
         }
     }


     @Override
     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

         if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.mSwipeRefreshLayout.isRefreshing()) {
             return;
         }

//        if (v.getId() == R.id.theList) {
         AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;


//        Log.d("Debug", "Chosen: " + menuInfo.toString());
//        Log.d("Debug", "Chosen: " + info.position);

//        Log.d("Debug", "View id: " + v.getId());
//        Log.d("Debug", "View id: " + R.id.RecyclerViewTrackers);


         if (v.getId() == R.id.RecyclerViewContentFiles) {

             getMenuInflater().inflate(R.menu.menu_file_contextual, menu);
         }


//        }
     }

     @Override
     public boolean onContextItemSelected(MenuItem item) {
         AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

//        Log.d("Debug", "Item name: " + getResources().getResourceEntryName(item.getItemId()));
//
//        Log.d("Debug", "Item position: " + TorrentDetailsFragment.fileContentRowPosition);


         switch (item.getItemId()) {

             case R.id.action_file_dont_download:
//                Log.d("Debug", "Don't download");
                 setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 0);
                 return true;
             case R.id.action_file_normal_priority:
//                Log.d("Debug", "Normal priority");
                 setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 1);
                 return true;
             case R.id.action_file_high_priority:
//                Log.d("Debug", "High priority");
                 setFilePrio(TorrentDetailsFragment.hashToUpdate, TorrentDetailsFragment.fileContentRowPosition, 6);
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

         if (currentCategory != null && currentCategory != "") {
             getSupportActionBar().setTitle(title + " (" + currentCategory + ")");
         } else {
             getSupportActionBar().setTitle(title);
         }
     }

     @Override
     protected void onPostCreate(Bundle savedInstanceState) {
         super.onPostCreate(savedInstanceState);
         drawerToggle.syncState();
     }

     @Override
     public void onBackPressed() {

         // If drawer is opened, close it
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
         setTitle(title.split("\\Q(\\E")[0].trim());
         refreshSwipeLayout();
         refresh(state, currentCategory);
         saveLastState(state);
     }

     private void refresh() {

         refresh(currentState, currentCategory);

     }

     // Volley
     protected void addVolleyRequest(JsonObjectRequest jsArrayRequest) {
         VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueueHttps(jsArrayRequest, keystore_path, keystore_password);
     }

     protected void addVolleyRequest(JsonArrayRequest jsArrayRequest) {
         VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueueHttps(jsArrayRequest, keystore_path, keystore_password);
     }

     protected void addVolleyRequest(StringRequest stringArrayRequest) {
         VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueueHttps(stringArrayRequest, keystore_path, keystore_password);
     }

     protected void addVolleyRequest(CustomMultipartRequest customMultipartRequest) {
         VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueueHttps(customMultipartRequest, keystore_path, keystore_password);
     }


     protected void addVolleyRequest(UrlsMultipartRequest urlsMultipartRequest) {
         VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueueHttps(urlsMultipartRequest, keystore_path, keystore_password);
     }

     public interface VolleyCallback {
         void onSuccess(String result);
     }


     private void getApiVersion(final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/app/webapiVersion";


         Log.d("Debug", "[getApiVersion] ApiURL: " + url);

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.GET,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         Log.d("Debug", "[getApiVersion] Response: " + response);

                         Gson gson = new Gson();

                         Float responseFloat = Float.valueOf(-1);

                         try {


                             String jsonString = "{\"apiversion\":" + response + "}";

                             Log.d("Debug", "[getApiVersion] jsonString: " + jsonString);

                             JSONObject jobj = new JSONObject(jsonString);

                             Log.d("Debug", "[getApiVersion] jobj (string) => : " + jobj.toString());

                             //api = gson.fromJson(jsonString, Api.class);


                             responseFloat = gson.fromJson(response, Float.class);


                             Log.d("Debug", "[getApiVersion] responseFloat: " + responseFloat);


                         } catch (JSONException e) {
                             Log.d("Debug", "[getApiVersion] Error: " + e);
                             e.printStackTrace();
                             Log.e("Error", e.toString());
                         }

                         Log.d("Debug", "[getApiVersion] JSONObject: " + response);

                         callback.onSuccess(responseFloat.toString());

                         connection403ErrorCounter = 0;

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         NetworkResponse networkResponse = error.networkResponse;

                         if (networkResponse != null) {
                             Log.d("Debug", "[getApiVersion] statusCode: " + networkResponse.statusCode);

                             if (networkResponse.statusCode == 404) {
                                 Toast.makeText(getApplicationContext(), "Host not found!", Toast.LENGTH_SHORT).show();
                             }

                             if (networkResponse.statusCode == 403) {

                                 Log.d("Debug", "[getApiVersion] connection403ErrorCounter: " + connection403ErrorCounter);

                                 connection403ErrorCounter = connection403ErrorCounter + 1;

//                                if(connection403ErrorCounter >= 2) {
                                 Toast.makeText(getApplicationContext(), "Authentication error!", Toast.LENGTH_SHORT).show();
//                                }
                             }
                         }


                         Log.d("Debug", " [getApiVersion] Error in JSON response: " + error.getMessage());

                         callback.onSuccess("");

                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     protected static String buildURL() {
         String url = "";

         // if server is publish in a subfolder, fix url
         if (subfolder != null && !subfolder.equals("")) {
             url = (subfolder.startsWith("/") ? subfolder : "/" + subfolder) + "/" + url;
         }

         url = protocol + "://" + hostname + (port != -1 ? ":" + port : "") + url;
         return url;
     }

     private void getCookieV(final VolleyCallback callback) {

         Log.d("Debug: ", "[getCookieV] getApi on Volley!");

         String url = buildURL();

         // Command
         url = url + "/api/v2/auth/login";

         Log.d("Debug: ", "[getCookieV] url: " + url);

         // New JSONObject request
         CustomStringRequest jsArrayRequest = new CustomStringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         Log.d("Debug", "[getCookieV] Response: " + response);
//                        Log.d("Debug", "headers: " + CustomStringRequest.headers);

                         JSONObject jsonObject = null;
                         CustomObjectResult customObjectResult;
                         try {
                             jsonObject = new JSONObject(response);
                         } catch (Exception e) {
                             Log.e("Debug", "[getCookieV] error: " + e.getMessage());
                             e.printStackTrace();
                         }

                         Gson gson = new Gson();

                         String cookieString = null;

                         try {
//                            Log.d("Debug", "[getCookieV] JSONObject: " + jsonObject.toString());
                             customObjectResult = gson.fromJson(jsonObject.toString(), CustomObjectResult.class);

//                            Log.d("Debug", "[getCookieV] DATA?: " + customObjectResult.getData());
//                            Log.d("Debug", "[getCookieV] HEADERS?: " + customObjectResult.getHeaders());

                             // Get Headers
                             String headers = customObjectResult.getHeaders();


//                            Log.d("Debug", "[getCookieV] Headers: " + headers);

                             // Get set-cookie from headers
                             cookieString = headers.split("set-cookie=")[1].split(";")[0];


//                            Log.d("Debug", "[getCookieV] set-cookie: " + cookieString);


                         } catch (Exception e) {

                             Log.e("Debug", "[getCookieV] error 2 => " + e.getMessage());
                             e.printStackTrace();
                         }


                         // Return value
                         callback.onSuccess(cookieString);

                         connection403ErrorCounter = 0;

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

//                        Log.d("Debug", "[getCookieV] Error in JSON response: " + error.getMessage());

                         callback.onSuccess("");

                         NetworkResponse networkResponse = error.networkResponse;

                         if (networkResponse != null) {
//                            Log.d("Debug", "[getCookieV] statusCode: " + networkResponse.statusCode);


                             if (networkResponse.statusCode == 403) {
//                                Log.d("Debug", "[getCookieV] trying to gen new cookie - connection403ErrorCounter: " + connection403ErrorCounter);

                                 Toast.makeText(getApplicationContext(), "User's IP is banned for too many failed login attempts!", Toast.LENGTH_SHORT).show();

//                                if (connection403ErrorCounter <= 2) {
//                                    getApi();
//                                }
//
//                                if(connection403ErrorCounter > 2) {
//                                    Toast.makeText(getApplicationContext(), "Please check your account settings!", Toast.LENGTH_SHORT).show();
//                                }
                             }
                         } else {

                             Toast.makeText(getApplicationContext(), "Check your connection settings!", Toast.LENGTH_SHORT).show();
                         }

                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");

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

     private void resumeAllTorrents(final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/resume";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", "all");
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void startTorrent(String hash, final VolleyCallback callback) {

         final String hash_param = hash;
         final String key;

         key = "hashes";

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/resume";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         Log.d("Debug", "Error in JSON response: " + error.getMessage());

                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put(key, hash_param);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void forceStartTorrent(final String hash, final VolleyCallback callback) {

         String url = buildURL();


         // Be aware of this issue https://github.com/qbittorrent/qBittorrent/issues/8958

         // Command
         url = url + "/api/v2/torrents/setForceStart?hashes=" + hash.toLowerCase() + "&value=true";

//         Log.d("Debug", "[forceStartTorrent] url: " + url);
//         Log.d("Debug", "[forceStartTorrent] hashes: " + hash);

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.GET,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "[forceStartTorrent] Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hash);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }


     private void pauseAllTorrents(final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/pause";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", "all");
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void pauseTorrent(String hash, final VolleyCallback callback) {

         final String hash_param = hash;
         final String key;
         key = "hashes";

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/pause";


         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put(key, hash_param);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void deleteTorrent(final String hashes, final VolleyCallback callback) {

         final Map<String, String> postParams = new HashMap<>();

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/delete";

         postParams.put("hashes", hashes);
         postParams.put("deleteFiles", "false");


         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 return postParams;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void deleteDriveTorrent(final String hashes, final VolleyCallback callback) {

         final Map<String, String> postParams = new HashMap<>();

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/delete";

         postParams.put("hashes", hashes);
         postParams.put("deleteFiles", "true");

//        Log.d("Debug", "URL: " + url);

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 return postParams;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void increasePrioTorrent(final String hashes, final VolleyCallback callback) {


         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/increasePrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void decreasePrioTorrent(final String hashes, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/decreasePrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void maxPrioTorrent(final String hashes, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/topPrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void minPrioTorrent(final String hashes, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/bottomPrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void setUpRateLimit(final String hashes, final String limit, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/transfer/uploadLimit";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 params.put("limit", limit);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void setDownRateLimit(final String hashes, final String limit, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/transfer/setDownloadLimit";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 params.put("limit", limit);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void recheckTorrent(final String hash, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/recheck";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hash);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }


     private void toggleFirstLastPiecePrio(final String hashes, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/toggleFirstLastPiecePrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         // Return value
                         callback.onSuccess("");

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void toggleSequentialDownload(final String hashes, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/toggleSequentialDownload";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void setCategory(final String hashes, final String category, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/setCategory";


         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

//                        Log.d("Debug", "[setCategory] response: " + response);

                         // Return value
                         callback.onSuccess("");

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "[setCategory] Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hashes", hashes);
                 params.put("category", category);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void toggleAlternativeSpeedLimits(final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/transfer/toggleSpeedLimitsMode";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void addTorrentUrls(final String urls, final String path2Set, final String category2Set, final VolleyCallback callback) {

//         Log.d("Debug", "[addTorrentUrls] path2set " + path2Set);
//         Log.d("Debug", "[addTorrentUrls] category2Set " + category2Set);


         byte[] multipartBody = null;

         String category = "";
         String savepath = "";


         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/add";

         UrlsMultipartRequest urlsMultipartRequest = new UrlsMultipartRequest(
                 url,
                 new Response.Listener<NetworkResponse>() {
                     @Override
                     public void onResponse(NetworkResponse response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         //Toast.makeText(context, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                     }
                 }) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
//                 params.put("Content-Type", urlContentType);
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("urls", urls);
                 if (path2Set != null && path2Set.length() != 0) {
                     params.put("savepath", path2Set);
                 }
                 if (category2Set != null && category2Set.length() != 0) {
                     params.put("category", category2Set);
                 }
                 return params;
             }

         };

         // Add request to te queue
         addVolleyRequest(urlsMultipartRequest);
     }

     private void addTorrentFileAPI7(final String hash, final String path2Set, final String category2Set, final VolleyCallback callback) {

//         Log.d("Debug", "[addTorrentFileAPI7] path2set " + path2Set);
//         Log.d("Debug", "[addTorrentFileAPI7] category2Set " + category2Set);

         final String boundary = "-----------------------" + (new Date()).getTime();
         final String urlContentType = "multipart/form-data; boundary=" + boundary;
         byte[] multipartBody = null;
         byte[] fileBytesTemp = null;
         final File file = new File(hash);
         String category = "";
         String savepath = "";

         try {
             fileBytesTemp = Common.fullyReadFileToBytes(file);
         } catch (IOException e) {
             e.printStackTrace();
         }

         final byte[] fileBytes = fileBytesTemp;

         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(bos);

         try {

             // Send file multipart form data necessary after file data
             // Category and savepath are set in the multipart body
             buildPart(boundary, dos, fileBytes, file.getName());

             // End of file
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

             // pass to multipart body
             multipartBody = bos.toByteArray();

         } catch (IOException e) {
             Log.d("Debug", "[addTorrentFileAPI7] IOException " + e.getMessage());
             e.printStackTrace();
         }

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/add";

         CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(
                 url,
                 urlContentType,
                 multipartBody,
                 new Response.Listener<NetworkResponse>() {
                     @Override
                     public void onResponse(NetworkResponse response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         //Toast.makeText(context, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                     }
                 }) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
//                 params.put("Content-Type", urlContentType);
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 if (path2Set != null && path2Set.length() != 0) {
                     params.put("savepath", path2Set);
                 }
                 if (category2Set != null && category2Set.length() != 0) {
                     params.put("category", category2Set);
                 }
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(customMultipartRequest);
     }

     private void addTracker(final String hash, final String urlParam, final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/addTrackers";


         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d("Debug", "Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hash", hash);
                 params.put("urls", urlParam);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void getAlternativeSpeedLimitsEnabled(final VolleyCallback callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/transfer/speedLimitsMode";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.GET,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         Gson gson = new Gson();

                         int responseInt = 0;

                         CustomStringResult result = null;
                         try {
                             responseInt = gson.fromJson(response, Integer.class);

                         } catch (Exception e) {
                             e.printStackTrace();
                             Log.e("Error", e.toString());
                         }

                         callback.onSuccess(String.valueOf(responseInt));
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.e("Debug", "[getAlternativeSpeedLimitsEnabled] Error in JSON response: " + error.getMessage());
                         callback.onSuccess("");

                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void setFilePrio(final String hashes, final int idTemp, final int priorityTemp, final VolleyCallback callback) {

         final String id = Integer.toString(idTemp);
         final String priority = Integer.toString(priorityTemp);

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/filePrio";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.e("Debug", "[setFilePrio] Error in JSON response: " + error.getMessage());
                         Toast.makeText(getApplicationContext(), "[setFilePrio] Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("hash", hashes);
                 params.put("id", id);
                 params.put("priority", priority);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     private void setQBittorrentPrefefrences(final String json, final VolleyCallback callback) {


         String url = buildURL();

         // Command
         url = url + "/api/v2/app/setPreferences";

         // New JSONObject request
         StringRequest jsArrayRequest = new StringRequest(
                 Request.Method.POST,
                 url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         // Return value
                         callback.onSuccess("");
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Error executing command: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             @Override
             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 params.put("json", json);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

     }

     // Get all torrents
     private List getTorrentListV(final String state, final String category, final TorrentsListCallBack callback) {

         final List<Torrent> torrents = new ArrayList<>();

         String categoryEncoded = "";
         String url = buildURL();

         // Command
//        url = url + "/api/v2/torrents/info?filter=" + state;
         url = url + "/api/v2/torrents/info";

         url = url + "?filter=" + state;


         if (category != null && !category.equals(getResources().getString(R.string.drawer_category_all))) {

             saveLastCategory(category);

             try {

                 if (!category.equals(getResources().getString(R.string.drawer_category_all).toLowerCase())) {

                     if (category.equals(getResources().getString(R.string.drawer_category_uncategorized))) {
                         categoryEncoded = Uri.encode("http://www.dummy.org?category=");
                     } else {
                         // I used a dummy URL to encode category
                         categoryEncoded = Uri.encode("http://www.dummy.org?category=" + category);
                     }

                     // then I got the the encoded category
                     categoryEncoded = categoryEncoded.substring(categoryEncoded.indexOf("%3D") + 3);

                     // to build the url and pass it to category param
                     url = url + "&category=" + categoryEncoded;
                 }

             } catch (Exception e) {
                 Log.e("Debug", "[getTorrentListV] Category Exception: " + e.toString());
             }

         }


//        Log.d("Debug: ", "[getTorrentListV] URL: " + url);
//        Log.d("Debug: ", "[getTorrentListV] cookies: " + cookie);

//        Log.d("Debug: ", "[getTorrentListV] category: " + category);
//        Log.d("Debug: ", "[getTorrentListV] categoryEncoded: " + categoryEncoded);
//        Log.d("Debug: ", "[getTorrentListV] url: " + url);

//        Log.d("Debug: ", "[getTorrentListV] filter: " + state);

         JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
                 Request.Method.GET,
                 url,
                 null,
                 new Response.Listener<JSONArray>() {
                     @Override
                     public void onResponse(JSONArray response) {

//                         Log.d("Debug: ", "[getTorrentListV] onResponse");

                         // Get list type to parse it
                         Type listType = new TypeToken<List<Torrent>>() {
                         }.getType();

                         // Parse Lists using Gson
                         torrents.addAll((List<Torrent>) new Gson().fromJson(response.toString(), listType));

                         List<Torrent> listTorrent = (List<Torrent>) new Gson().fromJson(response.toString(), listType);

                         // Return value
                         callback.onSuccess(torrents);

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Log.d("Debug", "[getTorrentListV] Connection error!");
                             Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
                         }
                         // Log status code
                         NetworkResponse networkResponse = error.networkResponse;
                         if (networkResponse != null) {
//                            Log.d("Debug", "[getTorrentListV] statusCode: " + networkResponse.statusCode);

                             if (networkResponse.statusCode == 404) {
                                 Toast.makeText(getApplicationContext(), "Host not found!", Toast.LENGTH_SHORT).show();
                             }

                             if (networkResponse.statusCode == 403) {
                                 Log.d("Debug", "[getTorrentListV] trying to gen new cookie - connection403ErrorCounter: " + connection403ErrorCounter);

                                 connection403ErrorCounter = connection403ErrorCounter + 1;

                                 if (connection403ErrorCounter > 1) {
                                     Toast.makeText(getApplicationContext(), "Authentication error!", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }

                         // Log error
                         Log.d("Debug", "[getTorrentListV] Error in JSON response: " + error.getMessage());
                         Log.d("Debug", "[getTorrentListV] Error in JSON error: " + error);


                     }
                 }

         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
//                params.put("filter", state);
//                params.put("category", "test");
//                if (category != null && category.length() != 0) {
//                    params.put("category", category);
//                }
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsArrayRequest);

         // Return the lists
         return torrents;
     }

     // Get all categories
     private List getCategoryListV(final CategoriesListCallBack callback) {

         final List<Category> categories = new ArrayList<>();

         String url = buildURL();

         // Command
         url = url + "/api/v2/torrents/categories";


         JsonObjectRequest jsObjectRequest = new JsonObjectRequest(
                 Request.Method.GET,
                 url,
                 null,
                 new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {

//                        Log.d("Debug: ", "[getCategoryListV] onResponse");

                         Iterator<String> iter = response.keys();
                         while (iter.hasNext()) {
                             String key = iter.next();
                             String name, savePath;
                             try {
                                 JSONObject value = (JSONObject) response.get(key);
                                 name = value.getString("name");
                                 savePath = value.getString("savePath");
//
//                                Log.d("Debug: ", "[getCategoryListV] name: " + name);
//                                Log.d("Debug: ", "[getCategoryListV] savePath: " + savePath);

                                 // Add category to the list
                                 categories.add(new Category(name, savePath));


                             } catch (JSONException e) {
                                 // Something went wrong!
                             }
                         }

                         // Return value
                         callback.onSuccess(categories);

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Log.d("Debug", "[getTorrentListV] Connection error!");
                             Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
                         }
                         // Log status code
                         NetworkResponse networkResponse = error.networkResponse;
                         if (networkResponse != null) {
//                            Log.d("Debug", "[getTorrentListV] statusCode: " + networkResponse.statusCode);

                             if (networkResponse.statusCode == 404) {
                                 Toast.makeText(getApplicationContext(), "Host not found!", Toast.LENGTH_SHORT).show();
                             }

                             if (networkResponse.statusCode == 403) {
                                 Log.d("Debug", "[getCategoryListV] trying to gen new cookie - connection403ErrorCounter: " + connection403ErrorCounter);

                                 connection403ErrorCounter = connection403ErrorCounter + 1;

                                 if (connection403ErrorCounter > 1) {
                                     Toast.makeText(getApplicationContext(), "[getCategoryListV] Authentication error!", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }

                         // Log error
                         Log.d("Debug", "[getCategoryListV] Error in JSON response: " + error.getMessage());
                         Log.d("Debug", "[getCategoryListV] Error in JSON error: " + error);


                     }
                 }

         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsObjectRequest);

         // Return the list
         return categories;
     }

     // Get Transfer Info
     private void getTransferInfoV(final TransferInfoCallback callback) {

         String url = buildURL();

         // Command
//        url = url + "/api/v2/torrents/info?filter=" + state;
         url = url + "/api/v2/transfer/info";


         JsonObjectRequest jsObjectRequest = new JsonObjectRequest(
                 Request.Method.GET,
                 url,
                 null,
                 new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {

//                        Log.d("Debug: ", "[getTransferInfoV] onResponse");


                         // Parse object using Gson

                         final TransferInfo tf = (TransferInfo) new Gson().fromJson(response.toString(), TransferInfo.class);

                         // Return value
                         callback.onSuccess(tf);

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Log.d("Debug", "[getTransferInfoV] Connection error!");
                             Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
                         }
                         // Log status code
                         NetworkResponse networkResponse = error.networkResponse;
                         if (networkResponse != null) {
//                            Log.d("Debug", "[getTransferInfoV] statusCode: " + networkResponse.statusCode);

                         }

                         // Log error
                         Log.d("Debug", "[getTransferInfoV] Error in JSON response: " + error.getMessage());
                         Log.d("Debug", "[getTransferInfoV] Error in JSON error: " + error);


                     }
                 }

         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsObjectRequest);

     }

     // Get serverState
     private void getServerStateV(final ServerStateCallBack callback) {

         String url = buildURL();

         // Command
         url = url + "/api/v2/sync/maindata";


         JsonObjectRequest jsObjectRequest = new JsonObjectRequest(
                 Request.Method.GET,
                 url,
                 null,
                 new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {

//                        Log.d("Debug: ", "[getServerStateV] onResponse");

                         try {

                             JSONObject serverState = response.getJSONObject("server_state");

                             final ServerState ss = (ServerState) new Gson().fromJson(serverState.toString(), ServerState.class);

//                             Log.d("Debug: ", "[getServerStateV] Free_space_on_disk: " + Common.calculateSize(ss.getFree_space_on_disk()));
//                             Log.d("Debug: ", "[getServerStateV] alt_speed_limits: " + ss.isUse_alt_speed_limits());

                             // Return value
                             callback.onSuccess(ss);

                         } catch (JSONException e) {
                             // Something went wrong!
                         }

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                         if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Log.d("Debug", "[getServerStateV] Connection error!");
                             Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
                         }
                         // Log status code
                         NetworkResponse networkResponse = error.networkResponse;
                         if (networkResponse != null) {
//                            Log.d("Debug", "[getServerStateV] statusCode: " + networkResponse.statusCode);

                             if (networkResponse.statusCode == 404) {
                                 Toast.makeText(getApplicationContext(), "Host not found!", Toast.LENGTH_SHORT).show();
                             }

                             if (networkResponse.statusCode == 403) {
                                 Log.d("Debug", "[getServerStateV] trying to gen new cookie - connection403ErrorCounter: " + connection403ErrorCounter);

                                 connection403ErrorCounter = connection403ErrorCounter + 1;

                                 if (connection403ErrorCounter > 1) {
                                     Toast.makeText(getApplicationContext(), "[getServerStateV] Authentication error!", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }

                         // Log error
                         Log.d("Debug", "[getServerStateV] Error in JSON response: " + error.getMessage());
                         Log.d("Debug", "[getServerStateV] Error in JSON error: " + error);


                     }
                 }

         ) {
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("User-Agent", "qBittorrent for Android");
//                 params.put("Host", hostname + ":" + port);
//                 params.put("Referer", protocol + "://" + hostname + ":" + port);
                 params.put("Content-Type", "application/x-www-form-urlencoded");
                 params.put("Cookie", cookie);
                 return params;
             }

             public Map<String, String> getParams() {
                 Map<String, String> params = new HashMap<>();
                 return params;
             }
         };

         // Add request to te queue
         addVolleyRequest(jsObjectRequest);

     }


     // Wraps
     private void getApi() {
         getApiVersion(new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

                 if (result != null && !result.equals("")) {


                     int api;

                     try {
                         api = Integer.parseInt(result.replace(".", ""));
                     } catch (Exception e) {
                         api = 0;
                     }

                     if (api >= 201) {
                         qb_version = "4.1.x";
                         cookie = null;
                         getCookie();

//                        Log.d("Debug: ", "[getApi] getApi was executed");
//                        Log.d("Debug: ", "[getApi] - cookie: " + cookie);
                     }

                     if (api >= 230) {
                         qb_version = "4.2.x";
                         cookie = null;
                         getCookie();

//                        Log.d("Debug: ", "[getApi] getApi was executed");
//                        Log.d("Debug: ", "[getApi] - cookie: " + cookie);
                     }


                     qb_api = api;
                     qbittorrentServer = result;
                 }
             }
         });
     }

     private void getCookie() {
         getCookieV(new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", "[getCookie] getApi on Success!");

                 MainActivity.cookie = result;

                 // Save cookie
                 savePreferenceAsString("qbCookie", result);

                 // Test
//                getTorrentList(lastState, lastCategory);


             }
         });
     }

     private void resumeAllTorrents() {

         resumeAllTorrents(new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

                 toastText(R.string.AllTorrentsResumed);

                 // Refresh
                 refreshAfterCommand(2);

             }
         });
     }

     public void startSelectedTorrents(String hashes) {

         String[] hashesArray = hashes.split("\\|");

         for (int i = 0; hashesArray.length > i; i++) {
             startTorrent(hashesArray[i], true);
         }

         toastText(R.string.torrentsSelectedStarted);

         // Delay of 1 second
         refreshAfterCommand(2);

     }

     private void startTorrent(String hash) {
         startTorrent(hash, false);
     }

     private void startTorrent(String hash, final boolean isSelection) {

         startTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Start Torrent: " + result);

                 if (!isSelection) {
                     toastText(R.string.torrentStarted);

                     // Refresh
                     refreshAfterCommand(delay);

                 }

             }
         });
     }

     public void forceStartSelectedTorrents(String hashes) {

         String[] hashesArray = hashes.split("\\|");

         for (int i = 0; hashesArray.length > i; i++) {
             forceStartTorrent(hashesArray[i], true);
         }

         toastText(R.string.torrentsSelectedStarted);

         // Delay of 1 second
         refreshAfterCommand(2);

     }

     private void forceStartTorrent(String hash) {
         forceStartTorrent(hash, false);
     }

     private void forceStartTorrent(String hash, final boolean isSelection) {

         forceStartTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Start Torrent: " + result);

                 if (!isSelection) {
                     toastText(R.string.torrentStarted);

                     // Refresh
                     refreshAfterCommand(delay);

                 }

             }
         });
     }

     private void pauseAllTorrents() {

         pauseAllTorrents(new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> PauseAll: " + result);
                 toastText(R.string.AllTorrentsPaused);

                 // Refresh
                 refreshAfterCommand(delay);

             }
         });
     }

     public void pauseSelectedTorrents(String hashes) {

         String[] hashesArray = hashes.split("\\|");

         for (int i = 0; hashesArray.length > i; i++) {
             pauseTorrent(hashesArray[i], true);
         }

         toastText(R.string.torrentsSelectedPaused);

         // Delay of 1 second
         refreshAfterCommand(2);

     }

     private void pauseTorrent(String hash, final boolean isSelection) {

         pauseTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Pause Torrent: " + result);

                 if (!isSelection) {
                     toastText(R.string.torrentPaused);

                     // Refresh
                     refreshAfterCommand(delay);

                 }

             }
         });
     }

     private void pauseTorrent(String hash) {
         pauseTorrent(hash, false);
     }

     public void deleteSelectedTorrents(String hashes) {

         deleteTorrent(hashes, true);

         toastText(R.string.torrentsSelectedDeleted);

         // Delay of 1 second
         refreshAfterCommand(2);

     }

     private void deleteTorrent(String hashes) {
         deleteTorrent(hashes, false);
     }

     private void deleteTorrent(String hash, final boolean isSelection) {

         deleteTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Delete Torrent: " + result);

                 if (!isSelection) {
                     toastText(R.string.torrentDeleted);

                     // Refresh
                     refreshAfterCommand(delay);

                 }

             }
         });
     }

     public void deleteDriveSelectedTorrents(String hashes) {

         deleteDriveTorrent(hashes, true);

         toastText(R.string.torrentsSelectedDeletedDrive);

         // Delay of 1 second
         refreshAfterCommand(2);

     }

     private void deleteDriveTorrent(String hashes) {
         deleteDriveTorrent(hashes, false);
     }

     private void deleteDriveTorrent(String hash, final boolean isSelection) {

         deleteDriveTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Delete Drive Torrent: " + result);

                 if (!isSelection) {
                     toastText(R.string.torrentDeletedDrive);

                     // Refresh
                     refreshAfterCommand(delay);

                 }

             }
         });
     }

     public void increasePrioTorrent(String hashes) {

         increasePrioTorrent(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Increase priority: " + result);

                 toastText(R.string.increasePrioTorrent);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void decreasePrioTorrent(String hashes) {

         decreasePrioTorrent(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Increase priority: " + result);

                 toastText(R.string.decreasePrioTorrent);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void maxPrioTorrent(String hashes) {

         maxPrioTorrent(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Max priority: " + result);

                 toastText(R.string.priorityUpdated);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void minPrioTorrent(String hashes) {

         minPrioTorrent(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Min priority: " + result);

                 toastText(R.string.priorityUpdated);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void setUpRateLimit(String hashes, String limit) {

         setUpRateLimit(hashes, limit, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> setUpRateLimit: " + result);

             }
         });

     }

     public void setDownRateLimit(String hashes, String limit) {

         setDownRateLimit(hashes, limit, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> setDownRateLimit: " + result);

             }
         });

     }

     public void recheckTorrent(String hash) {

         recheckTorrent(hash, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> [recheckTorrent] result: " + result);

             }
         });

     }

     public void toggleFirstLastPiecePrio(String hashes) {

         toggleFirstLastPiecePrio(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> Toggle first last piece priority: " + result);

                 toastText(R.string.torrentstogglefisrtLastPiecePrio);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void toggleSequentialDownload(String hashes) {

         toggleSequentialDownload(hashes, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> toggleSequentialDownload: " + result);

                 toastText(R.string.torrentstoggleSequentialDownload);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void setCategory(String hashes, String category) {

         setCategory(hashes, category, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {
//                Log.d("Debug: ", "[setCategory] Result: " + result);
                 toastText(R.string.torrentsApplyingChange);
                 // Refresh
                 refreshAfterCommand(3);
             }
         });

     }

     public void toggleAlternativeSpeedLimits() {

         toggleAlternativeSpeedLimits(new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", "[toggleAlternativeSpeedLimits] OK");

                 toastText(R.string.toggledAlternativeRates);

                 // Refresh
                 refreshAfterCommand(2);
                 swipeRefresh();

             }
         });

     }

     public void addTorrent(String hashes, String path, String category) {

         addTorrentUrls(hashes, path, category, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", "[addTorrent] result: " + result);

                 toastText(R.string.torrentAdded);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void addTorrentFileAPI7(String hash, String path, String category) {

         addTorrentFileAPI7(hash, path, category, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug", ">>> addTorrentFile: " + result);

                 toastText(R.string.torrentFileAdded);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

     public void addTracker(String hashes, String url) {

         addTracker(hashes, url, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> addTracker: " + result);
                 toastText(R.string.torrentsApplyingChange);

                 // Refresh
                 refreshAfterCommand(3);

             }
         });

     }

//     private void getAlternativeSpeedLimitsEnabled() {
//         getAlternativeSpeedLimitsEnabled(new VolleyCallback() {
//             @Override
//             public void onSuccess(String result) {
//
//                 Boolean isAlternativeSpeedLimitsEnabled;
//
////                Log.d("Debug: ", "[getAlternativeSpeedLimitsEnabled] result: " + result);
//
//                 if (result != null && !result.equals("")) {
//
//
//                     if ("1".equals(result)) {
//                         alternative_speeds = true;
////                        Log.d("Debug: ", "[getAlternativeSpeedLimitsEnabled] ON");
//                     } else {
//                         alternative_speeds = false;
////                        Log.d("Debug: ", "[getAlternativeSpeedLimitsEnabled] OFF");
//                     }
//
//                     savePreferenceAsBoolean("alternativeSpeedLimitsEnabled", alternative_speeds);
//
//
//                     if (altSpeedLimitsMenuItem != null) {
////                        Log.d("Debug: ", "[getAlternativeSpeedLimitsEnabled] altSpeedLimitsMenuItem not null");
//                         altSpeedLimitsMenuItem.setEnabled(true);
//                         altSpeedLimitsMenuItem.setChecked(alternative_speeds);
//                     }
//                 }
//             }
//         });
//     }

     public void setQBittorrentPrefefrences(String json) {

         setQBittorrentPrefefrences(json, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", ">>> setQBittorrentPrefefrences: " + result);

                 toastText(R.string.setQBittorrentPrefefrences);

             }
         });

     }

     public void setFilePrio(String hashes, int id, int priority) {

         setFilePrio(hashes, id, priority, new VolleyCallback() {
             @Override
             public void onSuccess(String result) {

//                Log.d("Debug: ", "[setFilePrio] Result: " + result);

                 // Refresh
                 refreshAfterCommand(2);
                 swipeRefresh();

             }
         });

     }

     public void getTorrentList(String state, String category) {

         getTorrentListV(state, category, new TorrentsListCallBack() {
             @Override
             public void onSuccess(List<Torrent> torrents) {

                 getServerState();
                 getTransferInfo();

                 String infoString = "";
                 String sizeInfo, downloadedInfo, uploadedInfo, progressInfo, etaInfo, uploadSpeedInfo, downloadSpeedInfo, ratioInfo;

//                Log.d("Debug", "[getTorrentList] torrents.size(): " + torrents.size());

                 for (int i = 0; i < torrents.size(); i++) {

                     connection403ErrorCounter = 0;

//                    Log.d("Debug", "[getTorrentList] File: " + torrents.get(i).getName());
//                    Log.d("Debug", "[getTorrentList] Hash: " + torrents.get(i).getHash());
//                    Log.d("Debug", "[getTorrentList] qb_version: "+ qb_version);
//                    Log.d("Debug", "[getTorrentList] qb_api: "+ qb_api);


                     // Get torrent size
                     sizeInfo = Common.calculateSize(torrents.get(i).getSize());

//                    Log.d("Debug", "[getTorrentList] sizeInfo: " + sizeInfo);
//                    Log.d("Debug", "[getTorrentList] progress raw: " + torrents.get(i).getProgress());

                     double progress = torrents.get(i).getProgress();

                     // Set torrent progress
                     torrents.get(i).setProgress(progress);

                     progressInfo = Common.ProgressForUi(progress);

//                    Log.d("Debug", "[getTorrentList] progress: " + (progress * 100));
//                    Log.d("Debug", "[getTorrentList] progress fixed: " + progressInfo);
//                    Log.d("Debug", "[getTorrentList] torrent state: " + torrents.get(i).getState());

                     // Get downloaded
                     downloadedInfo = Common.calculateSize(torrents.get(i).getDownloaded());

                     // Get uploaded
                     uploadedInfo = Common.calculateSize(torrents.get(i).getUploaded());

                     // Get ETA
                     etaInfo = Common.secondsToEta(torrents.get(i).getEta());

                     // Get upload speed
                     uploadSpeedInfo = Common.calculateSize(torrents.get(i).getUpspeed()) + "/s";

                     // Get download speed
                     downloadSpeedInfo = Common.calculateSize(torrents.get(i).getDlspeed()) + "/s";

                     // Get Ratio
                     ratioInfo = Common.RatioForUi(torrents.get(i).getRatio());


                     if (packageName.equals("com.lgallardo.qbittorrentclient")) {
                         // Info free
                         infoString = downloadedInfo + " / " + sizeInfo + " "
                                 + '\u2193' + " " + downloadSpeedInfo + " "
                                 + '\u2191' + " " + uploadSpeedInfo + " "
                                 + '\u2022' + " " + uploadedInfo + " "
                                 + '\u2022' + " " + ratioInfo + " "
                                 + '\u2022' + " " + progressInfo + "% "
                                 + '\u2022' + " " + etaInfo;

//                        if (torrents.get(i).getCategory() != null && !torrents.get(i).getCategory().equals("")) {
//                            infoString = infoString + " " + Character.toString('\u2022') + " " + torrents.get(i).getCategory();
//                        }


                     } else {
                         // Info pro
                         infoString = downloadedInfo + " / " + sizeInfo + " "
                                 + '\u2193' + " " + downloadSpeedInfo + " "
                                 + '\u2191' + " " + uploadSpeedInfo + " "
                                 + '\u2022' + " " + uploadedInfo + " "
                                 + '\u2022' + " " + ratioInfo + " "
                                 + '\u2022' + " " + etaInfo;

//                         if (torrents.get(i).getCategory() != null && !torrents.get(i).getCategory().equals("")) {
//                             infoString = infoString + " " + Character.toString('\u2022') + " " + torrents.get(i).getCategory();
//                         }


                     }

//                    Log.d("Debug", "[getTorrentList] infoString: " + infoString);

                     // Set info
                     torrents.get(i).setInfo(infoString);
                 }


                 // Reporting
                 if (CustomLogger.isMainActivityReporting()) {
                     CustomLogger.saveReportMessage("Main", "qBittorrentTask - result length: " + torrents.size());
                     //CustomLogger.saveReportMessage("Main", "qBittorrentTask - httpStatusCode: " + httpStatusCode);
                 }

                 ArrayList<Torrent> torrentsFiltered = new ArrayList<Torrent>();

                 // Categories
                 String category = null;


                 for (int i = 0; i < torrents.size(); i++) {

                     if (currentState.equals("all") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         torrentsFiltered.add(torrents.get(i));
                     }

                     if (currentState.equals("downloading") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("downloading".equals(torrents.get(i).getState()) || "stalledDL".equals(torrents.get(i).getState()) || "pausedDL".equals(torrents.get(i).getState())
                                 || "queuedDL".equals(torrents.get(i).getState()) || "checkingDL".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }

                     if (currentState.equals("completed") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("uploading".equals(torrents.get(i).getState()) || "stalledUP".equals(torrents.get(i).getState()) || "pausedUP".equals(torrents.get(i).getState())
                                 || "queuedUP".equals(torrents.get(i).getState()) || "checkingUP".equals(torrents.get(i).getState()) || "forcedUP".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }

                     if (currentState.equals("seeding") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("uploading".equals(torrents.get(i).getState()) || "stalledUP".equals(torrents.get(i).getState()) || "forcedUP".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }


                     if (currentState.equals("pause") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("pausedDL".equals(torrents.get(i).getState()) || "pausedUP".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }

                     if (currentState.equals("active") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("uploading".equals(torrents.get(i).getState()) || "downloading".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }

                     if (currentState.equals("inactive") && (searchField == "" || torrents.get(i).getName().toUpperCase().contains(searchField.toUpperCase()))) {
                         if ("pausedUP".equals(torrents.get(i).getState()) || "pausedDL".equals(torrents.get(i).getState()) || "queueUP".equals(torrents.get(i).getState())
                                 || "queueDL".equals(torrents.get(i).getState()) || "stalledUP".equals(torrents.get(i).getState())
                                 || "stalledDL".equals(torrents.get(i).getState())) {
                             torrentsFiltered.add(torrents.get(i));
                         }
                     }

                 }

                 // Categories
                 final ArrayList<DrawerItem> categoryItems = new ArrayList<DrawerItem>();

                 // Set uncategorized first

                 // Add category
                 categoryItems.add(new DrawerItem(R.drawable.ic_drawer_categories, getResources().getString(R.string.drawer_category_categories), DRAWER_CATEGORIES, true, "categories"));

                 // Add All
                 category = getResources().getString(R.string.drawer_category_all);
                 categoryItems.add(new DrawerItem(R.drawable.ic_drawer_subitem, category, DRAWER_CATEGORY, (currentCategory.equals(category)), "category"));

                 // Add uncategorized
                 // TODO: Uncomment to enable uncategorized item
//                category = getResources().getString(R.string.drawer_category_uncategorized);
//                categoryItems.add(new DrawerItem(R.drawable.ic_drawer_subitem, category, DRAWER_CATEGORY, currentCategory.equals(category) || currentCategory.equals(""), "category"));

                 if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {

                     getCategoryListV(new CategoriesListCallBack() {
                         @Override
                         public void onSuccess(List<Category> categories) {
                             Log.d("Debug", "[getCategoryListV] onSuccess");

                             String name, savePath;

                             for (int i = 0; i < categories.size(); i++) {

                                 name = categories.get(i).getName();
                                 savePath = categories.get(i).getSavePath();

//                            Log.d("Debug", "[getCategoryListV] Name: " + name);
//                            Log.d("Debug", "[getCategoryListV] Save Path: " + savePath);

                                 // Add category name to the drawer menu
                                 if (name != null && !name.equals("")) {
                                     categoryItems.add(new DrawerItem(R.drawable.ic_drawer_subitem, name, DRAWER_CATEGORY, currentCategory.equals(name), "category"));
                                 }
                             }

                             rAdapter.refreshDrawerCategories(categoryItems);
                         }
                     });
                 }

                 // Sort by filename
                 if (sortby_value == SORTBY_NAME) {
                     Collections.sort(torrentsFiltered, new TorrentNameComparator(reverse_order));
                 }
                 // Sort by size
                 if (sortby_value == SORTBY_SIZE) {
                     Collections.sort(torrentsFiltered, new TorrentSizeComparator(reverse_order));
                 }
                 // Sort by Eta
                 if (sortby_value == SORTBY_ETA) {
                     Collections.sort(torrentsFiltered, new TorrentEtaComparator(reverse_order));
                 }
                 // Sort by priority
                 if (sortby_value == SORTBY_PRIORITY) {
                     Collections.sort(torrentsFiltered, new TorrentPriorityComparator(reverse_order));
                 }
                 // Sort by progress
                 if (sortby_value == SORTBY_PROGRESS) {
                     Collections.sort(torrentsFiltered, new TorrentProgressComparator(reverse_order));
                 }
                 // Sort by Ratio
                 if (sortby_value == SORTBY_RATIO) {
                     Collections.sort(torrentsFiltered, new TorrentRatioComparator(reverse_order));
                 }
                 // Sort by download speed
                 if (sortby_value == SORTBY_DOWNLOAD) {
                     Collections.sort(torrentsFiltered, new TorrentDownloadSpeedComparator(reverse_order));
                 }
                 // Sort by upload speed
                 if (sortby_value == SORTBY_UPLOAD) {
                     Collections.sort(torrentsFiltered, new TorrentUploadSpeedComparator(reverse_order));
                 }
                 // Sort by Added on
                 if (sortby_value == SORTBY_ADDEDON) {
                     Collections.sort(torrentsFiltered, new TorrentAddedOnTimestampComparator(reverse_order));
                 }
                 // Sort by Completed on
                 if (sortby_value == SORTBY_COMPLETEDON) {
                     Collections.sort(torrentsFiltered, new TorrentCompletedOnTimestampComparator(reverse_order));
                 }

                 // Get names (delete in background method)
                 MainActivity.names = new String[torrentsFiltered.size()];
                 MainActivity.lines = new Torrent[torrentsFiltered.size()];

                 uploadSpeedCount = 0;
                 downloadSpeedCount = 0;

                 uploadCount = 0;
                 downloadCount = 0;
                 seedingCount = 0;
                 pauseCount = 0;
                 activeCount = 0;
                 inactiveCount = 0;

                 List<String> downloadingStates = new ArrayList<>(
                         Arrays.asList(
                                 "downloading", "metaDL", "pausedDL", "queuedDL", "stalledDL", "checkingDL", "forceDL")
                 );

                 List<String> uploadingStates = new ArrayList<>(
                         Arrays.asList(
                                 "uploading", "pausedUP", "queuedUP", "stalledUP", "checkingUP", "forceUP")
                 );

                 List<String> seedingStates = new ArrayList<>(
                         Arrays.asList(
                                 "pausedUP", "queuedUP", "stalledUP")
                 );

                 List<String> pausedStates = new ArrayList<>(
                         Arrays.asList(
                                 "pausedDL", "pausedUP")
                 );

                 List<String> activeStates = new ArrayList<>(
                         Arrays.asList(
                                 "downloading", "uploading", "checkingDL", "checkingUP", "forceDL", "forceUP")
                 );

                 List<String> inactiveStates = new ArrayList<>(
                         Arrays.asList(
                                 "pausedDL", "pausedUP", "queuedDL", "queuedUP", "stalledDL", "stalledUP")
                 );
                 try {

                     Torrent torrentToUpdate = null;

                     // Reporting
                     if (CustomLogger.isMainActivityReporting()) {
                         CustomLogger.saveReportMessage("Main", "qBittorrentTask - torrentsFiltered.size: " + torrentsFiltered.size());
                     }

                     for (int i = 0; i < torrentsFiltered.size(); i++) {

                         Torrent torrent = torrentsFiltered.get(i);

                         MainActivity.names[i] = torrent.getName();
                         MainActivity.lines[i] = torrent;

                         if (torrent.getHash().equals(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate)) {
                             torrentToUpdate = torrent;
                         }

                         downloadSpeedCount += torrent.getDlspeed();
                         uploadSpeedCount += torrent.getUpspeed();

                         Log.d("Debug", "[getTorrentList] torrent state: " + torrent.getState());

                         for (String s : downloadingStates) {
                             if (s.equals(torrent.getState())) {
                                 downloadCount = downloadCount + 1;
                             }
                         }

                         for (String s : uploadingStates) {
                             if (s.equals(torrent.getState())) {
                                 uploadCount = uploadCount + 1;
                             }
                         }

                         for (String s : seedingStates) {
                             if (s.equals(torrent.getState())) {
                                 seedingCount = seedingCount + 1;
                             }
                         }

                         for (String s : pausedStates) {
                             if (s.equals(torrent.getState())) {
                                 pauseCount = pauseCount + 1;
                             }
                         }

                         for (String s : activeStates) {
                             if (s.equals(torrent.getState())) {
                                 activeCount = activeCount + 1;
                             }
                         }

                         for (String s : inactiveStates) {
                             if (s.equals(torrent.getState())) {
                                 inactiveCount = inactiveCount + 1;
                             }
                         }

                     }

                     // Update torrent list
                     try {
                         myadapter.setNames(names);
                         myadapter.setData(lines);
                         myadapter.notifyDataSetChanged();
                     } catch (NullPointerException ne) {
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

                     if (CustomLogger.isMainActivityReporting()) {
                         CustomLogger.saveReportMessage("Main", "[qBittorrentTask - AdapterException]: " + e.toString());
                     }
                 }

                 // Update drawer counts
                 DrawerItem item;
                 String name;

                 Log.d("Debug", "[getTorrentList] currentState: " + currentState);

                 // All
                 item = actionItems.get(0);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("all")) {
                     name = name + " ( " + torrents.size() + " )";
                 }
                 item.setName(name);
                 actionItems.set(0, item);

                 // Downloading
                 item = actionItems.get(1);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("downloading")) {
                     name = name + " ( " + downloadCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(1, item);

                 // Completed
                 item = actionItems.get(2);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("completed")) {
                     name = name + " ( " + uploadCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(2, item);

                 // Seeding
                 item = actionItems.get(3);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("seeding")) {
                     name = name + " ( " + seedingCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(3, item);

                 // Paused
                 item = actionItems.get(4);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("pause")) {
                     name = name + " ( " + pauseCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(4, item);

                 // Active
                 item = actionItems.get(5);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("active")) {
                     name = name + " ( " + activeCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(5, item);

                 // Inactive
                 item = actionItems.get(6);
                 name = item.getName().split("\\Q(\\E")[0].trim();
                 if (currentState.equals("inactive")) {
                     name = name + " ( " + inactiveCount + " )";
                 }
                 item.setName(name);
                 actionItems.set(6, item);

                 // Send report
                 emailReport();

                 // Disable refreshSwipeLayout
                 disableRefreshSwipeLayout();


             }

         });

     }


     public void getTransferInfo() {

         getTransferInfoV(new TransferInfoCallback() {
             @Override
             public void onSuccess(TransferInfo transferInfo) {

//                 Log.d("Debug", "[getTransferInfo] Up speed: " + transferInfo.getUp_info_speed());
//                 Log.d("Debug", "[getTransferInfo] getDl_info_data: " + transferInfo.getDl_info_data());
//
//
                 // Set headerInfo
                 TextView uploadSpeedTextView = (TextView) findViewById(R.id.upspeed);
                 TextView downloadSpeedTextView = (TextView) findViewById(R.id.dlspeed);
                 TextView header2InfoTextView = (TextView) findViewById(R.id.header2_info);

                 headerInfo = (LinearLayout) findViewById(R.id.header);

                 boolean detailsFragment = findViewById(R.id.details_refresh_layout)!=null && findViewById(R.id.activity_main_swipe_refresh_layout)==null;

                 if (header&!detailsFragment) {
                     headerInfo.setVisibility(View.VISIBLE);
                 } else {
                     headerInfo.setVisibility(View.GONE);
                 }


                 String AltSpeedInfo;

                 if (alternative_speeds) {
                     AltSpeedInfo = '\u2713' + "  ";
                 } else {
                     AltSpeedInfo = "";
                 }

                 uploadSpeedTextView.setText(AltSpeedInfo + Common.calculateSize("" + transferInfo.getUp_info_speed()) + "/s " + '\u2022' + " " + Common.calculateSize(transferInfo.getUp_info_data()) + "  (" + uploadCount + ")");
                 downloadSpeedTextView.setText(Character.toString('\u21C5') + " " + Common.calculateSize("" + downloadSpeedCount) + "/s " + '\u2022' + " " + Common.calculateSize(transferInfo.getDl_info_data()) + " (" + downloadCount + ")");

                 if (freeSpaceOnDisk != null) {
                     // Phone
                     if (header2InfoTextView != null) {
                         downloadSpeedTextView.setText(Character.toString('\u21C5') + " " + Common.calculateSize("" + downloadSpeedCount) + "/s " + '\u2022' + " " + Common.calculateSize(transferInfo.getDl_info_data()) + " (" + downloadCount + ")");
                         header2InfoTextView.setText("Free: " + freeSpaceOnDisk);
                     }
                     // Tablets
                     else {
                         downloadSpeedTextView.setText(Character.toString('\u21C5') + " " + Common.calculateSize("" + downloadSpeedCount) + "/s " + '\u2022' + " " + Common.calculateSize(transferInfo.getDl_info_data()) + " (" + downloadCount + ") " + '\u2022' + " Free: " + freeSpaceOnDisk);
                     }
                 }

             }

         });

     }

     public void getServerState() {

         getServerStateV(new ServerStateCallBack() {
             @Override
             public void onSuccess(ServerState serverState) {
//                 Log.d("Debug", "[getServerState] Free_space_on_disk: " + Common.calculateSize(serverState.getFree_space_on_disk()));
//                 Log.d("Debug: ", "[getServerStateV] alt_speed_limits: " + serverState.isUse_alt_speed_limits());

                 alternative_speeds = serverState.isUse_alt_speed_limits();

                 if (altSpeedLimitsMenuItem != null) {
                     altSpeedLimitsMenuItem.setEnabled(true);
                     altSpeedLimitsMenuItem.setChecked(alternative_speeds);
                 }

                 freeSpaceOnDisk = Common.calculateSize(serverState.getFree_space_on_disk());
             }
         });

     }
     // End of wraps

     // MultiPart file
     private void buildPart(String boundary, DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
         dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
         dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);
         dataOutputStream.writeBytes(lineEnd);


         ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
         int bytesAvailable = fileInputStream.available();

         int maxBufferSize = 1024 * 1024;
         int bufferSize = Math.min(bytesAvailable, maxBufferSize);
         byte[] buffer = new byte[bufferSize];

         // read file and write it into form...
         int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

         while (bytesRead > 0) {
             dataOutputStream.write(buffer, 0, bufferSize);
             bytesAvailable = fileInputStream.available();
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }

         dataOutputStream.writeBytes(lineEnd);

         if (path2Set != null && path2Set.length() != 0) {
             dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
             dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"savepath\"" + lineEnd + lineEnd + path2Set);
             dataOutputStream.writeBytes(lineEnd);
         }

         if (category2Set != null && category2Set.length() != 0) {
             dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
             dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"category\"" + lineEnd + lineEnd + category2Set);
             dataOutputStream.writeBytes(lineEnd);
         }


     }

     private void refresh(String state, String category) {

         // If Contextual Action Bar is open, don't refresh
         if (firstFragment != null && firstFragment.mActionMode != null) {
             return;
         }


//        urlPrefix = "api/v2/torrents/info?filter=" + state;
//        params[0] = "api/v2/torrents/info?filter=" + state;


         // Get API version in case it hadn't been gotten before

         //getApi();
         getCookie();

//        Log.d("Debug", "[refresh] category: " + category);

         ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


         if (networkInfo != null && networkInfo.isConnected() && !networkInfo.isFailover()) {

             // Logs for reporting
             if (CustomLogger.isMainActivityReporting()) {
                 generateSettingsReport();
             }

             if (hostname.equals("")) {
                 qBittorrentNoSettingsFoundDialog(R.string.info, R.string.about_help1);
             } else {

//                Log.d("Report", "Report: " + CustomLogger.getReport());


                 if (qb_version.equals("3.2.x") && (cookie == null || cookie.equals(""))) {
                     // Request new cookie and execute task in background

                     if (connection403ErrorCounter > 1) {

                         toastText(R.string.error403);

                         httpStatusCode = 0;
                         disableRefreshSwipeLayout();
                     } else {
                         getCookie();
                     }

                 } else {

                     if (connection403ErrorCounter > 1) {

                         if (cookie != null && !cookie.equals("")) {
                             // Only toasts the message if there is not a cookie set before
                             toastText(R.string.error403);
                             cookie = null;
                         }

                         httpStatusCode = 0;
                         disableRefreshSwipeLayout();
                     } else {

                         // Execute the task in background
                         //qbTask = new qBittorrentTask().execute(params);

                         // Test
                         getTorrentList(state, category);

                         // Check if  alternative speed limit is set
//                         getAlternativeSpeedLimitsEnabled();
                     }
                 }

             }

         } else {

             // Connection Error message
             toastText(R.string.connection_error);

             disableRefreshSwipeLayout();
         }

     }

     private void toastText(int message) {

         if (activityIsVisible == true) {
             toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
             toast.show();
         }

     }

     // This method adds information to generate a report for support
     private void generateSettingsReport() {
         CustomLogger.saveReportMessage("Main", "currentServer: " + currentServer);
         CustomLogger.saveReportMessage("Main", "hostname: " + hostname);
         CustomLogger.saveReportMessage("Main", "https: " + https);
         CustomLogger.saveReportMessage("Main", "port: " + port);
         CustomLogger.saveReportMessage("Main", "subfolder: " + subfolder);
         CustomLogger.saveReportMessage("Main", "protocol: " + protocol);

         CustomLogger.saveReportMessage("Main", "username: " + username);
         CustomLogger.saveReportMessage("Main", "password: [is" + (password.isEmpty() ? "" : " not") + " empty]");

         CustomLogger.saveReportMessage("Main", "Auto-refresh?: " + auto_refresh);
         CustomLogger.saveReportMessage("Main", "Refresh period: " + refresh_period);

         CustomLogger.saveReportMessage("Main", "Connection timeout: " + connection_timeout);
         CustomLogger.saveReportMessage("Main", "Data timeout: " + data_timeout);

         CustomLogger.saveReportMessage("Main", "dark_ui: " + dark_ui);
         CustomLogger.saveReportMessage("Main", "qb_version: " + qb_version);
         CustomLogger.saveReportMessage("Main", "qBittorrent server: " + qbittorrentServer);


         CustomLogger.saveReportMessage("Main", "Cookie: [is" + ((cookie != null && cookie.isEmpty()) ? "" : " not") + " empty]");
         CustomLogger.saveReportMessage("Main", "enable_notifications: " + enable_notifications);
         CustomLogger.saveReportMessage("Main", "notification_period: " + notification_period);

         CustomLogger.saveReportMessage("Main", "packageName: " + packageName);
         CustomLogger.saveReportMessage("Main", "packageVersion: " + packageVersion);

         CustomLogger.saveReportMessage("Main", "Current state: " + currentState);
         CustomLogger.saveReportMessage("Main", "Last state: " + lastState);

         CustomLogger.saveReportMessage("Main", "Current Category: " + currentCategory);


     }

     public void emailReport() {

         if (CustomLogger.isMainActivityReporting()) {

             Intent emailIntent = new Intent(Intent.ACTION_SEND);

             emailIntent.setType("text/plain");

             emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"qbcontroller@gmail.com"});
             emailIntent.putExtra(Intent.EXTRA_SUBJECT, "qBittorrentController report");

             // Include report
             emailIntent.putExtra(Intent.EXTRA_TEXT, CustomLogger.getReport());

             // Delete report
             CustomLogger.setMainActivityReporting(false);
             CustomLogger.deleteMainReport();
             CustomLogger.deleteNotifierReport();

             // Launch email chooser
             startActivity(Intent.createChooser(emailIntent, "Send qBittorrent report..."));

             // Reporting - Finish report
             CustomLogger.setMainActivityReporting(false);

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

//            Log.d("Debug", "Search for..." + searchField);

             // Autorefresh
             refreshSwipeLayout();
             refreshCurrent();
         }

         if (Intent.ACTION_VIEW.equals(intent.getAction())) {

             // Add torrent (file, url or magnet)
             addTorrentByIntent(intent);

             // // Activity is visible
             activityIsVisible = true;

             // Autorefresh
             refreshCurrent();

         }

         try {


             if (intent.getStringExtra("from").equals("NotifierService")) {

                 saveLastState("completed");
                 setSelectionAndTitle("completed");
                 refresh("completed", currentCategory);
             }

             if (intent.getStringExtra("from").equals("RSSItemActivity")) {

                 // Add torrent (file, url or magnet)
                 addTorrentByIntent(intent);

                 // // Activity is visble
                 activityIsVisible = true;

                 // Autorefresh
                 refreshCurrent();
             }

//            Log.d("Debug", "lastState (handle intent):End " );
         } catch (Exception e) {

//            Log.e("Debug", "Handle intent: " + e.toString() );

         }
     }

     // Based on Trandroid's addTorrentFromStream
     // https://github.com/erickok/transdroid/blob/531051adafdac197295fef3d02e8608e86585c13/app/src/main/java/org/transdroid/core/gui/TorrentsActivity.java#L1157
     public String getFileNameFromStream(InputStream input) {

         String fileName = null;

         File tempFile = new File("/not/yet/set");
         try {
             // Write a temporary file with the torrent contents
             tempFile = File.createTempFile("qbcontroller_", ".torrent", getCacheDir());
             FileOutputStream output = new FileOutputStream(tempFile);
             try {
                 final byte[] buffer = new byte[1024];
                 int read;
                 while ((read = input.read(buffer)) != -1) {
                     output.write(buffer, 0, read);
                 }
                 output.flush();

                 // Get filename
                 fileName = Uri.fromFile(tempFile).toString();

             } finally {
                 output.close();
             }
         } catch (IOException e) {
             Log.e("Debug", "Can't write input stream to " + tempFile.toString() + ": " + e.toString());
         } finally {
             try {
                 if (input != null) {
                     input.close();
                 }
             } catch (IOException e) {
                 Log.e("Debug", "Error closing the input stream " + tempFile.toString() + ": " + e.toString());
             }
         }

         return fileName;
     }


     private void handleUrlTorrent() {

         // permission was granted, yay! Do the
         // contacts-related task you need to do.

//        Log.d("Debug", "[handleUrlTorrent] urlTorrent: " + urlTorrent);
//        Log.d("Debug", "[handleUrlTorrent] category2Set: " + category2Set);

         // if there is not a path to the file, open de file picker
         if (urlTorrent == null) {
             openFilePicker();
         } else {

             try {


                 // Handle format for torrent files on Downloaded list
                 if (urlTorrent.substring(0, 7).equals("content")) {

                     urlTorrent = getFileNameFromStream(getContentResolver().openInputStream(handledIntent.getData()));

//                    Log.d("Debug", "[handleUrlTorrent] urlTorrent path (content): " + urlTorrent);
                 }

                 // Handle format for downloaded torrent files (Ex: /storage/emulated/0/Download/afile.torrent)
                 if (urlTorrent.contains(".torrent") && urlTorrent.substring(0, 1).equals("/")) {

                     if (urlTorrent.substring(0, 1).equals("/")) {

                         // Encode path
                         URI encodedUri = new URI(URLEncoder.encode(urlTorrent, "UTF-8"));

                         // Get raw absolute and add file schema
                         urlTorrent = "file://" + (new File(encodedUri.getRawPath())).getAbsolutePath();

//                        Log.d("Debug", "[handleUrlTorrent] urlTorrent path: " + urlTorrent);
                     }

                 }


                 // Once formatted, add the torrent
                 if (urlTorrent.substring(0, 4).equals("file")) {

                     // File
                     urlTorrent = Uri.decode(URLEncoder.encode(urlTorrent, "UTF-8"));
                     addTorrentFile(Uri.parse(urlTorrent).getPath().replaceAll("\\+", "\\ "));
                 } else {

                     // Send magnet or torrent link
//                    Log.d("Debug", "[handleUrlTorrent] urlTorrent 1: " + urlTorrent );

                     urlTorrent = Uri.decode(URLEncoder.encode(urlTorrent, "UTF-8"));


                     // If It is a valid torrent or magnet link
                     if (urlTorrent.contains(".torrent") || urlTorrent.contains("magnet:") || "application/x-bittorrent".equals(handledIntent.getType())) {
//                        Log.d("Debug", "[handleUrlTorrent] URL: " + urlTorrent);
                         addTorrent(urlTorrent, path2Set, category2Set);
                     } else {
                         // Open not valid torrent or magnet link in browser

                         Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTorrent));
                         startActivity(browserIntent);
                     }

                 }

             } catch (UnsupportedEncodingException e) {
                 Log.e("Debug", "[handleUrlTorrent] Check URL: " + e.toString());
             } catch (NullPointerException e) {
                 Log.e("Debug", "[handleUrlTorrent] urlTorrent is null: " + e.toString());
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (Exception e) {
                 Log.e("Debug", "[handleUrlTorrent] urlTorrent is not ok: " + e.toString());
             }
         }
     }


     private void addTorrentByIntent(Intent intent) {

//        Log.d("Debug", "addTorrentByIntent invoked");

         // TODO: Define a setting for this timeout in preferences

         // Delay sending of file
         final Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 //Do something after 3000ms
                 // Nothing
             }
         }, 3000);

         handledIntent = intent;

         urlTorrent = intent.getDataString();

         if (urlTorrent != null && urlTorrent.length() != 0) {

             // Check dangerous permissions
             checkDangerousPermissions();

         }

         try {
             if (intent.getStringExtra("from").equals("NotifierService")) {
                 saveLastState("completed");
                 setSelectionAndTitle("completed");
                 refresh("completed", currentCategory);
             }
         } catch (NullPointerException npe) {

         }

     }


     private void checkDangerousPermissions() {

         // Check Dangerous permissions (Android 6.0+, API 23+)
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


             // Should we show an explanation?
             if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                 genericOkDialog(R.string.error_permission,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                         MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                             }
                         });

             } else {

                 // No explanation needed, request the permission.
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                         MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

             }


         } else {

             // Permissions granted
             sendTorrent();
//            handleUrlTorrent();

         }


     }


     @Override
     public void onRequestPermissionsResult(int requestCode,
                                            String permissions[], int[] grantResults) {
         switch (requestCode) {
             case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                 // If request is cancelled, the result arrays are empty.
                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                     // Permissions granted
                     sendTorrent();
//                    handleUrlTorrent();


                 } else {

                     // Permission denied


                     // Should we show an explanation?
                     if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                         genericOkCancelDialog(R.string.error_grant_permission,
                                 new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         Intent appIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                         appIntent.setData(Uri.parse("package:" + packageName));
                                         startActivityForResult(appIntent, 0);
                                     }
                                 });
                     }
                     return;
                 }
             }
         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main, menu);

         // Disable RSS support
         menu.findItem(R.id.action_rss).setVisible(false);

         // Retrieve the SearchView and plug it into SearchManager
         final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
         SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


         // Handle open/close SearchView (using an item menu)
         final MenuItem menuItem = menu.findItem(R.id.action_search);

         // When back is pressed or the SearchView is close, delete the query field
         // and close the SearchView using the item menu
         searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean queryTextFocused) {
                 if (!queryTextFocused) {
                     menuItem.collapseActionView();
                     searchView.setQuery("", false);
                     searchField = "";

                     refreshSwipeLayout();
                     refreshCurrent();
                 }
             }
         });

         // This must be implemented to override defaul searchview behaviour, in order to
         // make it work with tablets
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {

                 //Log.d("Debug", "onQueryTextSubmit - searchField: " + query);

                 // false: don't actually send the query. We are going to do something different
                 searchView.setQuery(query, false);

                 // Set the variable we use in the intent to perform the search
                 searchField = query;

                 // Refresh using the search field
                 refreshSwipeLayout();
                 refreshCurrent();

                 // Close the soft keyboard
                 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(menuItem.getActionView().getWindowToken(), 0);

                 // Here true means, override default searchview query
                 return true;
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 return false;
             }
         });

         // There is a bug setting the hint from searchable.xml, so force it
         searchView.setQueryHint(getResources().getString(R.string.search_hint));


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

             case R.id.action_refresh:
                 swipeRefresh();
                 return true;
             case R.id.action_add_file:
                 // Add torrent file
                 openFilePicker();
                 return true;
             case R.id.action_add_url:
                 // Add torrent URL
                 addUrlTorrent();
                 return true;
//            case R.id.action_rss:
//                // Open RSS Activity
//                Intent intent = new Intent(getBaseContext(), com.lgallardo.qbittorrentclient.RSSFeedActivity.class);
//                startActivity(intent);
//                return true;
             case R.id.action_pause:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     pauseTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_resume:
                 if (TorrentDetailsFragment.hashToUpdate != null) {
                     startTorrent(TorrentDetailsFragment.hashToUpdate);
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
             case R.id.action_force_start:
                 if (TorrentDetailsFragment.hashToUpdate != null) {
                     forceStartTorrent(TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_increase_prio:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     increasePrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_decrease_prio:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     decreasePrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_max_prio:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     maxPrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_min_prio:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     minPrioTorrent(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
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
                 }
                 return true;

             case R.id.action_download_rate_limit:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     downloadRateLimitDialog(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_recheck:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     recheckTorrents(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_first_last_piece_prio:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     toggleFirstLastPiecePrio(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_sequential_download:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     toggleSequentialDownload(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_set_category:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     setCategoryDialog(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             case R.id.action_delete_category:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     setCategory(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate, " ");
                 }
                 return true;

             case R.id.action_toggle_alternative_rate:
                 toggleAlternativeSpeedLimits();
                 return true;
             case R.id.action_sortby_name:
                 saveSortBy(SORTBY_NAME);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_size:
                 saveSortBy(SORTBY_SIZE);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_eta:
                 saveSortBy(SORTBY_ETA);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_priority:
                 saveSortBy(SORTBY_PRIORITY);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_progress:
                 saveSortBy(SORTBY_PROGRESS);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_ratio:
                 saveSortBy(SORTBY_RATIO);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_downloadSpeed:
                 saveSortBy(SORTBY_DOWNLOAD);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_uploadSpeed:
                 saveSortBy(SORTBY_UPLOAD);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_added_on:
                 saveSortBy(SORTBY_ADDEDON);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_completed_on:
                 saveSortBy(SORTBY_COMPLETEDON);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_sortby_reverse_order:
                 saveReverseOrder(!reverse_order);
                 invalidateOptionsMenu();
                 swipeRefresh();
                 return true;
             case R.id.action_add_tracker:
                 if (com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate != null) {
                     addUrlTracker(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate);
                 }
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }
     }

     //Change current server
     protected void changeCurrentServer() {

         connection403ErrorCounter = 0;

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
         //refreshSwipeLayout();

         refresh();

//        refreshCurrent();

//        // Get new token and cookie
//        MainActivity.cookie = null;
//
//        // Get API;
//        getApi();


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

             Gson gson = new Gson();

             String up_limit = Integer.toString(Integer.parseInt(global_upload) * 1024);
             String down_limit = Integer.toString(Integer.parseInt(global_download) * 1024);

             // TODO: Check when this changed (qb_api X.Y.Z )
             if (qb_api > 0) {
                 alt_upload = Integer.toString(Integer.parseInt(alt_upload) * 1024);
                 alt_download = Integer.toString(Integer.parseInt(alt_download) * 1024);
             }


             Options options = new Options(global_max_num_connections, global_max_num_connections,
                     max_num_conn_per_torrent, max_num_conn_per_torrent, max_uploads,
                     max_num_upslots_per_torrent, max_num_upslots_per_torrent,
                     global_upload, up_limit, global_download, down_limit,
                     alt_upload, alt_download, torrent_queueing, max_act_downloads,
                     max_act_uploads, max_act_torrents, schedule_alternative_rate_limits,
                     alt_from_hour, alt_from_min, alt_to_hour, alt_to_min, scheduler_days);

             json = gson.toJson(options).toString();

             // Set preferences using this json object
             setQBittorrentPrefefrences(json);

             // Now it can be refreshed
             canrefresh = true;

         }

         if (requestCode == HELP_CODE && resultCode == RESULT_OK) {
             // Now it can be refreshed
             canrefresh = true;

             refreshSwipeLayout();
             refreshCurrent();
         }


         if (requestCode == SETTINGS_CODE && resultCode == RESULT_OK) {

             // Change current server (from settings or drawer menu)
             changeCurrentServer();

             refreshSwipeLayout();
             refreshCurrent();

         }

         if (requestCode == ADDFILE_CODE && resultCode == RESULT_OK) {

             String file_path_value = "";

             // MaterialDesignPicker
             if (requestCode == ADDFILE_CODE && resultCode == RESULT_OK) {

                 urlTorrent = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                 sendTorrent();

             }

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

             // Okv
             builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     // User accepted the dialog
                     urlTorrent = urlInput.getText().toString();
                     sendTorrent();

                 }
             });

             // Create dialog
             AlertDialog dialog = builder.create();

             // Show dialog
             dialog.show();
         }

     }

     public void addUrlTracker(final String hash) {

         // get prompts.xml view
         LayoutInflater li = LayoutInflater.from(MainActivity.this);
         View addTrackerView = li.inflate(R.layout.add_tracker, null);

         // URL input
         final EditText urlInput = (EditText) addTrackerView.findViewById(R.id.url);

         if (!isFinishing()) {
             // Dialog
             Builder builder = new Builder(MainActivity.this);

             // Set add_torrent.xml to AlertDialog builder
             builder.setView(addTrackerView);

             // Cancel
             builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     // User cancelled the dialog
//                    Log.d("Debug", "addUrlTracker - Cancelled");
                 }
             });

             // Ok
             builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     // User accepted the dialog
//                    Log.d("Debug", "addUrlTracker - Adding tracker");
                     addTracker(hash, urlInput.getText().toString());
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

     // This get qBittorrent options to save them in shared preferences variables and then open the Option activity
     protected void getAndOpenOptions() {

         // Options - Execute the task in background
         toastText(R.string.getQBittorrentPrefefrences);

//        qBittorrentOptions qso = new qBittorrentOptions();
//        qso.execute(new String[]{qbQueryString + "/preferences", "setOptions"});

     }

     protected void getPRO() {
         Intent intent = new Intent(
                 new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lgallardo.qbittorrentclientpro")));
         startActivityForResult(intent, GETPRO_CODE);
     }

     public void addTorrentFile(String url) {

         addTorrentFileAPI7(url, path2Set, category2Set);

//      // TODO: Check when this changed (qb_api X.Y.Z )
//        if (Integer.parseInt(qb_api) >= 7) {
//            addTorrentFileAPI7(url, path2Set, category2Set);
//        } else {
//            addTorrentFile(url, path2Set, category2Set);
//            // Execute the task in background
//            qBittorrentCommand qtc = new qBittorrentCommand();
//            qtc.execute(new String[]{"addTorrentFile", url, path2Set, category2Set});
//
//        }
     }

     public void recheckTorrents(String hashes) {
         // Execute the task in background

         String[] hashesArray = hashes.split("\\|");

         for (int i = 0; hashesArray.length > i; i++) {
             recheckTorrent(hashesArray[i]);
         }

         toastText(R.string.torrentsRecheck);

         // Delay of 3 seconds
         refreshAfterCommand(3);
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

     public void setCategoryDialog(final String hash) {

         // get prompts.xml view
         LayoutInflater li = LayoutInflater.from(MainActivity.this);
         View view = li.inflate(R.layout.set_category, null);

         // URL input
         final EditText category = (EditText) view.findViewById(R.id.set_category);

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

                     String categoryEncoded = Uri.encode(category.getText().toString());

                     setCategory(hash, categoryEncoded);
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
                     setUpRateLimit(hashesArray[i], "" + limit * 1024);
                 }

                 toastText(R.string.setUploadRateLimit);

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
                     setDownRateLimit(hashesArray[i], "" + limit * 1024);
                 }

                 toastText(R.string.setDownloadRateLimit);

                 // Delay of 1 second
                 refreshAfterCommand(1);

             } else {
                 genericOkDialog(R.string.error, R.string.global_value_error);
             }
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

     public void refreshAfterCommand(int delay) {

//        switch (drawerList.getCheckedItemPosition()) {
         switch (actionStates.indexOf(currentState)) {
             case 0:
                 refreshWithDelay("all", currentCategory, delay);
                 break;
             case 1:
                 refreshWithDelay("downloading", currentCategory, delay);
                 break;
             case 2:
                 refreshWithDelay("completed", currentCategory, delay);
                 break;
             case 3:
                 refreshWithDelay("seeding", currentCategory, delay);
                 break;
             case 4:
                 refreshWithDelay("pause", currentCategory, delay);
                 break;
             case 5:
                 refreshWithDelay("active", currentCategory, delay);
                 break;
             case 6:
                 refreshWithDelay("inactive", currentCategory, delay);
                 break;
             case 7:
                 break;
             case 8:
                 break;
             default:
                 refreshWithDelay("all", currentCategory, delay);
                 break;
         }

     }

     public void genericOkDialog(int title, int message) {
         genericOkDialog(title, message, null);
     }

     public void genericOkDialog(int message, DialogInterface.OnClickListener okListener) {
         genericOkDialog(-1, message, okListener);
     }

     public void genericOkDialog(int message) {
         genericOkDialog(-1, message, null);
     }

     public void genericOkDialog(int title, int message, DialogInterface.
             OnClickListener okListener) {

         if (!isFinishing()) {

             Builder builder = new Builder(this);

             // Title
             if (title != -1) {
                 builder.setTitle(title);
             }

             // Message
             builder.setMessage(message);

             // Ok
             builder.setPositiveButton(R.string.ok, okListener);

             // Create dialog
             AlertDialog dialog = builder.create();

             // Show dialog
             dialog.show();
         }

     }

     private void genericOkCancelDialog(int title, int message) {

         genericOkCancelDialog(title, message, null);
     }

     private void genericOkCancelDialog(int message, DialogInterface.
             OnClickListener okListener) {

         genericOkCancelDialog(-1, message, okListener);

     }

     private void genericOkCancelDialog(int message) {

         genericOkCancelDialog(-1, message, null);

     }

     private void genericOkCancelDialog(int title, int message, DialogInterface.
             OnClickListener okListener) {

         if (!isFinishing()) {

             Builder builder = new Builder(this);

             // Title
             if (title != -1) {
                 builder.setTitle(title);
             }

             // Message
             builder.setMessage(message);

             // Ok
             builder.setPositiveButton(R.string.ok, okListener);

             // Cancel
             builder.setNegativeButton(R.string.cancel, null);

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
             builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

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
     public void refreshWithDelay(final String state, final String category, int seconds) {

         seconds *= 1000;

         final Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 // Do something after 5s = 5000ms
                 refresh(state, category);
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
             port = Integer.parseInt(sharedPrefs.getString("port", "-1"));
         } catch (NumberFormatException e) {
             port = -1;
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

         sortby_value = sharedPrefs.getInt("sortby_value", 1);
         reverse_order = sharedPrefs.getBoolean("reverse_order", false);

         dark_ui = sharedPrefs.getBoolean("dark_ui", false);

         qb_version = sharedPrefs.getString("qb_version", "4.2.x");

         MainActivity.cookie = sharedPrefs.getString("qbCookie", null);

         // Get last state
         lastState = sharedPrefs.getString("lastState", "all");

         // Get last category
         lastCategory = sharedPrefs.getString("lastCategory", getResources().getString(R.string.drawer_category_all));
         currentCategory = lastCategory;


         // Notification check
         enable_notifications = sharedPrefs.getBoolean("enable_notifications", false);

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
         packageVersion = pInfo.versionName;

         // Get AlternativeSpeedLimitsEnabled value
         alternative_speeds = sharedPrefs.getBoolean("alternativeSpeedLimitsEnabled", false);

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
             WifiManager wifiMgr = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
             WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
             String wifiSSID = wifiInfo.getSSID();

//            Log.d("Debug", "WiFi SSID: " + wifiSSID);
//            Log.d("Debug", "SSID: " + ssid);

             if (wifiSSID.toUpperCase().equals("\"" + ssid.toUpperCase() + "\"") && wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                 if (local_hostname != null && !local_hostname.equals("")) {
                     hostname = local_hostname;
                 }

                 if (local_port != -1) {
                     port = local_port;
                 }

//                Log.d("Debug", "hostname: " + hostname);
//                Log.d("Debug", "port: " + port);
//                Log.d("Debug", "local_hostname: " + local_hostname);
//                Log.d("Debug", "local_port: " + local_port);

             }
         }

         // Get keystore for self-signed certificate
         keystore_path = sharedPrefs.getString("keystore_path" + currentServer, "");
         keystore_password = sharedPrefs.getString("keystore_password" + currentServer, "");

         // Get path and category history
         path_history = sharedPrefs.getStringSet("path_history", new HashSet<String>());
         category_history = sharedPrefs.getStringSet("category_history", new HashSet<String>());

         pathAndCategoryDialog = sharedPrefs.getBoolean("pathAndCategoryDialog", true);

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
         max_uploads = sharedPrefs.getString("max_uploads", "0");
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

         // Check alternatives speed
         alternative_speeds = sharedPrefs.getBoolean("alternativeSpeedLimitsEnabled", false);

         // Share Ratio Limiting
         max_ratio_enabled = sharedPrefs.getBoolean("max_ratio_enabled", false);
         max_ratio = sharedPrefs.getString("max_ratio", "0");
         max_ratio_act = sharedPrefs.getString("max_ratio_act", "NULL");

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
         savePreferenceAsString("lastState", state);
     }

     public void saveLastCategory(String category) {
         currentCategory = category;
         savePreferenceAsString("lastCategory", category);
     }

     private void saveSortBy(int sortby_value) {
         MainActivity.sortby_value = sortby_value;
         savePreferenceAsInt("sortby_value", sortby_value);
     }

     // Save key-value preference as String
     private void savePreferenceAsString(String preference, String value) {

         // Save preference locally
         sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         Editor editor = sharedPrefs.edit();

         // Save key-values
         editor.putString(preference, value);

         // Commit changes
         editor.apply();

     }

     // Save key-value preference as a String Set
     private void savePreferenceAsStringSet(String preference, Set<String> value) {

         // Save preference locally
         sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         Editor editor = sharedPrefs.edit();

         // Save key-values
         editor.putStringSet(preference, value);

         // Commit changes
         editor.apply();

     }

     // Save key-value preference as Int
     private void savePreferenceAsInt(String preference, int value) {

         // Save preference locally
         sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         Editor editor = sharedPrefs.edit();

         // Save key-values
         editor.putInt(preference, value);

         // Commit changes
         editor.apply();

     }


     // Save key-value preference as boolean
     private void savePreferenceAsBoolean(String preference, boolean value) {

         // Save preference locally
         sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         Editor editor = sharedPrefs.edit();

         // Save key-values
         editor.putBoolean(preference, value);

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

     private void openFilePicker() {

         // Check Dangerous permissions (Android 6.0+, API 23+)
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


             // Should we show an explanation?
             if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                 genericOkDialog(R.string.error_permission2,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                         MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                             }
                         });

             } else {

                 // No explanation needed, request the permission.
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                         MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

             }


         } else {

             // Permissions granted, open file picker
             Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
             intent.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(".*\\.torrent"));
//            startActivityForResult(INTENT, RESULT_CODE);
             startActivityForResult(intent, ADDFILE_CODE);

         }


     }

     public void sendTorrent() {
         // get prompts.xml view
         LayoutInflater li = LayoutInflater.from(this);
         View sentTorrentView = li.inflate(R.layout.send_torrent, null);

         MainActivity.path2Set = "";
         MainActivity.category2Set = "";

//        Log.d("Debug", "qb_version: " + qb_version);
//        Log.d("Debug", "qb_api: " + qb_api);
//        Log.d("Debug", "type: " + type);

         if (pathAndCategoryDialog) {

             // Variables

             final AutoCompleteTextView pathTextView = (AutoCompleteTextView) sentTorrentView.findViewById(R.id.path_sent);
             final AutoCompleteTextView categoryTextView = (AutoCompleteTextView) sentTorrentView.findViewById(R.id.category_sent);
             final CheckBox checkBoxPathAndCategoryDialog = (CheckBox) sentTorrentView.findViewById(R.id.pathAndCategoryDialog);


             // Load history for path and category autocomplete text field

             // Path
             ArrayAdapter<String> pathAdapter = new ArrayAdapter<String>(
                     this, android.R.layout.simple_list_item_1, path_history.toArray(new String[path_history.size()]));
             pathTextView.setAdapter(pathAdapter);

             // Category
             ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                     this, android.R.layout.simple_list_item_1, category_history.toArray(new String[category_history.size()]));
             categoryTextView.setAdapter(categoryAdapter);

             // Checkbox value
             if (pathAndCategoryDialog) {
                 checkBoxPathAndCategoryDialog.setChecked(false);
             } else {
                 checkBoxPathAndCategoryDialog.setChecked(true);
             }

             // Dialog
             if (!isFinishing()) {
                 // Dialog
                 AlertDialog.Builder builder = new AlertDialog.Builder(this);

                 // Set send_torrent.xml to AlertDialog builder
                 builder.setView(sentTorrentView);

                 // Cancel
                 builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         // User cancelled the dialog
                     }
                 });

                 // Ok
                 builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {

                         MainActivity.path2Set = pathTextView.getText().toString();
                         MainActivity.category2Set = categoryTextView.getText().toString();

                         if (!(path2Set.equals(""))) {
                             addPath2History(path2Set);
                         }

                         if (!(category2Set.equals(""))) {
                             addCategory2History(category2Set);
                         }


//                        Log.d("Debug", "[sendTorrent] path2Set: " + path2Set);
//                        Log.d("Debug", "[sendTorrent] category2Set: " + category2Set);

                         // Save checkbox
                         savePreferenceAsBoolean("pathAndCategoryDialog", !(checkBoxPathAndCategoryDialog.isChecked()));

                         // User accepted
                         handleUrlTorrent();
                     }
                 });

                 // Create dialog
                 AlertDialog dialog = builder.create();

                 // Show dialog
                 dialog.show();
             }

         } else {

             // No dialog if it's disabled
             handleUrlTorrent();
         }

     }

     private void addPath2History(String path) {

         if (!path_history.contains(path)) {
             path_history.add(path);
             savePreferenceAsStringSet("path_history", path_history);
         }
     }

     private void addCategory2History(String category) {

         if (!category_history.contains(category)) {
             category_history.add(category);
             savePreferenceAsStringSet("category_history", category_history);
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

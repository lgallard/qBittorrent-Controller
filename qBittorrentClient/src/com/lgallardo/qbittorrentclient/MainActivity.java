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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends FragmentActivity {

	// Params to get JSON Array
	private static String[] params = new String[4];

	// JSON Node Names
	protected static final String TAG_USER = "user";
	protected static final String TAG_ID = "id";
	protected static final String TAG_ALTDWLIM = "alt_dl_limit";
	protected static final String TAG_DWLIM = "dl_limit";

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
	protected static final String TAG_SAVE_PATH = "save_path";
	protected static final String TAG_CREATION_DATE = "creation_date";
	protected static final String TAG_COMMENT = "comment";
	protected static final String TAG_TOTAL_WASTED = "total_wasted";
	protected static final String TAG_TOTAL_UPLOADED = "total_uploaded";
	protected static final String TAG_TOTAL_DOWNLOADED = "total_downloaded";
	protected static final String TAG_TIME_ELAPSED = "time_elapsed";
	protected static final String TAG_NB_CONNECTIONS = "nb_connections";
	protected static final String TAG_SHARE_RATIO = "share_ratio";
	protected static final String TAG_UPLOAD_LIMIT = "up_limit";
	protected static final String TAG_DOWNLOAD_LIMIT = "dl_limit";

	protected static final String TAG_INFO = "info";

	protected static final String TAG_ACTION = "action";
	protected static final String TAG_START = "start";
	protected static final String TAG_PAUSE = "pause";
	protected static final String TAG_DELETE = "delete";
	protected static final String TAG_DELETE_DRIVE = "deleteDrive";

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
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private CharSequence drawerTitle;
	private CharSequence title;
	// For app icon control for navigation drawer, add new property on
	// MainActivity
	private ActionBarDrawerToggle drawerToggle;

	private ItemstFragment firstFragment;
	private AboutFragment secondFragment;
	private HelpFragment helpTabletFragment;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Set App title
		setTitle(R.string.app_shortname);

		// Drawer menu
		navigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList = (ListView) findViewById(R.id.left_drawer);

		// Drawer item list objects
		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[8];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_drawer_all, navigationDrawerItemTitles[0]);
		drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_drawer_downloading, navigationDrawerItemTitles[1]);
		drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_drawer_completed, navigationDrawerItemTitles[2]);
		drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_drawer_paused, navigationDrawerItemTitles[3]);
		drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_drawer_active, navigationDrawerItemTitles[4]);
		drawerItem[5] = new ObjectDrawerItem(R.drawable.ic_drawer_inactive, navigationDrawerItemTitles[5]);
		drawerItem[6] = new ObjectDrawerItem(R.drawable.ic_action_options, navigationDrawerItemTitles[6]);
		drawerItem[7] = new ObjectDrawerItem(R.drawable.ic_drawer_settings, navigationDrawerItemTitles[7]);

		// Create object for drawer item OnbjectDrawerItem
		DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
		drawerList.setAdapter(adapter);

		// Set the item click listener
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Get drawer title
		title = drawerTitle = getTitle();

		// Add the application icon control code inside MainActivity onCreate

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(title);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(drawerTitle);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Get preferences
		getSettings();

		// Get options and save them as shared preferences
		qBittorrentOptions qso = new qBittorrentOptions();
		qso.execute(new String[] { "json/preferences", "getSettings" });

		// If it were awaked from an intent-filter,
		// get intent from the intent filter and Add URL torrent
		Intent intent = getIntent();
		String urlTorrent = intent.getDataString();

		if (urlTorrent != null && urlTorrent.length() != 0) {

			// File
			if (urlTorrent.substring(0, 4).equals("file")) {
				// File
				addTorrentFile(Uri.parse(urlTorrent).getPath());
			} else {
				// URL
				addTorrent(intent.getDataString());
			}
		}

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
			firstFragment = new ItemstFragment();

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

			fragmentTransaction.add(R.id.list_frame, helpTabletFragment);
			fragmentTransaction.add(R.id.content_frame, secondFragment);
			// .addToBackStack("secondFragment");

			fragmentTransaction.commit();

			// Second fragment will be added in ItemsFRagment's onListItemClick
			// method

		} else {

			// Phones handle just one fragment

			// Create an instance of ItemsFragments
			firstFragment = new ItemstFragment();

			// This i the about fragment, holding a default message at the
			// beginning

			secondFragment = new AboutFragment();

			firstFragment.setSecondFragmentContainer(R.id.one_frame);

			// Add the fragment to the 'list_frame' FrameLayout
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			fragmentTransaction.add(R.id.one_frame, secondFragment);

			fragmentTransaction.commit();
		}

		// Autorefresh

		handler = new Handler();
		handler.postDelayed(m_Runnable, refresh_period);

	}

	@Override
	public void onResume() {
		super.onResume();
		activityIsVisible = true;

		refreshCurrent();
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

	// MainActivity old methods

	// // Rotation handling
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// }

	@Override
	public void onBackPressed() {

		if (getFragmentManager().getBackStackEntryCount() == 0) {
			this.finish();
		} else {
			getFragmentManager().popBackStack();
		}
	}

	private void refresh() {

		refresh("all", false);

	}

	private void refresh(String state) {

		refresh(state, false);

	}

	private void refresh(String state, boolean clear) {

		if (oldVersion == true) {
			params[0] = "json/events";
		} else {
			params[0] = "json/torrents";
		}

		params[1] = state;

		if (clear) {
			params[2] = "clear";
		} else {
			params[2] = "";
		}

		params[3] = "/json/propertiesGeneral/";

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected() && !networkInfo.isFailover()) {

			// Execute the task in background
			qBittorrentTask qtt = new qBittorrentTask();

			qtt.execute(params);

			// Load banner
			loadBanner();

			// If activity is visible, Connecting message
			if (activityIsVisible) {
				// Connecting message
				Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
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

			return true;
		case R.id.action_delete_drive:

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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SETTINGS_CODE) {

			// Get options from server and save them as shared preferences
			// locally
			qBittorrentOptions qso = new qBittorrentOptions();
			qso.execute(new String[] { "json/preferences", "getSettings" });

			// Select "All" torrents list
			selectItem(0);

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

	}

	private void addUrlTorrent() {

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View addTorrentView = li.inflate(R.layout.add_torrent, null);

		// URL input
		final EditText urlInput = (EditText) addTorrentView.findViewById(R.id.url);

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

	private void openSettings() {
		canrefresh = false;
		Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		// startActivity(intent);
		startActivityForResult(intent, SETTINGS_CODE);

	}

	private void openOptions() {
		canrefresh = false;
		// Retrieve preferences for options
		Intent intent = new Intent(getBaseContext(), OptionsActivity.class);
		startActivityForResult(intent, OPTION_CODE);

	}

	public void startTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "start", hash });

	}

	public void pauseTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "pause", hash });
	}

	public void deleteTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "delete", hash });
	}

	public void deleteDriveTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "deleteDrive", hash });
	}

	public void addTorrent(String url) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "addTorrent", url });
	}

	public void addTorrentFile(String url) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "addTorrentFile", url });
	}

	public void pauseAllTorrents() {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "pauseAll", null });
	}

	public void resumeAllTorrents() {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "resumeAll", null });
	}

	public void increasePrioTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "increasePrio", hash });

	}

	public void decreasePrioTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "decreasePrio", hash });

	}

	public void setQBittorrentPrefefrences(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "setQBittorrentPrefefrences", hash });

	}

	public void uploadRateLimitDialog(final String hash) {

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View view = li.inflate(R.layout.upload_rate_limit, null);

		// URL input
		final EditText uploadRateLimit = (EditText) view.findViewById(R.id.upload_rate_limit);

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

	public void downloadRateLimitDialog(final String hash) {

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View view = li.inflate(R.layout.download_rate_limit, null);

		// URL input
		final EditText downloadRateLimit = (EditText) view.findViewById(R.id.download_rate_limit);

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

	public void setUploadRateLimit(String hash, String uploadRateLimit) {
		int limit;

		if (uploadRateLimit != null && !uploadRateLimit.equals("")) {

			if (global_upload != null) {

				limit = (Integer.parseInt(uploadRateLimit) > Integer.parseInt(global_upload) && Integer.parseInt(global_upload) != 0) ? Integer
						.parseInt(global_upload) : Integer.parseInt(uploadRateLimit);

				qBittorrentCommand qtc = new qBittorrentCommand();
				qtc.execute(new String[] { "setUploadRateLimit", hash + "&" + limit * 1024 });

			} else {
				genericOkDialog(R.string.error, R.string.global_value_error);

			}
		}

	}

	public void setDownloadRateLimit(String hash, String downloadRateLimit) {

		int limit;

		if (downloadRateLimit != null && !downloadRateLimit.equals("")) {

			if (global_download != null) {

				limit = (Integer.parseInt(downloadRateLimit) > Integer.parseInt(global_download)) ? Integer.parseInt(global_download) : Integer
						.parseInt(downloadRateLimit);

				qBittorrentCommand qtc = new qBittorrentCommand();
				qtc.execute(new String[] { "setDownloadRateLimit", hash + "&" + limit * 1024 });
			} else {
				genericOkDialog(R.string.error, R.string.global_value_error);
			}

		}

	}

	public void genericOkDialog(int title, int message) {

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

	// Delay method
	public void refreshWithDelay(final String state, int seconds) {

		seconds *= 1000;

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Do something after 5s = 5000ms
				refresh(state, true);
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
		hostname = sharedPrefs.getString("hostname", "NULL");
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
		connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "5"));
		data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "8"));

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

	// Here is where the action happens
	private class qBittorrentCommand extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			// Get values from preferences
			getSettings();

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

			jParser.postCommand(params[0], params[1]);

			return params[0];

		}

		@Override
		protected void onPostExecute(String result) {

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
				messageId = R.string.torrentDeleled;
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

			Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_SHORT).show();

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
				// Select "All" torrents list
				selectItem(0);
				break;
			case 7:
				break;
			default:
				refreshWithDelay("all", delay);
				break;
			}

		}
	}

	// Here is where the action happens
	private class qBittorrentTask extends AsyncTask<String, Integer, Torrent[]> {

		@Override
		protected Torrent[] doInBackground(String... params) {

			String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, downloaded, eta, uploadSpeed, downloadSpeed;

			Torrent[] torrents = null;

			// Get settings
			getSettings();

			try {
				// Creating new JSON Parser
				jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

				JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);

				if (jArray != null) {

					Log.i("qbTask", "jArray length: " + jArray.length());

					torrents = new Torrent[jArray.length()];

					MainActivity.names = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						name = json.getString(TAG_NAME);
						size = json.getString(TAG_SIZE);
						progress = String.format("%.2f", json.getDouble(TAG_PROGRESS) * 100) + "%";
						info = "";
						state = json.getString(TAG_STATE);
						hash = json.getString(TAG_HASH);
						ratio = json.getString(TAG_RATIO);
						leechs = json.getString(TAG_NUMLEECHS);
						seeds = json.getString(TAG_NUMSEEDS);
						priority = json.getString(TAG_PRIORITY);
						eta = json.getString(TAG_ETA);
						downloadSpeed = json.getString(TAG_DLSPEED);
						uploadSpeed = json.getString(TAG_UPSPEED);

						torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, leechs, seeds, priority, eta, downloadSpeed, uploadSpeed);

						MainActivity.names[i] = name;

						// Get torrent generic properties

						try {
							// Calculate total downloaded
							Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
							String sizeUnit = size.substring(size.indexOf(" "), size.length());

							torrents[i].setDownloaded(String.format("%.2f", sizeScalar * json.getDouble(TAG_PROGRESS)) + sizeUnit);
						} catch (Exception e) {
							torrents[i].setDownloaded(size);
						}

						// Info
						torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
								+ Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
								+ torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + progress + " " + Character.toString('\u2022') + " "
								+ torrents[i].getEta());

					}
					Log.i("qbTask", "jArray length: " + jArray.length());
				} else {
					Log.i("qbTask", "jArray is null");
				}
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

			} else {

				ArrayList<Torrent> torrentsFiltered = new ArrayList<Torrent>();

				Log.i("qbTask", "Results (torrents): " + result.length);

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

				// MainActivity.lines = (torrent[]) torrentsFiltered.toArray();

				// Get names (delete in background method)
				MainActivity.names = new String[torrentsFiltered.size()];
				MainActivity.lines = new Torrent[torrentsFiltered.size()];

				Log.i("qbTask", "MainActivity.names: " + MainActivity.names.length);
				Log.i("qbTask", "MainActivity.lines: " + MainActivity.names.length);

				try {

					for (int i = 0; i < torrentsFiltered.size(); i++) {

						Torrent torrent = torrentsFiltered.get(i);

						MainActivity.names[i] = torrent.getFile();
						MainActivity.lines[i] = torrent;
					}

					if (findViewById(R.id.one_frame) != null) {
						getFragmentManager().popBackStack();
					}

					firstFragment.setListAdapter(new myAdapter(MainActivity.this,names,lines));

					// Create the about fragment
					AboutFragment aboutFragment = new AboutFragment();

					// Got some results
					if (torrentsFiltered.size() > 0) {

						// Add the fragment to the 'list_frame' FrameLayout
						FragmentManager fragmentManager = getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

						// Set the second fragments container
						if (findViewById(R.id.fragment_container) != null) {
							firstFragment.setSecondFragmentContainer(R.id.content_frame);
							fragmentTransaction.replace(R.id.list_frame, firstFragment);

						} else {
							firstFragment.setSecondFragmentContainer(R.id.one_frame);
							fragmentTransaction.replace(R.id.one_frame, firstFragment);
						}

						fragmentTransaction.commit();

						ListView lv = firstFragment.getListView();

						lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

						// Also update the second fragment (if it comes from the
						// drawer)
						if (params[2].equals("clear") && lv.getCount() > 0) {

							// Scroll to the first position
							lv.smoothScrollToPosition(0);

							// Notify there isn't any item selected
							// firstFragment.setSelection(-1);

							if (findViewById(R.id.fragment_container) != null) {

								// Reset the BackStack (Back button)
								fragmentManager = getFragmentManager();

								for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
									getFragmentManager().popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
								}

								// Replace with the about fragment
								fragmentManager.beginTransaction().replace(R.id.content_frame, aboutFragment).commit();

							} else {

								// Just one fragment
								// Reset the BackStack (Back button)
								fragmentManager = getFragmentManager();
								for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
									fragmentManager.popBackStack();
								}

								// Replace with the about fragment
								fragmentManager.beginTransaction().replace(R.id.one_frame, firstFragment, "firstFragment").commit();
							}

						}

						if (params[2].equals("") && lv.getCount() > 0) {

							// Scroll listView to the first position
							lv.smoothScrollToPosition(0);

							if (aboutFragment != null) {

								if (findViewById(R.id.fragment_container) != null) {

									// Reset the BackStack (Back button)
									fragmentManager = getFragmentManager();

									for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
										getFragmentManager().popBackStack("secondFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
									}

									fragmentManager = getFragmentManager();
									fragmentManager.beginTransaction().replace(R.id.content_frame, aboutFragment).commit();

								}

								else {

									fragmentManager = getFragmentManager();

									fragmentManager.beginTransaction().replace(R.id.one_frame, firstFragment, "firstFragment").commit();

								}
							}

						}

					} else {

						// No results
						String[] emptyList = new String[] { getString(R.string.no_results) };
						firstFragment.setListAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.no_items_found, R.id.no_results, emptyList));

						// Add the fragment to the 'list_frame' FrameLayout
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

						// Set the second fragments container
						if (findViewById(R.id.fragment_container) != null) {
							firstFragment.setSecondFragmentContainer(R.id.content_frame);
							fragmentTransaction.replace(R.id.list_frame, firstFragment);
							fragmentTransaction.replace(R.id.content_frame, aboutFragment);
							fragmentTransaction.commit();

						} else {
							firstFragment.setSecondFragmentContainer(R.id.one_frame);
							fragmentTransaction.replace(R.id.one_frame, firstFragment);
							fragmentTransaction.commit();
						}

					}

				}
				// catch(IllegalStateException le){
				//
				// throw le;
				// }

				catch (Exception e) {
					// TODO: handle exception
					Log.e("ADAPTER", e.toString());
				}

				// Clear serch field

				searchField = "";

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

			//
			JSONObject json = jParser.getJSONFromUrl(params[0]);

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

	class myAdapter extends ArrayAdapter<String> {
		private String[] torrentsNames;
		private Torrent[] torrentsData;
		private Context context;

		public myAdapter(Context context, String[] torrentsNames, Torrent[] torrentsData) {
			// TODO Auto-generated constructor stub
			super(context, R.layout.row, R.id.file, torrentsNames);

			this.context = context;
			this.torrentsNames = torrentsNames;
			this.torrentsData = torrentsData;

			// Log.i("myAdapter", "lines: " + lines.length);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub}

			Log.i("qbTask", "getCount: " + ((torrentsNames != null) ? torrentsNames.length : 0));

			return (torrentsNames != null) ? torrentsNames.length : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			String state = torrentsData[position].getState();

			TextView info = (TextView) row.findViewById(R.id.info);

			info.setText("" + torrentsData[position].getInfo());

			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
				icon.setImageResource(R.drawable.paused);
			}

			if ("stalledUP".equals(state)) {
				icon.setImageResource(R.drawable.stalledup);
			}

			if ("stalledDL".equals(state)) {
				icon.setImageResource(R.drawable.stalleddl);
			}

			if ("downloading".equals(state)) {
				icon.setImageResource(R.drawable.downloading);
			}

			if ("uploading".equals(state)) {
				icon.setImageResource(R.drawable.uploading);
			}

			if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
				icon.setImageResource(R.drawable.queued);
			}

			return (row);
		}
	}

	// Drawer classes

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}

	}

	private void selectItem(int position) {

		// Fragment fragment = null;

		switch (position) {
		case 0:
			refresh("all", true);
			break;
		case 1:
			refresh("downloading", true);
			break;
		case 2:
			refresh("completed", true);
			break;
		case 3:
			refresh("paused", true);
			break;
		case 4:
			refresh("active", true);
			break;
		case 5:
			refresh("inactive", true);
			break;
		case 6:
			// Options - Execute the task in background
			Toast.makeText(getApplicationContext(), R.string.getQBittorrentPrefefrences, Toast.LENGTH_SHORT).show();
			qBittorrentOptions qso = new qBittorrentOptions();
			qso.execute(new String[] { "json/preferences", "setOptions" });
			break;
		case 7:
			// Settings
			openSettings();
			break;
		default:
			break;
		}

		// if (fragment != null || listFragment != null || contentFragment !=
		// null) {
		// // FragmentManager fragmentManager = getFragmentManager();
		// // fragmentManager.beginTransaction()
		// // .replace(R.id.content_frame, fragment).commit();

		drawerList.setItemChecked(position, true);
		drawerList.setSelection(position);
		setTitle(navigationDrawerItemTitles[position]);
		drawerLayout.closeDrawer(drawerList);

		// } else {
		// Log.e("MainActivity", "Error in creating fragment");
		// }
	}
}

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	protected static final String TAG_SAVE_PATH = "save_path";
	protected static final String TAG_CREATION_DATE = "creation_date";
	protected static final String TAG_COMMENT = "comment";
	protected static final String TAG_TOTAL_WASTED = "total_wasted";
	protected static final String TAG_TOTAL_UPLOADED = "total_uploaded";
	protected static final String TAG_TOTAL_DOWNLOADED = "total_downloaded";
	protected static final String TAG_TIME_ELAPSED = "time_elapsed";
	protected static final String TAG_NB_CONNECTIONS = "nb_connections";
	protected static final String TAG_SHARE_RATIO = "share_ratio";

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

	protected static final int SETTINGS_CODE = 0;
	protected static final int OPTION_CODE = 1;

	// Preferences properties
	protected static String hostname;
	protected static int port;
	protected static String protocol;
	protected static String username;
	protected static String password;
	protected static boolean oldVersion;
	protected static boolean https;

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

	protected static String NO_RESULTS = "No torrents found";

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Drawer menu
		navigationDrawerItemTitles = getResources().getStringArray(
				R.array.navigation_drawer_items_array);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList = (ListView) findViewById(R.id.left_drawer);

		// Drawer item list objects
		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[8];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_drawer_all, "All");
		drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_drawer_downloading,
				"Downloading");
		drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_drawer_completed,
				"Completed");
		drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_drawer_paused,
				"Paused");
		drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_drawer_active,
				"Active");
		drawerItem[5] = new ObjectDrawerItem(R.drawable.ic_drawer_inactive,
				"Inactive");
		drawerItem[6] = new ObjectDrawerItem(R.drawable.ic_action_options,
				"Options");
		drawerItem[7] = new ObjectDrawerItem(R.drawable.ic_drawer_settings,
				"Settings");

		// Create object for drawer item OnbjectDrawerItem
		DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this,
				R.layout.listview_item_row, drawerItem);
		drawerList.setAdapter(adapter);

		// Set the item click listener
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Get drawer title
		title = drawerTitle = getTitle();

		// Add the application icon control code inside MainActivity onCreate

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

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

		// If it were awaked from an intent-filter,
		// get intent from the intent filter and Add URL torrent
		Intent intent = getIntent();
		String urlTorrent = intent.getDataString();

		if (urlTorrent != null && urlTorrent.length() != 0) {
			addTorrent(intent.getDataString());
		}

		// Fragments

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

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
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

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
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			fragmentTransaction.add(R.id.one_frame, secondFragment);

			fragmentTransaction.commit();
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
			Log.i("onBackPressed", "getBackStackEntryCount: "
					+ getFragmentManager().getBackStackEntryCount());
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

		if (networkInfo != null && networkInfo.isConnected()
				&& !networkInfo.isFailover()) {

			// Execute the task in background
			qBittorrentTask qtt = new qBittorrentTask();

			qtt.execute(params);

			// Connecting message
			Toast.makeText(getApplicationContext(), R.string.connecting,
					Toast.LENGTH_SHORT).show();

		} else {

			// Connection Error message
			Toast.makeText(getApplicationContext(), R.string.connection_error,
					Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public TorrentDetailsFragment getTorrentDetailsFragment() {

		TorrentDetailsFragment tf = null;

		if (findViewById(R.id.fragment_container) != null) {
			tf = (TorrentDetailsFragment) getFragmentManager()
					.findFragmentById(R.id.content_frame);
		} else {

			if (getFragmentManager().findFragmentById(R.id.one_frame) instanceof TorrentDetailsFragment) {

				tf = (TorrentDetailsFragment) getFragmentManager()
						.findFragmentById(R.id.one_frame);
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

			} else {
				Log.i("pase", "tf is null :(");
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
			builder.setMessage(R.string.dm_deleteTorrent).setTitle(
					R.string.dt_deleteTorrent);

			// Cancel
			builder.setNeutralButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog

							okay = false;
							Log.i("Okay?", "FALSE");
						}
					});

			// Ok
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User accepted the dialog

							TorrentDetailsFragment tf = null;
							int position;
							String hash;

							if (findViewById(R.id.fragment_container) != null) {
								tf = (TorrentDetailsFragment) getFragmentManager()
										.findFragmentById(R.id.content_frame);
							} else {

								if (getFragmentManager().findFragmentById(
										R.id.one_frame) instanceof TorrentDetailsFragment) {

									tf = (TorrentDetailsFragment) getFragmentManager()
											.findFragmentById(R.id.one_frame);
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
			builder.setMessage(R.string.dm_deleteDriveTorrent).setTitle(
					R.string.dt_deleteDriveTorrent);

			// Cancel
			builder.setNeutralButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User canceled the dialog
						}
					});

			// Ok
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User accepted the dialog

							TorrentDetailsFragment tf = null;
							int position;
							String hash;

							if (findViewById(R.id.fragment_container) != null) {
								tf = (TorrentDetailsFragment) getFragmentManager()
										.findFragmentById(R.id.content_frame);
							} else {

								if (getFragmentManager().findFragmentById(
										R.id.one_frame) instanceof TorrentDetailsFragment) {

									tf = (TorrentDetailsFragment) getFragmentManager()
											.findFragmentById(R.id.one_frame);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SETTINGS_CODE) {

			// Select "All" torrents list
			selectItem(0);
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
			json += ",\"max_uploads_per_torrent\":"
					+ max_num_upslots_per_torrent;

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
		}

	}

	private void addUrlTorrent() {

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View addTorrentView = li.inflate(R.layout.add_torrent, null);

		// URL input
		final EditText urlInput = (EditText) addTorrentView
				.findViewById(R.id.url);

		// Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		// Set add_torrent.xml to AlertDialog builder
		builder.setView(addTorrentView);

		// Cancel
		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// Ok
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
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
		// TODO Auto-generated method stub
		Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		// startActivity(intent);
		startActivityForResult(intent, SETTINGS_CODE);

	}

	private void openOptions() {
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
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		builderPrefs = new StringBuilder();

		builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

		// Get values from preferences
		hostname = sharedPrefs.getString("hostname", "NULL");
		protocol = sharedPrefs.getString("protocol", "NULL");
		port = Integer.parseInt(sharedPrefs.getString("port", "80"));
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
	}

	// Get Options
	protected void getOptions() {
		// Preferences stuff
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		builderPrefs = new StringBuilder();

		builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

		// Get values from options
		global_max_num_connections = sharedPrefs.getString(
				"global_max_num_connections", "0");

		max_num_conn_per_torrent = sharedPrefs.getString(
				"max_num_conn_per_torrent", "0");
		max_num_upslots_per_torrent = sharedPrefs.getString(
				"max_num_upslots_per_torrent", "0");

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
			JSONParser jParser = new JSONParser(hostname, protocol, port,
					username, password);

			jParser.postCommand(params[0], params[1]);

			return params[0];

		}

		@Override
		protected void onPostExecute(String result) {

			int messageId = R.string.connection_error;

			if (result == null) {
				messageId = R.string.connection_error;
			}

			if ("start".equals(result)) {
				messageId = R.string.torrentStarted;
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

			if ("pauseAll".equals(result)) {
				messageId = R.string.AllTorrentsPaused;
			}

			if ("increasePrio".equals(result)) {
				messageId = R.string.increasePrioTorrent;
			}

			if ("decreasePrio".equals(result)) {
				messageId = R.string.decreasePrioTorrent;
			}

			if ("setQBittorrentPrefefrences".equals(result)) {
				messageId = R.string.decreasePrioTorrent;
			}

			Toast.makeText(getApplicationContext(), messageId,
					Toast.LENGTH_SHORT).show();

			switch (drawerList.getCheckedItemPosition()) {
			case 0:
				refreshWithDelay("all", 3);
				break;
			case 1:
				refreshWithDelay("downloading", 3);
				break;
			case 2:
				refreshWithDelay("completed", 3);
				break;
			case 3:
				refreshWithDelay("paused", 3);
				break;
			case 4:
				refreshWithDelay("active", 3);
				break;
			case 5:
				refreshWithDelay("inactive", 3);
				break;
			default:
				refreshWithDelay("all", 3);
				break;
			}

			Log.i("refresh_done", "refresh_perfomed");

		}
	}

	// Here is where the action happens
	private class qBittorrentTask extends AsyncTask<String, Integer, Torrent[]> {

		@Override
		protected Torrent[] doInBackground(String... params) {

			String name, size, info, progress, state, hash, ratio, leechs, seeds, priority;

			Torrent[] torrents = null;

			// Get settings
			getSettings();

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, protocol, port,
					username, password);

			JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);

			if (jArray != null) {

				// Log.i("jArray length", "" + jArray.length());

				try {

					torrents = new Torrent[jArray.length()];

					MainActivity.names = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						name = json.getString(TAG_NAME);
						size = json.getString(TAG_SIZE);
						progress = String.format("%.2f",
								json.getDouble(TAG_PROGRESS) * 100)
								+ "%";
						info = size + " | D:" + json.getString(TAG_DLSPEED)
								+ " | U:" + json.getString(TAG_UPSPEED) + " | "
								+ progress;
						state = json.getString(TAG_STATE);
						hash = json.getString(TAG_HASH);
						ratio = json.getString(TAG_RATIO);
						leechs = json.getString(TAG_NUMLEECHS);
						seeds = json.getString(TAG_NUMSEEDS);
						priority = json.getString(TAG_PRIORITY);

						torrents[i] = new Torrent(name, size, state, hash,
								info, ratio, progress, leechs, seeds, priority);

						MainActivity.names[i] = name;

						// Get torrent generic properties

						JSONObject json2 = jParser.getJSONFromUrl(params[3]
								+ hash);

						Log.i("JSON", "param[3]: " + params[3] + hash);
						Log.i("JSON", "length: " + json2.length());

						for (int j = 0; j < json2.length(); j++) {

							torrents[i].setSavePath(json2
									.getString(TAG_SAVE_PATH));
							torrents[i].setCreationDate(json2
									.getString(TAG_CREATION_DATE));
							torrents[i]
									.setComment(json2.getString(TAG_COMMENT));
							torrents[i].setTotalWasted(json2
									.getString(TAG_TOTAL_WASTED));
							torrents[i].setTotalUploaded(json2
									.getString(TAG_TOTAL_UPLOADED));
							torrents[i].setTotalDownloaded(json2
									.getString(TAG_TOTAL_DOWNLOADED));
							torrents[i].setTimeElapsed(json2
									.getString(TAG_TIME_ELAPSED));
							torrents[i].setNbConnections(json2
									.getString(TAG_NB_CONNECTIONS));
							torrents[i].setShareRatio(json2
									.getString(TAG_SHARE_RATIO));
						}

					}
				} catch (JSONException e) {
					Log.e("MAIN:", e.toString());
				}

			}
			return torrents;

		}

		@Override
		protected void onPostExecute(Torrent[] result) {

			if (result == null) {

				Toast.makeText(getApplicationContext(),
						R.string.connection_error, Toast.LENGTH_SHORT).show();

			} else {

				ArrayList<Torrent> torrentsFiltered = new ArrayList<Torrent>();

				for (int i = 0; i < result.length; i++) {

					if (params[1].equals("all")) {
						torrentsFiltered.add(result[i]);
					}

					if (params[1].equals("downloading")) {
						if ("downloading".equals(result[i].getState())
								|| "stalledDL".equals(result[i].getState())
								|| "pausedDL".equals(result[i].getState())
								|| "queuedDL".equals(result[i].getState())
								|| "checkingDL".equals(result[i].getState())) {
							torrentsFiltered.add(result[i]);
						}
					}

					if (params[1].equals("completed")) {
						if ("uploading".equals(result[i].getState())
								|| "stalledUP".equals(result[i].getState())
								|| "pausedUP".equals(result[i].getState())
								|| "queuedUP".equals(result[i].getState())
								|| "checkingUP".equals(result[i].getState())) {
							torrentsFiltered.add(result[i]);
						}
					}

					if (params[1].equals("paused")) {
						if ("pausedDL".equals(result[i].getState())
								|| "pausedUP".equals(result[i].getState())) {
							torrentsFiltered.add(result[i]);
						}
					}

					if (params[1].equals("active")) {
						if ("uploading".equals(result[i].getState())
								|| "downloading".equals(result[i].getState())) {
							torrentsFiltered.add(result[i]);
						}
					}

					if (params[1].equals("inactive")) {
						if ("pausedUP".equals(result[i].getState())
								|| "pausedDL".equals(result[i].getState())
								|| "queueUP".equals(result[i].getState())
								|| "queueDL".equals(result[i].getState())
								|| "stalledUP".equals(result[i].getState())
								|| "stalledDL".equals(result[i].getState())) {
							torrentsFiltered.add(result[i]);
						}
					}

				}

				// MainActivity.lines = (torrent[]) torrentsFiltered.toArray();

				// Get names (delete in background method)
				MainActivity.names = new String[torrentsFiltered.size()];
				MainActivity.lines = new Torrent[torrentsFiltered.size()];

				for (int i = 0; i < torrentsFiltered.size(); i++) {

					Torrent torrent = torrentsFiltered.get(i);

					MainActivity.names[i] = torrent.getFile();
					MainActivity.lines[i] = torrent;
				}

				try {

					if (findViewById(R.id.one_frame) != null) {

						getFragmentManager().popBackStack();

					}

					Log.i("Refresh >", "About to set Adapter");
					firstFragment.setListAdapter(new myAdapter());

					// Create the about fragment
					AboutFragment aboutFragment = new AboutFragment();

					// Got some results
					if (torrentsFiltered.size() > 0) {

						// Add the fragment to the 'list_frame' FrameLayout
						FragmentManager fragmentManager = getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();

						// Set the second fragments container
						if (findViewById(R.id.fragment_container) != null) {
							firstFragment
									.setSecondFragmentContainer(R.id.content_frame);
							fragmentTransaction.replace(R.id.list_frame,
									firstFragment);

						} else {
							firstFragment
									.setSecondFragmentContainer(R.id.one_frame);
							fragmentTransaction.replace(R.id.one_frame,
									firstFragment);
						}

						fragmentTransaction.commit();

						Log.i("Refresh", "ItemList?: "
								+ (firstFragment instanceof ItemstFragment));

						ListView lv = firstFragment.getListView();

						lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

						// Also update the second fragment (if it comes from the
						// drawer)
						if (params[2].equals("clear") && lv.getCount() > 0) {

							Log.i("Refresh >", "Clear init");

							// Scroll to the first position
							lv.smoothScrollToPosition(0);

							// Notify there isn't any item selected
							// firstFragment.setSelection(-1);

							if (findViewById(R.id.fragment_container) != null) {

								// Reset the BackStack (Back button)
								fragmentManager = getFragmentManager();

								for (int i = 0; i < getFragmentManager()
										.getBackStackEntryCount(); ++i) {
									getFragmentManager()
											.popBackStack(
													"secondFragment",
													FragmentManager.POP_BACK_STACK_INCLUSIVE);
								}

								// Replace with the about fragment
								fragmentManager
										.beginTransaction()
										.replace(R.id.content_frame,
												aboutFragment).commit();

							} else {

								// Just one fragment
								// Reset the BackStack (Back button)
								fragmentManager = getFragmentManager();
								for (int i = 0; i < fragmentManager
										.getBackStackEntryCount(); ++i) {
									fragmentManager.popBackStack();
								}

								// Replace with the about fragment
								fragmentManager
										.beginTransaction()
										.replace(R.id.one_frame, firstFragment,
												"firstFragment").commit();
							}

						}

						if (params[2].equals("") && lv.getCount() > 0) {

							// Scroll listView to the first position
							lv.smoothScrollToPosition(0);

							if (aboutFragment != null) {

								if (findViewById(R.id.fragment_container) != null) {

									// Reset the BackStack (Back button)
									fragmentManager = getFragmentManager();

									for (int i = 0; i < getFragmentManager()
											.getBackStackEntryCount(); ++i) {
										getFragmentManager()
												.popBackStack(
														"secondFragment",
														FragmentManager.POP_BACK_STACK_INCLUSIVE);
									}

									fragmentManager = getFragmentManager();
									fragmentManager
											.beginTransaction()
											.replace(R.id.content_frame,
													aboutFragment).commit();

								}

								else {

									fragmentManager = getFragmentManager();

									fragmentManager
											.beginTransaction()
											.replace(R.id.one_frame,
													firstFragment,
													"firstFragment").commit();

								}
							}

						}

					} else {

						// No results
						String[] emptyList = new String[] { NO_RESULTS };
						firstFragment.setListAdapter(new ArrayAdapter<String>(
								MainActivity.this, R.layout.no_items_found,
								R.id.no_results, emptyList));

						// Add the fragment to the 'list_frame' FrameLayout
						FragmentTransaction fragmentTransaction = getFragmentManager()
								.beginTransaction();

						// Set the second fragments container
						if (findViewById(R.id.fragment_container) != null) {
							firstFragment
									.setSecondFragmentContainer(R.id.content_frame);
							fragmentTransaction.replace(R.id.list_frame,
									firstFragment);
							fragmentTransaction.replace(R.id.content_frame,
									aboutFragment);

						} else {
							firstFragment
									.setSecondFragmentContainer(R.id.one_frame);
							fragmentTransaction.replace(R.id.one_frame,
									firstFragment);

						}

						fragmentTransaction.commit();

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

			}
		}
	}

	// Here is where the action happens
	private class qBittorrentSetOptions extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			// Get settings
			getSettings();

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, protocol, port,
					username, password);

			//
			JSONObject json = jParser.getJSONFromUrl(params[0]);

			if (json != null) {

				try {

					// // Option
					// protected static int global_upload;
					// protected static int global_download;
					// protected static int alt_upload;
					// protected static int alt_download;
					// protected static boolean torrent_queueing;
					// protected static String max_act_downloads;
					// protected static String max_act_uploads;
					// protected static String max_act_torrents;

					global_max_num_connections = json
							.getString(TAG_GLOBAL_MAX_NUM_CONNECTIONS);
					max_num_conn_per_torrent = json
							.getString(TAG_MAX_NUM_CONN_PER_TORRENT);
					max_num_upslots_per_torrent = json
							.getString(TAG_MAX_NUM_UPSLOTS_PER_TORRENT);

					// Save options locally
					sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
					Editor editor = sharedPrefs.edit();
					
					// Save key-values
					editor.putString("global_max_num_connections",
							global_max_num_connections);
					editor.putString("max_num_conn_per_torrent",
							max_num_conn_per_torrent);
					editor.putString("max_num_upslots_per_torrent",
							max_num_upslots_per_torrent);

					// Commit changes
					editor.commit();

				} catch (Exception e) {
					Log.e("MAIN:", e.toString());
					return null;
				}

			}
			return "ok";

		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {

				Toast.makeText(getApplicationContext(),
						R.string.connection_error, Toast.LENGTH_SHORT).show();

			} else {

				// Open options activity
				openOptions();

			}
		}
	}

	class myAdapter extends ArrayAdapter<String> {
		public myAdapter() {
			// TODO Auto-generated constructor stub
			super(MainActivity.this, R.layout.row, R.id.file, names);
			Log.i("myAdapter", "lines: " + lines.length);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			String state = lines[position].getState();

			TextView info = (TextView) row.findViewById(R.id.info);

			info.setText("" + lines[position].getInfo());

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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
			qBittorrentSetOptions qso = new qBittorrentSetOptions();
			qso.execute(new String[] { "json/preferences" });
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

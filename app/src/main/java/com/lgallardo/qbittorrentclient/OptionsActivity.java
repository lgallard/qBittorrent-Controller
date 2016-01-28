/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;

public class OptionsActivity extends PreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

	private EditTextPreference global_max_num_connections;
	private EditTextPreference max_num_conn_per_torrent;
	private EditTextPreference max_num_upslots_per_torrent;
	private EditTextPreference global_upload;
	private EditTextPreference global_download;
	private EditTextPreference alt_upload;
	private EditTextPreference alt_download;

	private CheckBoxPreference schedule_alternative_rate_limits;
	private TimePreference alt_from;
	private TimePreference alt_to;
	private ListPreference scheduler_days;

	private CheckBoxPreference torrent_queueing;
	private EditTextPreference max_act_downloads;
	private EditTextPreference max_act_uploads;
	private EditTextPreference max_act_torrents;

	private CheckBoxPreference max_ration_enabled;
	private EditTextPreference max_ratio;
	private ListPreference max_ratio_act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Set Theme
		this.setTheme(R.style.Theme_Light);

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.options);

        // Set last state in intent result
        Intent result = new Intent();
        result.putExtra("currentState", MainActivity.currentState);
        setResult(Activity.RESULT_OK, result);


		// Read values
		global_max_num_connections = (EditTextPreference) findPreference("global_max_num_connections");
		max_num_conn_per_torrent = (EditTextPreference) findPreference("max_num_conn_per_torrent");
		max_num_upslots_per_torrent = (EditTextPreference) findPreference("max_num_upslots_per_torrent");
		global_upload = (EditTextPreference) findPreference("global_upload");
		global_download = (EditTextPreference) findPreference("global_download");
		alt_upload = (EditTextPreference) findPreference("alt_upload");
		alt_download = (EditTextPreference) findPreference("alt_download");

		schedule_alternative_rate_limits = (CheckBoxPreference) findPreference("schedule_alternative_rate_limits");
		alt_from = (TimePreference) findPreference("alt_from");
		alt_to = (TimePreference) findPreference("alt_to");
		scheduler_days = (ListPreference) findPreference("scheduler_days");

		torrent_queueing = (CheckBoxPreference) findPreference("torrent_queueing");
		max_act_downloads = (EditTextPreference) findPreference("max_act_downloads");
		max_act_uploads = (EditTextPreference) findPreference("max_act_uploads");
		max_act_torrents = (EditTextPreference) findPreference("max_act_torrents");

		max_ration_enabled = (CheckBoxPreference) findPreference("max_ration_enabled");
		max_ratio = (EditTextPreference) findPreference("max_ratio");
		max_ratio_act = (ListPreference) findPreference("max_ratio_act");

		// Get values for server
		getQBittorrentOptionValues();

		// Set preference change listeners

		alt_from.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
				alt_from.setSummary(TimePreference.fixStringDecimal(alt_from.getPicker().getCurrentHour()) + ":" + TimePreference.fixStringDecimal(alt_from.getPicker().getCurrentMinute()));

				return true;
			}

		});

		alt_to.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
				alt_to.setSummary(TimePreference.fixStringDecimal(alt_to.getPicker().getCurrentHour()) + ":" + TimePreference.fixStringDecimal(alt_to.getPicker().getCurrentMinute()));

				return true;
			}

		});

//		scheduler_days.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//				scheduler_days.setSummary(scheduler_days.getEntries()[Integer.parseInt((String) newValue)]);
//
//				return true;
//			}
//
//		});



	}

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        saveQBittorrentOptionValues();
        super.onPause();
    }

	private void getQBittorrentOptionValues() {

		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();


		String global_max_num_connections_value = sharedPrefs.getString("global_max_num_connections", "0");
		String global_upload_value = sharedPrefs.getString("global_upload", "0");
		String max_ratio_value = sharedPrefs.getString("max_ratio", "0");


		if(global_max_num_connections_value.equals("-1")){
			global_max_num_connections_value = "0";
		}

		if(global_upload_value.equals("-1")){
			global_upload_value = "0";
		}

		if(max_ratio_value.equals("-1")){
			max_ratio_value = "1";
		}


		global_max_num_connections.setText(global_max_num_connections_value);
		global_max_num_connections.setSummary(global_max_num_connections_value);



		max_num_conn_per_torrent.setSummary(sharedPrefs.getString("max_num_conn_per_torrent", ""));
		max_num_upslots_per_torrent.setSummary(sharedPrefs.getString("max_num_upslots_per_torrent", ""));

		global_upload.setText(global_upload_value);
		global_upload.setSummary(global_upload_value);



		global_download.setSummary(sharedPrefs.getString("global_download", ""));
		alt_upload.setSummary(sharedPrefs.getString("alt_upload", ""));
		alt_download.setSummary(sharedPrefs.getString("alt_download", ""));

		max_ratio.setText(max_ratio_value);

		max_act_downloads.setSummary(sharedPrefs.getString("max_act_downloads", ""));
		max_act_uploads.setSummary(sharedPrefs.getString("max_act_uploads", ""));
		max_act_torrents.setSummary(sharedPrefs.getString("max_act_torrents", ""));




		String time_from = sharedPrefs.getString("alt_from", "8:00");
		String time_to = sharedPrefs.getString("alt_to", "20:00");

		alt_from.setSummary(TimePreference.fixStringDecimal(TimePreference.getHour(time_from)) + ":" + TimePreference.fixStringDecimal(TimePreference.getMinute(time_from)));
		alt_to.setSummary(TimePreference.fixStringDecimal(TimePreference.getHour(time_to)) + ":" + TimePreference.fixStringDecimal(TimePreference.getMinute(time_to)));

		if (scheduler_days.getEntry() == null) {
			scheduler_days.setValueIndex(1);
		}

		scheduler_days.setSummary(scheduler_days.getEntry());

		max_ratio.setSummary(sharedPrefs.getString("max_ratio",""));


		if (max_ratio_act.getEntry() == null) {
			max_ratio_act.setValueIndex(0);
		}

		max_ratio_act.setSummary(max_ratio_act.getEntry());

	}

	private void saveQBittorrentOptionValues(){
		// Save options locally
		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

		SharedPreferences.Editor editor = sharedPrefs.edit();

		if (global_max_num_connections.getText().toString() != null && global_max_num_connections.getText().toString() != "") {
			editor.putString("global_max_num_connections", global_max_num_connections.getText().toString());
		}


        if (max_num_conn_per_torrent.getText().toString() != null && max_num_conn_per_torrent.getText().toString() != "") {
            editor.putString("max_num_conn_per_torrent", max_num_conn_per_torrent.getText().toString());
        }

        if (max_num_upslots_per_torrent.getText().toString() != null && max_num_upslots_per_torrent.getText().toString() != "") {
            editor.putString("max_num_upslots_per_torrent", max_num_upslots_per_torrent.getText().toString());
        }



		//TODO: Complete saving values


//		Log.d("Debug", "1) max_ratio: " + max_ratio.getText().toString());



		if (global_upload.getText().toString() != null && global_upload.getText().toString() != "") {

			if(global_upload.getText().equals("-1")){
				global_upload.setText("0");
			}

			editor.putString("global_upload", global_upload.getText().toString());
		}


		editor.putString("max_ratio", max_ratio.getText().toString());


        // Commit changes
		editor.commit();


	}



	public void refreshScreenValues() {

		global_max_num_connections.setSummary(global_max_num_connections.getText());
		max_num_conn_per_torrent.setSummary(max_num_conn_per_torrent.getText());
		max_num_upslots_per_torrent.setSummary(max_num_upslots_per_torrent.getText());

		global_upload.setSummary(global_upload.getText());
		global_download.setSummary(global_download.getText());
		alt_upload.setSummary(alt_upload.getText());
		alt_download.setSummary(alt_download.getText());

		scheduler_days.setSummary(scheduler_days.getEntry());

		max_act_downloads.setSummary(max_act_downloads.getText());
		max_act_uploads.setSummary(max_act_uploads.getText());
		max_act_torrents.setSummary(max_act_torrents.getText());




		max_ratio.setSummary(max_ratio.getText());
		max_ratio_act.setSummary(max_ratio_act.getEntry());
	}
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Update values on Screen
//		Log.d("Debug", "option changed");
		refreshScreenValues();
	}
}

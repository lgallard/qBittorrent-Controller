/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;

import java.sql.Time;

public class OptionsActivity extends PreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

	private TimePreference alt_from;
	private TimePreference alt_to;
	private ListPreference scheduler_days;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.options);

        // Set last state in intent result
        Intent result = new Intent();
        result.putExtra("currentState", MainActivity.currentState);
        setResult(Activity.RESULT_OK, result);

		alt_from = (TimePreference) findPreference("alt_from");
		alt_to = (TimePreference) findPreference("alt_to");
		scheduler_days = (ListPreference) findPreference("scheduler_days");

		// Get values for server
		getQBittorrentOptionValues();

		// Set prefrerence change listeners

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

		scheduler_days.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				Log.d("Debug", newValue.toString());
				scheduler_days.setSummary(scheduler_days.getEntries()[Integer.parseInt((String) newValue)]);

				return true;
			}

		});

	}

	private void getQBittorrentOptionValues() {

		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

		String time_from = sharedPrefs.getString("alt_from", "12:00");
		String time_to = sharedPrefs.getString("alt_to", "12:00");

		alt_from.setSummary(TimePreference.fixStringDecimal(TimePreference.getHour(time_from)) + ":" + TimePreference.fixStringDecimal(TimePreference.getMinute(time_from)));
		alt_to.setSummary(TimePreference.fixStringDecimal(TimePreference.getHour(time_to)) + ":" + TimePreference.fixStringDecimal(TimePreference.getMinute(time_to)));

		if (scheduler_days.getEntry() == null) {
			scheduler_days.setValueIndex(1);
		}

		scheduler_days.setSummary(scheduler_days.getEntry());

	}

	private void saveQBittorrentOptionValues(){
		// Save options locally
		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

		SharedPreferences.Editor editor = sharedPrefs.edit();


//		editor.putString("hostname" + currentServerValue, hostname.getText().toString());

		// Commit changes
		editor.commit();


	}



	public void refreshScreenValues() {
		scheduler_days.setSummary(scheduler_days.getEntry());
	}
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Update values on Screen
		refreshScreenValues();
	}
}

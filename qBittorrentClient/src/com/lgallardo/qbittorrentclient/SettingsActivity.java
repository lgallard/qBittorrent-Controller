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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity implements android.content.SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference currentServer;
    private EditTextPreference hostname;
    private EditTextPreference subfolder;
    private CheckBoxPreference https;
    private EditTextPreference port;
    private EditTextPreference username;
    private EditTextPreference password;
    private CheckBoxPreference old_version;
    private String currentServerValue;

    private CheckBoxPreference auto_refresh;
    private ListPreference refresh_period;

    private EditTextPreference connection_timeout;
    private EditTextPreference data_timeout;

    private ListPreference sortBy;
    private CheckBoxPreference reverse_order;

    private CheckBoxPreference dark_ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Get preferences from screen
        currentServer = (ListPreference) findPreference("currentServer");
        hostname = (EditTextPreference) findPreference("hostname");
        subfolder = (EditTextPreference) findPreference("subfolder");
        https = (CheckBoxPreference) findPreference("https");
        port = (EditTextPreference) findPreference("port");
        username = (EditTextPreference) findPreference("username");
        password = (EditTextPreference) findPreference("password");
        old_version = (CheckBoxPreference) findPreference("old_version");
        auto_refresh = (CheckBoxPreference) findPreference("auto_refresh");
        refresh_period = (ListPreference) findPreference("refresh_period");
        connection_timeout = (EditTextPreference) findPreference("connection_timeout");
        data_timeout = (EditTextPreference) findPreference("data_timeout");
        sortBy = (ListPreference) findPreference("sortby");
        reverse_order = (CheckBoxPreference) findPreference("reverse_order");

        dark_ui = (CheckBoxPreference) findPreference("dark_ui");

        // Get values for server
        getQBittorrentServerValues(currentServer.getValue());

        Preference pref = findPreference("currentServer");
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // do whatever you want with new value

                Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                // Message
                builder.setMessage(R.string.settings_qbittorrent_pro_message).setTitle(R.string.settings_qbittorrent_pro_title);

                // Ok
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // User accepted the dialog

                        // Set first server
                        currentServer.setValueIndex(0);
                    }
                });

                // Create dialog
                AlertDialog dialog = builder.create();

                // Show dialog
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub

        // Update values on Screen
        refreshScreenValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        saveQBittorrentServerValues();
        super.onPause();
    }

    public void getQBittorrentServerValues(String value) {

        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

        currentServer.setSummary(currentServer.getEntry());
        hostname.setText(sharedPrefs.getString("hostname" + value, ""));
        hostname.setSummary(sharedPrefs.getString("hostname" + value, ""));

        if (hostname.getText().toString().equals("")) {

            hostname.setSummary(getString(R.string.settings_qbittorrent_hostname_hint));

        }

        subfolder.setText(sharedPrefs.getString("subfolder" + value, ""));
        subfolder.setSummary(sharedPrefs.getString("subfolder" + value, ""));

        https.setChecked(sharedPrefs.getBoolean("https" + value, false));

        port.setText(sharedPrefs.getString("port" + value, "8080"));
        port.setSummary(sharedPrefs.getString("port" + value, "8080"));

        username.setText(sharedPrefs.getString("username" + value, "admin"));
        username.setSummary(sharedPrefs.getString("username" + value, "admin"));

        password.setText(sharedPrefs.getString("password" + value, "adminadmin"));
        old_version.setChecked(sharedPrefs.getBoolean("old_version" + value, false));

        if (refresh_period.getEntry() == null) {
            refresh_period.setValueIndex(6);
        }

        refresh_period.setSummary(refresh_period.getEntry());

        connection_timeout.setText(sharedPrefs.getString("connection_timeout", "5"));
        data_timeout.setText(sharedPrefs.getString("data_timeout", "8"));

        if (sortBy.getEntry() == null) {
            sortBy.setValueIndex(1);
        }

        sortBy.setSummary(sortBy.getEntry());
        reverse_order.setChecked(sharedPrefs.getBoolean("reverse_order", false));

        dark_ui.setChecked(sharedPrefs.getBoolean("dark_ui", false));

    }

    public void refreshScreenValues() {

        currentServer.setSummary(currentServer.getEntry());
        hostname.setSummary(hostname.getText());
        subfolder.setSummary(subfolder.getText());
        port.setSummary(port.getText());
        username.setSummary(username.getText());
        refresh_period.setSummary(refresh_period.getEntry());
        sortBy.setSummary(sortBy.getEntry());
    }

    public void saveQBittorrentServerValues() {

        currentServerValue = currentServer.getValue();

        // Save options locally
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

        // SharedPreferences sharedPrefs =
        // PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor editor = sharedPrefs.edit();

        if (hostname.getText().toString() != null && hostname.getText().toString() != "") {

            editor.putString("hostname" + currentServerValue, hostname.getText().toString());
        }

        if (subfolder.getText().toString() != null) {
            editor.putString("subfolder" + currentServerValue, subfolder.getText().toString());
        }

        editor.putBoolean("https" + currentServerValue, https.isChecked());

        if (port.getText().toString() != null && port.getText().toString() != "") {

            editor.putString("port" + currentServerValue, port.getText().toString());
        }

        if (username.getText().toString() != null && username.getText().toString() != "") {

            editor.putString("username" + currentServerValue, username.getText().toString());
        }

        if (password.getText().toString() != null && password.getText().toString() != "") {

            editor.putString("password" + currentServerValue, password.getText().toString());
        }

        editor.putBoolean("old_version" + currentServerValue, old_version.isChecked());

        if (connection_timeout.getText().toString() != null && connection_timeout.getText().toString() != "") {
            editor.putString("connection_timeout", connection_timeout.getText().toString());
        }

        if (data_timeout.getText().toString() != null && data_timeout.getText().toString() != "") {
            editor.putString("data_timeout", data_timeout.getText().toString());
        }

        editor.putBoolean("revserse_order" + currentServerValue, reverse_order.isChecked());

        editor.putBoolean("dark_ui" + currentServerValue, dark_ui.isChecked());

        // Commit changes
        editor.commit();

    }

}
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference currentServer;
    private EditTextPreference hostname;
    private EditTextPreference subfolder;
    private CheckBoxPreference https;
    private EditTextPreference port;
    private EditTextPreference username;
    private EditTextPreference password;
    private String currentServerValue;

    private CheckBoxPreference auto_refresh;
    private ListPreference refresh_period;

    private EditTextPreference connection_timeout;
    private EditTextPreference data_timeout;

    private CheckBoxPreference dark_ui;

    private CheckBoxPreference enable_notifications;
    private ListPreference notification_period;

    private EditTextPreference ssid;
    private EditTextPreference local_hostname;
    private EditTextPreference local_port;

    private Preference keystore_path;
    private EditTextPreference keystore_password;

    private CheckBoxPreference pathAndLabelDialog;


    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set Theme
        this.setTheme(R.style.Theme_Light);

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
        auto_refresh = (CheckBoxPreference) findPreference("auto_refresh");
        refresh_period = (ListPreference) findPreference("refresh_period");
        connection_timeout = (EditTextPreference) findPreference("connection_timeout");
        data_timeout = (EditTextPreference) findPreference("data_timeout");

        dark_ui = (CheckBoxPreference) findPreference("dark_ui");

        enable_notifications = (CheckBoxPreference) findPreference("enable_notifications");
        notification_period = (ListPreference) findPreference("notification_period");


        ssid = (EditTextPreference) findPreference("ssid");
        local_hostname = (EditTextPreference) findPreference("local_hostname");
        local_port = (EditTextPreference) findPreference("local_port");

        keystore_path = (Preference) findPreference("keystore_path");
        keystore_password = (EditTextPreference) findPreference("keystore_password");

        // Get values for server
        getQBittorrentServerValues(currentServer.getValue());


        // savepath and label (category) checkbox
        pathAndLabelDialog = (CheckBoxPreference) findPreference("pathAndLabelDialog");


        // Enable savepath and label preference if API7+ (qBittorrent 3.3.1+)
        pathAndLabelDialog.setEnabled(true);

        try {
            // TODO: Check when this changed (qb_api X.Y.Z )
            if (MainActivity.qb_api >= 7) {
                pathAndLabelDialog.setEnabled(true);
            } else {
                pathAndLabelDialog.setEnabled(false);
            }

        } catch (Exception e) {
        }


        Preference pref = findPreference("currentServer");
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // do whatever you want with new value

                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {


                    Builder builder = new Builder(SettingsActivity.this);

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
                    final AlertDialog dialog = builder.create();

                    // This detects if the back button was press while showing the dialog
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //do whatever you want the back key to do
                            // Set first server
                            currentServer.setValueIndex(0);

                        }
                    });

                    // Show dialog
                    dialog.show();


                } else {

                    // Read and load preferences
                    saveQBittorrentServerValues();
                    getQBittorrentServerValues(newValue.toString());
                }

                return true;
            }
        });


        Preference pref2 = findPreference("ssid");
        pref2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // do whatever you want with new value

                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {


                    Builder builder = new Builder(SettingsActivity.this);

                    // Message
                    builder.setMessage(R.string.settings_qbittorrent_pro_message).setTitle(R.string.settings_qbittorrent_pro_title);

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog

                            // Set first server
                            ssid.setText("");
                        }
                    });

                    // Create dialog
                    final AlertDialog dialog = builder.create();

                    // This detects if the back button was press while showing the dialog
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //do whatever you want the back key to do
                            // Set first server
                            ssid.setText("");

                        }
                    });

                    // Show dialog
                    dialog.show();


                }

                return true;
            }
        });

        Preference pref3 = findPreference("local_hostname");
        pref3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // do whatever you want with new value

                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {


                    Builder builder = new Builder(SettingsActivity.this);

                    // Message
                    builder.setMessage(R.string.settings_qbittorrent_pro_message).setTitle(R.string.settings_qbittorrent_pro_title);

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog

                            // Set first server
                            local_hostname.setText("");
                        }
                    });

                    // Create dialog
                    final AlertDialog dialog = builder.create();

                    // This detects if the back button was press while showing the dialog
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //do whatever you want the back key to do
                            // Set first server
                            local_hostname.setText("");

                        }
                    });

                    // Show dialog
                    dialog.show();


                }

                return true;
            }
        });

        Preference pref4 = findPreference("local_port");
        pref4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // do whatever you want with new value

                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {


                    Builder builder = new Builder(SettingsActivity.this);

                    // Message
                    builder.setMessage(R.string.settings_qbittorrent_pro_message).setTitle(R.string.settings_qbittorrent_pro_title);

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog

                            // Set first server
                            local_port.setText("");
                        }
                    });

                    // Create dialog
                    final AlertDialog dialog = builder.create();

                    // This detects if the back button was press while showing the dialog
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //do whatever you want the back key to do
                            // Set first server
                            local_port.setText("");

                        }
                    });

                    // Show dialog
                    dialog.show();


                }

                return true;
            }
        });


        // Set last state in intent result
        Intent result = new Intent();
        result.putExtra("currentState", MainActivity.currentState);
        setResult(Activity.RESULT_OK, result);


        Preference keystore_path = (Preference) findPreference("keystore_path");
        keystore_path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                // Check dangerous permissions and open file picker
                openFilePicker();

                return true;
            }
        });

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
                                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        });

            }else{

                // No explanation needed, request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }


        } else {

            // Permissions granted, open file picker
            Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(".*\\.bks"));
            startActivityForResult(intent, 1);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permissions granted, open file picker
                    Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
                    intent.putExtra(FilePickerActivity.ARG_FILE_FILTER, Pattern.compile(".*\\.bks"));
                    startActivityForResult(intent, 1);


                } else {

                    // Permission denied


                    // Should we show an explanation?
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        genericOkCancelDialog(R.string.error_grant_permission2,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent appIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        appIntent.setData(Uri.parse("package:" + MainActivity.packageName));
                                        startActivityForResult(appIntent, 0);
                                    }
                                });
                    }
                    return;
                }
            }
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


    public void genericOkDialog(int title, int message, DialogInterface.OnClickListener okListener) {

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

    private void genericOkCancelDialog(int message, DialogInterface.OnClickListener okListener) {

        genericOkCancelDialog(-1, message, okListener);

    }

    private void genericOkCancelDialog(int message) {

        genericOkCancelDialog(-1, message, null);

    }

    private void genericOkCancelDialog(int title, int message, DialogInterface.OnClickListener okListener) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        String keystore_path_value = "";

        // MaterialDesignPicker
        if (requestCode == 1 && resultCode == RESULT_OK) {
            keystore_path_value = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        }

        // Save keystore path
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
        Editor editor = sharedPrefs.edit();

        editor.putString("keystore_path" + currentServerValue, keystore_path_value);
        editor.commit();

        keystore_path.setSummary(keystore_path_value);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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

        if (refresh_period.getEntry() == null) {
            refresh_period.setValueIndex(6);
        }

        refresh_period.setSummary(refresh_period.getEntry());

        connection_timeout.setText(sharedPrefs.getString("connection_timeout" + value, "10"));
        connection_timeout.setSummary(sharedPrefs.getString("connection_timeout" + value, "10"));

        data_timeout.setText(sharedPrefs.getString("data_timeout" + value, "20"));
        data_timeout.setSummary(sharedPrefs.getString("data_timeout" + value, "20"));


        dark_ui.setChecked(sharedPrefs.getBoolean("dark_ui", false));

        if (notification_period.getEntry() == null) {
            notification_period.setValueIndex(1);
        }

        notification_period.setSummary(notification_period.getEntry());


        ssid.setText(sharedPrefs.getString("ssid" + value, ""));
        ssid.setSummary(sharedPrefs.getString("ssid" + value, ""));

        local_hostname.setText(sharedPrefs.getString("local_hostname" + value, ""));
        local_hostname.setSummary(sharedPrefs.getString("local_hostname" + value, ""));

        local_port.setText(sharedPrefs.getString("local_port" + value, ""));
        local_port.setSummary(sharedPrefs.getString("local_port" + value, ""));

        keystore_path.setSummary(sharedPrefs.getString("keystore_path" + value, ""));
        keystore_password.setText(sharedPrefs.getString("keystore_password" + value, ""));

    }

    public void refreshScreenValues() {

        currentServer.setSummary(currentServer.getEntry());
        hostname.setSummary(hostname.getText());
        subfolder.setSummary(subfolder.getText());
        port.setSummary(port.getText());
        username.setSummary(username.getText());
        refresh_period.setSummary(refresh_period.getEntry());
        notification_period.setSummary(notification_period.getEntry());
        connection_timeout.setSummary(connection_timeout.getText());
        data_timeout.setSummary(data_timeout.getText());


        ssid.setSummary(ssid.getText());
        local_hostname.setSummary(local_hostname.getText());
        local_port.setSummary(local_port.getText());


    }

    public void saveQBittorrentServerValues() {

        currentServerValue = currentServer.getValue();

        // Save options locally
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

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

        if (connection_timeout.getText().toString() != null && connection_timeout.getText().toString() != "") {
            editor.putString("connection_timeout", connection_timeout.getText().toString());
        }

        if (data_timeout.getText().toString() != null && data_timeout.getText().toString() != "") {
            editor.putString("data_timeout", data_timeout.getText().toString());
        }

        editor.putBoolean("dark_ui" + currentServerValue, dark_ui.isChecked());

        if (ssid.getText().toString() != null && ssid.getText().toString() != "") {
            editor.putString("ssid" + currentServerValue, ssid.getText().toString());
        }

        if (local_hostname.getText().toString() != null && local_hostname.getText().toString() != "") {
            editor.putString("local_hostname" + currentServerValue, local_hostname.getText().toString());
        }

        if (local_port.getText().toString() != null && local_port.getText().toString() != "") {
            editor.putString("local_port" + currentServerValue, local_port.getText().toString());
        }


        if (keystore_path.getSummary().toString() != null) {
            editor.putString("keystore_path" + currentServerValue, keystore_path.getSummary().toString());
        }

        if (keystore_password.getText().toString() != null) {
            editor.putString("keystore_password" + currentServerValue, keystore_password.getText().toString());
        }

        // Commit changes
        editor.commit();


        // Refresh drawer servers
        String[] navigationDrawerServerItems;
        navigationDrawerServerItems = getResources().getStringArray(R.array.qBittorrentServers);

        ArrayList<DrawerItem> serverItems = new ArrayList<DrawerItem>();

        // Server items
        int currentServerIntValue = 1;

        try {
            currentServerIntValue = Integer.parseInt(currentServerValue);
        } catch (NumberFormatException e) {

        }

        serverItems.add(new DrawerItem(R.drawable.ic_drawer_servers, getResources().getString(R.string.drawer_servers_category), MainActivity.DRAWER_CATEGORY, false, null));

        for (int i = 0; i < navigationDrawerServerItems.length; i++) {
            serverItems.add(new DrawerItem(R.drawable.ic_drawer_subitem, navigationDrawerServerItems[i], MainActivity.DRAWER_ITEM_SERVERS, ((i + 1) == currentServerIntValue), "changeCurrentServer"));
        }

        try {
            MainActivity.rAdapter.refreshDrawerServers(serverItems);

        } catch (Exception e) {
            Log.e("Debug", "SettingActivity - Couldn't refresh servers: " + e.toString());
        }
    }
}

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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HelpActivity extends PreferenceActivity {

    private Preference version;
    private Preference report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.help);



        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Get preferences from screen
        version = (Preference) findPreference("version");
        report = (Preference) findPreference("report");

        //Set version
        version.setSummary(pInfo.versionName);

        // Set click event
        report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {


                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(HelpActivity.this);
                View addTorrentView = li.inflate(R.layout.send_report, null);

                // URL input
                final EditText reportDescription = (EditText) addTorrentView.findViewById(R.id.report_description);

                if (!isFinishing()) {
                    // Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);

                    // Set add_torrent.xml to AlertDialog builder
                    builder.setView(addTorrentView);

                    // Cancel
                    builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            CustomLogger.setMainActivityReporting(false);

                        }
                    });

                    // Ok
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User accepted the dialog

                            CustomLogger.setReportDescription(reportDescription.getText().toString());
                            CustomLogger.setMainActivityReporting(true);

                            finish();

                        }
                    });

                    // Create dialog
                    AlertDialog dialog = builder.create();

                    // Show dialog
                    dialog.show();
                }


                return true;
            };
        });


        // Set last state in intent result
        Intent result = new Intent();
        result.putExtra("currentState", MainActivity.currentState);
        setResult(Activity.RESULT_OK, result);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

}

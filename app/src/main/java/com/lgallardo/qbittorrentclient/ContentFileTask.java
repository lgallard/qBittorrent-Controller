/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

package com.lgallardo.qbittorrentclient;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lgallard on 07/06/16.
 */

// // Here is where the action happens
public class ContentFileTask extends AsyncTask<String, Void, ArrayList<TorrentDetailsItem>> {

    String name, size;
    Double progress;
    int priority;
    private String qbQueryString = "query";
    String url;
    private ArrayList<TorrentDetailsItem> contentFiles;
    String hash;

    protected ArrayList<TorrentDetailsItem> doInBackground(String... params) {

        hash = params[0];

        // Get torrent's files
        if (MainActivity.qb_version.equals("2.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.1.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.2.x")) {
            qbQueryString = "query";
        }


        url = qbQueryString + "/propertiesFiles/";

        contentFiles = new ArrayList<TorrentDetailsItem>();

        try {

            JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port, MainActivity.keystore_path, MainActivity.keystore_password,
                    MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

            jParser.setCookie(MainActivity.cookie);

            JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

            if (jArray != null) {


                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json = jArray.getJSONObject(i);

                    name = json.getString(MainActivity.TAG_NAME);
                    size = json.getString(MainActivity.TAG_SIZE).replace(",", ".");
                    progress = json.getDouble(MainActivity.TAG_PROGRESS);
                    priority = json.getInt(MainActivity.TAG_PRIORITY);

                    if (MainActivity.qb_version.equals("3.2.x")) {
                        size = Common.calculateSize(json.getString(MainActivity.TAG_SIZE)).replace(",", ".");
                    }


                    // Add ContentFiles
                    contentFiles.add(new TorrentDetailsItem(name, size, progress, priority, null, TorrentDetailsItem.FILE, "setFilePriority"));


                }

            }

        } catch (Exception e) {

            Log.e("ContentFileTask:", e.toString());

        }

        Log.d("Debug", "contentFiles.size: " + contentFiles.size());

        return contentFiles;

    }

    @Override
    protected void onPostExecute(ArrayList<TorrentDetailsItem> contentFiles) {

        Log.d("Debug", "onPostExecute");
        Log.d("Debug", "onPostExecute - contentFiles.size: " + contentFiles.size());

        TorrentDetailsFragment.rAdapter.refreshContentFiles(contentFiles);


    }

}

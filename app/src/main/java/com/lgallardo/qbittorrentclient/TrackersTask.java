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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lgallard on 07/06/16.
 */

// // Here is where the action happens
public class TrackersTask extends AsyncTask<String, Void, ArrayList<TorrentDetailsItem>> {

    String name, size;
    Double progress;
    int priority;
    private String qbQueryString = "query";
    String url;
    private ArrayList<TorrentDetailsItem> trackers;

    String hash;

    static int MAX_CONTENT_FILES = 20;

    protected ArrayList<TorrentDetailsItem> doInBackground(String... params) {

        hash = params[0];
        // Get torrent's trackers
        if (MainActivity.qb_version.equals("2.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.1.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.2.x")) {
            qbQueryString = "query";
        }

        url = qbQueryString + "/propertiesTrackers/";

        trackers = new ArrayList<TorrentDetailsItem>();


        try {

            JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port, MainActivity.keystore_path, MainActivity.keystore_password, MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);
            jParser.setCookie(MainActivity.cookie);
            JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

            if (jArray != null) {

                TorrentDetailsFragment.trackerNames = new String[jArray.length()];

                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json = jArray.getJSONObject(i);

                    url = json.getString(MainActivity.TAG_URL);

//                    Log.d("Debug", "TrackersTask - url: " + url);

                    // Add trackers
                    trackers.add(new TorrentDetailsItem(null, null, null, -1, url, TorrentDetailsItem.TRACKER, "addTracker"));

                }

            }

        } catch (Exception e) {

            Log.e("Debug", "TrackersTask - error: ");
            Log.e("TrackersTask:", e.toString());

        }


        return trackers;

    }

    @Override
    protected void onPostExecute(ArrayList<TorrentDetailsItem> trackers) {
//        Log.d("Debug", "onPostExecute");
//        Log.d("Debug", "onPostExecute - contentFiles.size: " + trackers.size());
        try {
            TorrentDetailsFragment.trackerAdapter.refreshTrackers(trackers);
        } catch (Exception e) {
            Log.e("Debug", e.toString());
        }


    }

}

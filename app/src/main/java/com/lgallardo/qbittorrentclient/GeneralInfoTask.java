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

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lgallard on 07/06/16.
 */

// // Here is where the action happens
public class GeneralInfoTask extends AsyncTask<String, Void, ArrayList<GeneralInfoItem>> {

    String name, size;
    private String qbQueryString = "query";
    String url;

    String hash;
    JSONObject json2;

    protected ArrayList<GeneralInfoItem> doInBackground(String... params) {


        hash = params[0];

        GeneralInfoItem item;

        // Get torrent's extra info
        if (MainActivity.qb_version.equals("2.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.1.x")) {
            qbQueryString = "json";
        }

        if (MainActivity.qb_version.equals("3.2.x")) {
            qbQueryString = "query";
        }

        url = qbQueryString + "/propertiesGeneral/";

        try {

//                Log.e("Debug", "qBittorrentGeneralInfoTask");

            JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port, MainActivity.keystore_path, MainActivity.keystore_password, MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

//                if(jParser != null){
//
//                    Log.e("Debug", "jParser is not null");
//
//                }

            jParser.setCookie(MainActivity.cookie);


//                Log.e("Debug", "jParser cookie set");


            json2 = jParser.getJSONFromUrl(url + hash);

            if (json2 != null && json2.length() > 0) {

//                    Log.e("Debug", "json2 not null");

                // Save path
                item = TorrentDetailsFragment.generalInfoItems.get(0);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_SAVE_PATH));
                TorrentDetailsFragment.generalInfoItems.set(0, item);

//                    Log.e("Debug", "save path");

                // Creation date
                item = TorrentDetailsFragment.generalInfoItems.get(1);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_CREATION_DATE));
                TorrentDetailsFragment.generalInfoItems.set(1, item);

//                    Log.e("Debug", "Creation date");

                // Comment
                item = TorrentDetailsFragment.generalInfoItems.get(2);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_COMMENT));
                TorrentDetailsFragment.generalInfoItems.set(2, item);

//                    Log.e("Debug", "Comment");

                // Total wasted
                item = TorrentDetailsFragment.generalInfoItems.get(3);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_TOTAL_WASTED));
                TorrentDetailsFragment.generalInfoItems.set(3, item);

//                    Log.e("Debug", "Total wasted");

                // Total uploaded
                item = TorrentDetailsFragment.generalInfoItems.get(4);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_TOTAL_UPLOADED));
                TorrentDetailsFragment.generalInfoItems.set(4, item);

//                    Log.e("Debug", "Total uploaded");

                // Total downloaded
                item = TorrentDetailsFragment.generalInfoItems.get(5);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_TOTAL_DOWNLOADED));
                TorrentDetailsFragment.generalInfoItems.set(5, item);

//                    Log.e("Debug", "Total downloaded");

                // Time elapsed
                item = TorrentDetailsFragment.generalInfoItems.get(6);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_TIME_ELAPSED));
                TorrentDetailsFragment.generalInfoItems.set(6, item);

//                    Log.e("Debug", "Time elapsed");

                // Number of connections
                item = TorrentDetailsFragment.generalInfoItems.get(7);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_NB_CONNECTIONS));
                TorrentDetailsFragment.generalInfoItems.set(7, item);

//                    Log.e("Debug", "Number of connections");

                // Share ratio
                item = TorrentDetailsFragment.generalInfoItems.get(8);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_SHARE_RATIO));

                // Format ratio
                try {

                    item.setValue(String.format("%.2f", Float.parseFloat(item.getValue())).replace(",", "."));

                } catch (Exception e) {
                }

                TorrentDetailsFragment.generalInfoItems.set(8, item);


//                    Log.e("Debug", "Share ratio");

                // Upload limit
                item = TorrentDetailsFragment.generalInfoItems.get(9);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_UPLOAD_LIMIT));
                TorrentDetailsFragment.generalInfoItems.set(9, item);

//                    Log.e("Debug", "Upload limit");

                // Download limit
                item = TorrentDetailsFragment.generalInfoItems.get(10);
                item.setValue(json2.getString(TorrentDetailsFragment.TAG_DOWNLOAD_LIMIT));
                TorrentDetailsFragment.generalInfoItems.set(10, item);

//                    Log.e("Debug", "Download limit");

                if (MainActivity.qb_version.equals("3.2.x")) {

                    // Creation date
                    item = TorrentDetailsFragment.generalInfoItems.get(1);
                    item.setValue(json2.getString(TorrentDetailsFragment.TAG_CREATION_DATE));
                    TorrentDetailsFragment.generalInfoItems.set(1, item);

                    // Total wasted
                    item = TorrentDetailsFragment.generalInfoItems.get(3);
                    item.setValue(Common.calculateSize(item.getValue()).replace(",", "."));
                    TorrentDetailsFragment.generalInfoItems.set(3, item);

                    // Total uploaded
                    item = TorrentDetailsFragment.generalInfoItems.get(4);
                    item.setValue(Common.calculateSize(item.getValue()).replace(",", "."));
                    TorrentDetailsFragment.generalInfoItems.set(4, item);

                    // Total downloaded
                    item = TorrentDetailsFragment.generalInfoItems.get(5);
                    item.setValue(Common.calculateSize(item.getValue()).replace(",", "."));
                    TorrentDetailsFragment.generalInfoItems.set(5, item);

                    // Time elapsed
                    item = TorrentDetailsFragment.generalInfoItems.get(6);
                    item.setValue(Common.secondsToEta(item.getValue()).replace(",", "."));
                    TorrentDetailsFragment.generalInfoItems.set(6, item);

                    // Upload limit
                    item = TorrentDetailsFragment.generalInfoItems.get(9);

                    if (!item.getValue().equals("-1")) {

                        item.setValue(Common.calculateSize(item.getValue()) + "/s");

                    } else {
                        item.setValue("∞");
                    }

                    TorrentDetailsFragment.generalInfoItems.set(9, item);


                    // Download limit
                    item = TorrentDetailsFragment.generalInfoItems.get(10);

                    if (!item.getValue().equals("-1")) {

                        item.setValue(Common.calculateSize(item.getValue()) + "/s");

                    } else {
                        item.setValue("∞");
                    }

                    TorrentDetailsFragment.generalInfoItems.set(10, item);

                }

//                    Log.e("Debug", "FIN");



            }

        } catch (Exception e) {

            Log.e("Debug", "GeneralInfoTask: " + e.toString());

        }


        return TorrentDetailsFragment.generalInfoItems;

    }

    @Override
    protected void onPostExecute(ArrayList<GeneralInfoItem> items) {

//        Log.d("Debug", "onPostExecute");
//        Log.d("Debug", "onPostExecute - GeneralInfo.size: " + items.size());

        try {
            TorrentDetailsFragment.generalInfoAdapter.refreshGeneralInfo(items);
        } catch (Exception e) {
            Log.e("Debug", e.toString());
        }


    }

}

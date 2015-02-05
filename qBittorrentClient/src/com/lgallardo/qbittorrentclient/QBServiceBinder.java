/*******************************************************************************
 * Copyright (c) 2015 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/

package com.lgallardo.qbittorrentclient;

import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class QBServiceBinder extends Binder {

    protected static HashMap<String, Torrent> unCompletedTorrents, completedTorrents, torrentHashMap;
    private static String[] params = new String[2];
    QBServiceListener listener;
    private Handler handler;

    QBServiceBinder() {

        String state = "all";

        // Get Settings thr params?

        params[0] = "json/torrents";

        params[1] = state;

        Log.i("Binder", "Binder created");

    }

    void getTorrentList(QBServiceListener listener) {

        this.listener = listener;

        handler = new Handler();
        handler.postDelayed(m_Runnable, 10000);


    }

    class FetchTorrentListTask extends AsyncTask<String, Integer, Torrent[]> {

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
        QBServiceListener listener;


        FetchTorrentListTask(QBServiceListener listener) {
            this.listener = listener;
        }

        @Override
        protected Torrent[] doInBackground(String... params) {

            String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, eta, uploadSpeed, downloadSpeed;

            Torrent[] torrents = null;

            // Get settings
            //getSettings();
            String hostname, subfolder, protocol, username, password;
            int port, connection_timeout, data_timeout;
            JSONParser jParser;

            hostname = "10.10.50.63";
            subfolder = "";
            protocol = "http";
            port = 8080;
            username = "admin";
            password = "adminadmin";
            connection_timeout = 5;
            data_timeout = 8;

            int httpStatusCode = 0;

            Log.i("Binder", "Getting torrents");

            try {
                // Creating new JSON Parser
                jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

                JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);

                if (jArray != null) {

                    torrents = new Torrent[jArray.length()];

//                    tivity.names = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        name = json.getString(TAG_NAME);
                        size = json.getString(TAG_SIZE).replace(",", ".");
                        progress = String.format("%.2f", json.getDouble(TAG_PROGRESS) * 100) + "%";
                        progress = progress.replace(",", ".");
                        info = "";
                        state = json.getString(TAG_STATE);
                        hash = json.getString(TAG_HASH);
                        ratio = json.getString(TAG_RATIO).replace(",", ".");
                        leechs = json.getString(TAG_NUMLEECHS);
                        seeds = json.getString(TAG_NUMSEEDS);
                        priority = json.getString(TAG_PRIORITY);
                        eta = json.getString(TAG_ETA);
                        downloadSpeed = json.getString(TAG_DLSPEED);
                        uploadSpeed = json.getString(TAG_UPSPEED);

                        torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, leechs, seeds, priority, eta, downloadSpeed, uploadSpeed);

//                        MainActivity.names[i] = name;

                        // Get torrent generic properties

                        try {
                            // Calculate total downloaded
                            Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                            String sizeUnit = size.substring(size.indexOf(" "), size.length());

                            torrents[i].setDownloaded(String.format("%.1f", sizeScalar * json.getDouble(TAG_PROGRESS)).replace(",", ".") + sizeUnit);

                        } catch (Exception e) {
                            torrents[i].setDownloaded(size);
                        }

                        // Info
                        torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + torrents[i].getEta());

                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
                torrents = null;
                Log.e("JSONParserStatusCodeException SERVICE", e.toString());

            } catch (Exception e) {
                torrents = null;
                Log.e("Binder:", e.toString());
            }

            return torrents;

        }

        @Override
        protected void onPostExecute(Torrent[] torrents) {

            Iterator it;

            // This is needed if you want to share the to the application
            if (listener != null && torrents != null) {
                listener.updateTorrentList(torrents);
            }


            // Check completed torrents and notify them

            if (unCompletedTorrents == null) {
                unCompletedTorrents = new HashMap<String, Torrent>();
            }


            if (completedTorrents == null) {
                completedTorrents = new HashMap<String, Torrent>();
            }

            if (torrents != null) {

                torrentHashMap = new HashMap<String, Torrent>();

                // Check torrents
                for (int i = 0; i < torrents.length; i++) {

                    // Make torrent hashmap

                    torrentHashMap.put(torrents[i].getHash(), torrents[i]);

                    Torrent torrent;

                    torrent = (Torrent) unCompletedTorrents.get(torrents[i].getHash());


                    if (torrent == null) {

                        // Torrent not being checked and is uncompleted
                        if (!(torrents[i].getPercentage().equals("100"))) {

                            // Add to uncompleted torrents
                            unCompletedTorrents.put(torrents[i].getHash(), torrents[i]);
                        }
                    } else {
                        // Torrent is being checked and completed
                        if ((torrents[i].getPercentage().equals("100"))) {

                            // remove it from uncompleted torrents
                            unCompletedTorrents.remove(torrents[i].getHash());

                            // Add to completed torrents
                            completedTorrents.put(torrents[i].getHash(), torrents[i]);


                        } else {
                            Log.i("Not yet", torrents[i].getFile() + " - " + torrents[i].getPercentage());
                        }

                    }

                }

                // Notify completed torrents

                if (!completedTorrents.isEmpty()) {
                    Log.i("Completed", "Torrent(s) download completed");

                    listener.notifyCompleted(completedTorrents);

                }

                // Notify individually and remove form completed list
                it = completedTorrents.entrySet().iterator();
                while (it.hasNext()) {

                    HashMap.Entry pairs = (HashMap.Entry) it.next();

                    Torrent t = (Torrent) pairs.getValue();

                    Log.i("Completed", t.getFile() + " - completed");

                    // Remove it
                    completedTorrents.remove(pairs.getKey());

                    it.remove(); // avoids a ConcurrentModificationException
                }


//                // Check deleted torrents for removing on checking list
//                it = unCompletedTorrents.entrySet().iterator();
//                while (it.hasNext()) {
//
//                    HashMap.Entry pairs = (HashMap.Entry) it.next();
//
//                    if (!torrentHashMap.containsKey(pairs.getKey())) {
//                        unCompletedTorrents.remove(pairs.getKey());
//                    }
//
//                    it.remove(); // avoids a ConcurrentModificationException
//                }
            }
        }

    }

    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {

            new FetchTorrentListTask(listener).execute(params);

            Log.i("Binder", "FetchTorrentListTask called");

            handler.postDelayed(m_Runnable, 10000);
        }

    };// runnable


}

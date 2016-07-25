/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TorrentDetailsFragment extends Fragment {

    // TAGS
    protected static final String TAG_SAVE_PATH = "save_path";
    protected static final String TAG_CREATION_DATE = "creation_date";
    protected static final String TAG_COMMENT = "comment";
    protected static final String TAG_TOTAL_WASTED = "total_wasted";
    protected static final String TAG_TOTAL_UPLOADED = "total_uploaded";
    protected static final String TAG_TOTAL_DOWNLOADED = "total_downloaded";
    protected static final String TAG_TIME_ELAPSED = "time_elapsed";
    protected static final String TAG_NB_CONNECTIONS = "nb_connections";
    protected static final String TAG_SHARE_RATIO = "share_ratio";
    protected static final String TAG_UPLOAD_LIMIT = "up_limit";
    protected static final String TAG_DOWNLOAD_LIMIT = "dl_limit";

    // TODO: Delete trackers
    static ArrayList<GeneralInfoItem> generalInfoItems;


    // Recycler view for files and trackers
    public static ContentFilesRecyclerViewAdapter rAdapter;
    public static TrackersRecyclerViewAdapter trackerAdapter;
    public static GeneralInfoRecyclerViewAdapter generalInfoAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerView mRecyclerViewTrackers;
    protected RecyclerView mRecyclerViewGeneralInfo;

    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private RecyclerView.LayoutManager mLayoutManagerTrackers;            // Declaring Layout Manager as a linear layout manager
    private RecyclerView.LayoutManager mLayoutManagerGeneralInfo;            // Declaring Layout Manager as a linear layout manager


    // Torrent variables
    String name, info, hash, ratio, size, progress, state, leechs, seeds, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
            totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded, eta, downloadSpeed, uploadSpeed,
            percentage = "", addedOn, completionOn, label;

    static String hashToUpdate;

    String url;
    int position;
    JSONObject json2;

    private String qbQueryString = "query";
    private Torrent torrent;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshListener refreshListener;

    // AdView for ads
    private AdView adView;

    public static int fileContentRowPosition;


    public TorrentDetailsFragment() {
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Tell the host activity that your fragment has menu options that it
        // wants to add/replace/delete using the onCreateOptionsMenu method.
        setHasOptionsMenu(true);


        View rootView;

        if (MainActivity.qb_version.equals("3.2.x")) {
            rootView = inflater.inflate(R.layout.torrent_details, container, false);
        } else {
            rootView = inflater.inflate(R.layout.torrent_details_old, container, false);
        }


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.RecyclerViewContentFiles); // Assigning the RecyclerView Object to the xml View
        rAdapter = new ContentFilesRecyclerViewAdapter((MainActivity) getActivity(), getActivity(), new ArrayList<TorrentDetailsItem>());
        rAdapter.notifyDataSetChanged();

        mRecyclerViewTrackers = (RecyclerView) rootView.findViewById(R.id.RecyclerViewTrackers); // Assigning the RecyclerView Object to the xml View
        trackerAdapter = new TrackersRecyclerViewAdapter((MainActivity) getActivity(), getActivity(), new ArrayList<TorrentDetailsItem>());
        trackerAdapter.notifyDataSetChanged();

        mRecyclerViewGeneralInfo = (RecyclerView) rootView.findViewById(R.id.RecyclerViewGeneralInfo); // Assigning the RecyclerView Object to the xml View
        generalInfoAdapter = new GeneralInfoRecyclerViewAdapter((MainActivity) getActivity(), getActivity(), new ArrayList<GeneralInfoItem>());
        generalInfoAdapter.notifyDataSetChanged();


        if (mRecyclerView == null) {
            Log.d("Debug", "mRecyclerView is null");
        }

        if (rAdapter == null) {
            Log.d("Debug", "rAdapter is null");
        }


        try {
            mRecyclerView.setAdapter(rAdapter);
            mRecyclerViewTrackers.setAdapter(trackerAdapter);
            mRecyclerViewGeneralInfo.setAdapter(generalInfoAdapter);

            mLayoutManager = new LinearLayoutManager(rootView.getContext());                 // Creating a layout Manager
            mLayoutManagerTrackers = new LinearLayoutManager(rootView.getContext());                 // Creating a layout Manager
            mLayoutManagerGeneralInfo = new LinearLayoutManager(rootView.getContext());                 // Creating a layout Manager

            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
            mRecyclerViewTrackers.setLayoutManager(mLayoutManagerTrackers);                 // Setting the layout Manager
            mRecyclerViewGeneralInfo.setLayoutManager(mLayoutManagerGeneralInfo);                 // Setting the layout Manager
        } catch (Exception e) {
            Log.e("Debug", e.toString());
        }

        // TODO: Check if this can be removed
        registerForContextMenu(mRecyclerView);
//        registerForContextMenu(mRecyclerViewTrackers);
//        registerForContextMenu(mRecyclerViewGeneralInfo);


        // Get Refresh Listener
        refreshListener = (RefreshListener) getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.details_refresh_layout);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshListener.swipeRefresh();
                }
            });
        }

        // Hide herderInfo and title in phone's view
        if (getActivity().findViewById(R.id.one_frame) != null && MainActivity.headerInfo != null) {
            MainActivity.headerInfo.setVisibility(View.GONE);

            ((MainActivity) getActivity()).setTitle("");
        }

        savePath = "";
        creationDate = "";
        comment = "";
        uploadRateLimit = "";
        downloadRateLimit = "";
        totalWasted = "";
        totalUploaded = "";
        totalDownloaded = "";
        timeElapsed = "";
        nbConnections = "";
        shareRatio = "";

        try {

            if (savedInstanceState != null) {

                // Get saved values
                name = savedInstanceState.getString("torrentDetailName", "");
                size = savedInstanceState.getString("torrentDetailSize", "");
                hash = savedInstanceState.getString("torrentDetailHash", "");
                ratio = savedInstanceState.getString("torrentDetailRatio", "");
                state = savedInstanceState.getString("torrentDetailState", "");
                leechs = savedInstanceState.getString("torrentDetailLeechs", "");
                seeds = savedInstanceState.getString("torrentDetailSeeds", "");
                progress = savedInstanceState.getString("torrentDetailProgress", "");
                priority = savedInstanceState.getString("torrentDetailPriority", "");
                eta = savedInstanceState.getString("torrentDetailEta", "");
                uploadSpeed = savedInstanceState.getString("torrentDetailUploadSpeed", "");
                downloadSpeed = savedInstanceState.getString("torrentDetailDownloadSpeed", "");
                downloaded = savedInstanceState.getString("torrentDetailDownloaded", "");
                addedOn = savedInstanceState.getString("torrentDetailsAddedOn", "");
                completionOn = savedInstanceState.getString("torrentDetailsCompletionOn", "");
                label = savedInstanceState.getString("torrentDetailsLabel", "");
                hashToUpdate = hash;

                // Only for Pro version
                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
                    int index = progress.indexOf(".");

                    if (index == -1) {
                        index = progress.indexOf(",");

                        if (index == -1) {
                            index = progress.length();
                        }
                    }

                    percentage = progress.substring(0, index);
                }

            } else {

                // Get values from current activity
                name = this.torrent.getFile();
                size = this.torrent.getSize();
                hash = this.torrent.getHash();
                ratio = this.torrent.getRatio();
                state = this.torrent.getState();
                leechs = this.torrent.getLeechs();
                seeds = this.torrent.getSeeds();
                progress = this.torrent.getProgress();
                priority = this.torrent.getPriority();
                eta = this.torrent.getEta();
                uploadSpeed = this.torrent.getUploadSpeed();
                downloadSpeed = this.torrent.getDownloadSpeed();
                downloaded = this.torrent.getDownloaded();
                addedOn = this.torrent.getAddedOn();
                completionOn = this.torrent.getCompletionOn();
                label = this.torrent.getLabel();

                hashToUpdate = hash;

                // Only for Pro version
                if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
                    int index = this.torrent.getProgress().indexOf(".");

                    if (index == -1) {
                        index = this.torrent.getProgress().indexOf(",");

                        if (index == -1) {
                            index = this.torrent.getProgress().length();
                        }
                    }

                    percentage = this.torrent.getProgress().substring(0, index);
                }
            }

            TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
            TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
            TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
            TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
            TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
            TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
            TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
            TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
            TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);
            TextView etaTextView = (TextView) rootView.findViewById(R.id.torrentEta);
            TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentUploadSpeed);
            TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentDownloadSpeed);

            CheckBox sequentialDownloadCheckBox;
            CheckBox firstLAstPiecePrioCheckBox;

            nameTextView.setText(name);
            ratioTextView.setText(ratio);
            stateTextView.setText(state);
            leechsTextView.setText(leechs);
            seedsTextView.setText(seeds);
            progressTextView.setText(progress);
            hashTextView.setText(hash);
            etaTextView.setText(eta);
            priorityTextView.setText(priority);


            if (MainActivity.qb_version.equals("3.2.x")) {
                sequentialDownloadCheckBox = (CheckBox) rootView.findViewById(R.id.torrentSequentialDownload);
                firstLAstPiecePrioCheckBox = (CheckBox) rootView.findViewById(R.id.torrentFirstLastPiecePrio);

                sequentialDownloadCheckBox.setChecked(this.torrent.getSequentialDownload());
                firstLAstPiecePrioCheckBox.setChecked(this.torrent.getisFirstLastPiecePrio());

                TextView addedOnTextView = (TextView) rootView.findViewById(R.id.torrentAddedOn);
                TextView completionOnTextView = (TextView) rootView.findViewById(R.id.torrentCompletionOn);
                TextView labelTextView = (TextView) rootView.findViewById(R.id.torrentLabel);

                if (addedOn != null && !(addedOn.equals("null")) && !(addedOn.equals("4294967295"))) {
                    addedOnTextView.setText(Common.timestampToDate(addedOn));
                } else {
                    addedOnTextView.setText("");
                }

                Log.d("Debug", "completionOn: " + completionOn);

                if (completionOn != null && !(completionOn.equals("null")) && !(completionOn.equals("4294967295"))) {
                    completionOnTextView.setText(Common.timestampToDate(completionOn));
                } else {
                    completionOnTextView.setText("");
                }

                if (label != null && !(label.equals("null"))) {
                    labelTextView.setText(label);
                } else {
                    labelTextView.setText("");
                }


            }

            // Set Downloaded vs Total size
            sizeTextView.setText(downloaded + " / " + size);

            // Only for Pro version
            if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
                downloadSpeedTextView.setText(Character.toString('\u2193') + " " + downloadSpeed);
                uploadSpeedTextView.setText(Character.toString('\u2191') + " " + uploadSpeed);

                // Set progress bar
                ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

                progressBar.setProgress(Integer.parseInt(percentage));
                percentageTV.setText(percentage + "%");
            } else {
                downloadSpeedTextView.setText(downloadSpeed);
                uploadSpeedTextView.setText(uploadSpeed);
            }

            nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);

            // Set status icon
            if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
            }

            if ("stalledUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);
            }

            if ("stalledDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);
            }

            if ("downloading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
            }

            if ("uploading".equals(state) || "forcedUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
            }

            if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
            }

            if ("checkingDL".equals(state) || "checkingUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);
            }

            if ("error".equals(state) || "missingFiles".equals(state) || "unknown".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
            }


            // Get Content files in background
            ContentFileTask cft = new ContentFileTask();
            cft.execute(new String[]{hash});


            // Get trackers in background
            TrackersTask tt = new TrackersTask();
            tt.execute(new String[]{hash});

            // Get General info labels
            generalInfoItems = new ArrayList<GeneralInfoItem>();

            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_save_path), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_created_date), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_comment), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_wasted), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_uploaded), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_downloaded), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_time_elapsed), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_num_connections), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_share_ratio), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_upload_rate_limit), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_download_rate_limit), null, GeneralInfoItem.GENERALINFO, "generalInfo"));

            // Get general info in background
            GeneralInfoTask git = new GeneralInfoTask();
            git.execute(new String[]{hash});


        } catch (Exception e) {
            Log.e("Debug", "TorrentDetailsFragment - onCreateView: " + e.toString());
        }

        if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {
            // Load banner
            loadBanner();
        }

        return rootView;
    }

    public void updateDetails(Torrent torrent) {

        Log.d("Debug", "Updating details");

        try {

            // Hide herderInfo in phone's view
            if (getActivity().findViewById(R.id.one_frame) != null) {
                MainActivity.headerInfo.setVisibility(View.GONE);
            }

            // Get values from current activity
            name = torrent.getFile();
            size = torrent.getSize();
            hash = torrent.getHash();
            ratio = torrent.getRatio();
            state = torrent.getState();
            leechs = torrent.getLeechs();
            seeds = torrent.getSeeds();
            progress = torrent.getProgress();
            priority = torrent.getPriority();
            eta = torrent.getEta();
            uploadSpeed = torrent.getUploadSpeed();
            downloadSpeed = torrent.getDownloadSpeed();
            downloaded = torrent.getDownloaded();
            addedOn = torrent.getAddedOn();
            completionOn = torrent.getCompletionOn();
            label = torrent.getLabel();

            int index = torrent.getProgress().indexOf(".");

            if (index == -1) {
                index = torrent.getProgress().indexOf(",");

                if (index == -1) {
                    index = torrent.getProgress().length();
                }
            }

            percentage = torrent.getProgress().substring(0, index);


            FragmentManager fragmentManager = getFragmentManager();

            TorrentDetailsFragment detailsFragment = null;

            if (getActivity().findViewById(R.id.one_frame) != null) {
                detailsFragment = (TorrentDetailsFragment) fragmentManager.findFragmentByTag("firstFragment");
            } else {
                detailsFragment = (TorrentDetailsFragment) fragmentManager.findFragmentByTag("secondFragment");
            }

            View rootView = detailsFragment.getView();

            TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
            TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
            TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
            TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
            TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
            TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
            TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
            TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
            TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);

            TextView etaTextView = (TextView) rootView.findViewById(R.id.torrentEta);
            TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentUploadSpeed);
            TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentDownloadSpeed);

            CheckBox sequentialDownloadCheckBox;
            CheckBox firstLAstPiecePrioCheckBox;

            nameTextView.setText(name);
            ratioTextView.setText(ratio);
            stateTextView.setText(state);
            leechsTextView.setText(leechs);
            seedsTextView.setText(seeds);
            progressTextView.setText(progress);
            hashTextView.setText(hash);
            priorityTextView.setText(priority);
            etaTextView.setText(eta);


            if (MainActivity.qb_version.equals("3.2.x")) {
                sequentialDownloadCheckBox = (CheckBox) rootView.findViewById(R.id.torrentSequentialDownload);
                firstLAstPiecePrioCheckBox = (CheckBox) rootView.findViewById(R.id.torrentFirstLastPiecePrio);

                sequentialDownloadCheckBox.setChecked(torrent.getSequentialDownload());
                firstLAstPiecePrioCheckBox.setChecked(torrent.getisFirstLastPiecePrio());

                TextView addedOnTextView = (TextView) rootView.findViewById(R.id.torrentAddedOn);
                TextView completionOnTextView = (TextView) rootView.findViewById(R.id.torrentCompletionOn);

                TextView labelTextView = (TextView) rootView.findViewById(R.id.torrentLabel);


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

                if (addedOn != null && !(addedOn.equals("null")) && !(addedOn.equals("4294967295"))) {
                    addedOnTextView.setText(Common.timestampToDate(addedOn));
                } else {
                    addedOnTextView.setText("");
                }

                if (completionOn != null && !(completionOn.equals("null")) && !(completionOn.equals("4294967295"))) {
                    completionOnTextView.setText(Common.timestampToDate(completionOn));
                } else {
                    completionOnTextView.setText("");
                }

                if (label != null && !(label.equals("null"))) {
                    labelTextView.setText(label);
                } else {
                    labelTextView.setText("");
                }


            }

            // Set Downloaded vs Total size
            sizeTextView.setText(downloaded + " / " + size);

            // Only for Pro version
            if (MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
                downloadSpeedTextView.setText(Character.toString('\u2193') + " " + downloadSpeed);
                uploadSpeedTextView.setText(Character.toString('\u2191') + " " + uploadSpeed);

                // Set progress bar
                ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

                progressBar.setProgress(Integer.parseInt(percentage));
                percentageTV.setText(percentage + "%");


            } else {
                downloadSpeedTextView.setText(downloadSpeed);
                uploadSpeedTextView.setText(uploadSpeed);
            }


            nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);

            if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
            }

            if ("stalledUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);
            }

            if ("stalledDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);
            }

            if ("downloading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
            }

            if ("uploading".equals(state) || "forcedUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
            }

            if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
            }

            if ("checkingDL".equals(state) || "checkingUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);
            }

            if ("error".equals(state) || "missingFiles".equals(state) || "unknown".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
            }

//            // Get Content files in background
            ContentFileTask cft = new ContentFileTask();
            cft.execute(new String[]{hash});

            // Get trackers in background
            TrackersTask tt = new TrackersTask();
            tt.execute(new String[]{hash});

            // Get General info labels
            generalInfoItems = new ArrayList<GeneralInfoItem>();

            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_save_path), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_created_date), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_comment), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_wasted), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_uploaded), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_total_downloaded), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_time_elapsed), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_num_connections), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_share_ratio), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_upload_rate_limit), null, GeneralInfoItem.GENERALINFO, "generalInfo"));
            generalInfoItems.add(new GeneralInfoItem(getString(R.string.torrent_details_download_rate_limit), null, GeneralInfoItem.GENERALINFO, "generalInfo"));


            // Get General info in background;
            GeneralInfoTask git = new GeneralInfoTask();
            git.execute(new String[]{hash});

        } catch (Exception e) {

            Log.e("Debug", "TorrentDetailsFragment - onCreateView: " + e.toString());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("torrentDetailName", name);
        outState.putString("torrentDetailSize", size);
        outState.putString("torrentDetailHash", hash);
        outState.putString("torrentDetailRatio", ratio);
        outState.putString("torrentDetailState", state);
        outState.putString("torrentDetailLeechs", leechs);
        outState.putString("torrentDetailSeeds", seeds);
        outState.putString("torrentDetailProgress", progress);
        outState.putString("torrentDetailPriority", priority);
        outState.putString("torrentDetailEta", eta);
        outState.putString("torrentDetailUploadSpeed", uploadSpeed);
        outState.putString("torrentDetailDownloadSpeed", downloadSpeed);
        outState.putString("torrentDetailDownloaded", downloaded);
        outState.putString("torrentDetailsAddedOn", addedOn);
        outState.putString("torrentDetailsCompletionOn", completionOn);

    }

    // @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {

    }

    // @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {

            menu.findItem(R.id.action_resume_all).setVisible(false);
            menu.findItem(R.id.action_pause_all).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_refresh).setVisible(false);
            } else {
                menu.findItem(R.id.action_refresh).setVisible(true);
            }

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_sort_menu).setVisible(false);
            } else {
                menu.findItem(R.id.action_sort_menu).setVisible(true);
            }

            menu.findItem(R.id.action_resume).setVisible(true);
            menu.findItem(R.id.action_pause).setVisible(true);
            menu.findItem(R.id.action_priority_menu).setVisible(true);
            menu.findItem(R.id.action_increase_prio).setVisible(true);
            menu.findItem(R.id.action_decrease_prio).setVisible(true);
            menu.findItem(R.id.action_max_prio).setVisible(true);
            menu.findItem(R.id.action_min_prio).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_delete_drive).setVisible(true);
            menu.findItem(R.id.action_download_rate_limit).setVisible(true);
            menu.findItem(R.id.action_upload_rate_limit).setVisible(true);
            menu.findItem(R.id.action_recheck).setVisible(true);

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_search).setVisible(false);
            } else {
                menu.findItem(R.id.action_search).setVisible(true);
            }

//            Log.d("Debug", "qb_version: " + MainActivity.qb_version);

            if (MainActivity.qb_version.equals("3.2.x")) {
                menu.findItem(R.id.action_first_last_piece_prio).setVisible(true);
                menu.findItem(R.id.action_sequential_download).setVisible(true);
                menu.findItem(R.id.action_toggle_alternative_rate).setVisible(true);
                // TODO: Change add_tracker to true
//                menu.findItem(R.id.action_add_tracker).setVisible(false);
                menu.findItem(R.id.action_label_menu).setVisible(true);
                menu.findItem(R.id.action_set_label).setVisible(true);
                menu.findItem(R.id.action_delete_label).setVisible(true);

                if (Integer.parseInt(MainActivity.qb_api) < 8) {
                    menu.findItem(R.id.action_delete_label).setVisible(false);
                }

                // Set Alternate Speed limit state
                if (MainActivity.alternative_speeds) {
                    menu.findItem(R.id.action_toggle_alternative_rate).setChecked(true);
                } else {
                    menu.findItem(R.id.action_toggle_alternative_rate).setChecked(true);
                }


            } else {
                menu.findItem(R.id.action_first_last_piece_prio).setVisible(false);
                menu.findItem(R.id.action_sequential_download).setVisible(false);
                menu.findItem(R.id.action_toggle_alternative_rate).setVisible(false);
                // TODO: Change add_tracker to true
//                menu.findItem(R.id.action_add_tracker).setVisible(false);
                menu.findItem(R.id.action_label_menu).setVisible(false);
                menu.findItem(R.id.action_set_label).setVisible(false);
                menu.findItem(R.id.action_delete_label).setVisible(false);

            }

        }
    }

    // Load Banner method
    public void loadBanner() {

        // Get the adView.
        adView = (AdView) getActivity().findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }

    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }


    /**
     * *
     * Method for Setting the Height of the ListView dynamically. Hack to fix
     * the issue of not showing all the items of the ListView when placed inside
     * a ScrollView
     * **
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            long numOfLines = 1;
            view = listAdapter.getView(i, view, listView);

            if (i == 0) {
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);

            TextView file = (TextView) view.findViewById(R.id.file);
            TextView percentage = (TextView) view.findViewById(R.id.percentage);
            ProgressBar progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);

            if (view.getMeasuredWidth() > desiredWidth) {

                double viewWidthLong = Double.valueOf(view.getMeasuredWidth());
                double desiredWidthLong = Double.valueOf(desiredWidth);

                numOfLines = Math.round(viewWidthLong / desiredWidthLong) + 1;

                totalHeight += (file.getMeasuredHeight() * numOfLines) + percentage.getMeasuredHeight() + progressBar1.getMeasuredHeight();

            } else {
                totalHeight += view.getMeasuredHeight();
            }

        }

        LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}

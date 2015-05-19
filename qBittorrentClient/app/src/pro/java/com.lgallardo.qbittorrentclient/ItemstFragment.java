/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Set;

public class ItemstFragment extends ListFragment {

    static public ActionMode mActionMode;
    public int nr = 0;
    int secondContainer;
    com.lgallardo.qbittorrentclient.TorrentDetailsFragment detailsFragment;
    private RecyclerView recyclerView;
    private com.lgallardo.qbittorrentclient.RefreshListener refreshListener;
    private View.OnClickListener originalListener;

    public static SwipeRefreshLayout mSwipeRefreshLayout;

    public ItemstFragment() {
    }

    public int getSecondFragmentContainer() {

        return this.secondContainer;

    }

    public void setSecondFragmentContainer(int container) {

        this.secondContainer = container;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Tell the host activity that your fragment has menu options that it
        // wants to add/replace/delete using the onCreateOptionsMenu method.
        setHasOptionsMenu(true);

        // Get Refresh Listener
        refreshListener = (com.lgallardo.qbittorrentclient.RefreshListener) getActivity();

        View rootView = inflater.inflate(R.layout.activity_main_original, container, false);


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.primary,                R.color.primary_dark, R.color.primary_text);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                com.lgallardo.qbittorrentclient.MainActivity.refreshCurrent();
                Log.d("Debug", "Swipe!");

                refreshListener.swipeRefresh();

            }
        });





//        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewList);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            // Get adapter
            final TorrentListAdapter mAdapter = (TorrentListAdapter) this.getListAdapter();
            getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                private int nr = 0;

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {


                    if(com.lgallardo.qbittorrentclient.MainActivity.listViewRefreshing) {
                        Log.d("Debug", "onViewCreated - listview is refreshing pal");
                    }
                    else{
                        Log.d("Debug", "onViewCreated - listview is NOT refreshing dude");
                    }


                    if (checked) {
                        nr++;
                        mAdapter.setNewSelection(position, checked);

                    } else {
                        nr--;
                        mAdapter.removeSelection(position);
                    }
//                    mode.setTitle(nr + " selected");
                    mode.setTitle("" + nr);


                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    nr = 0;
                    MenuInflater inflater = getActivity().getMenuInflater();
                    inflater.inflate(R.menu.main_contextual_action_bar, menu);

                    mSwipeRefreshLayout.setEnabled(false);

                    ItemstFragment.mActionMode = actionMode;

                    return true;

                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    if (com.lgallardo.qbittorrentclient.MainActivity.qb_version.equals("3.2.x")) {
                        menu.findItem(R.id.action_firts_last_piece_prio).setVisible(true);
                        menu.findItem(R.id.action_sequential_download).setVisible(true);
                    } else {
                        menu.findItem(R.id.action_firts_last_piece_prio).setVisible(false);
                        menu.findItem(R.id.action_sequential_download).setVisible(false);
                    }
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    AlertDialog.Builder builder;
                    AlertDialog dialog;
                    String hashes = null;
                    final String hashesStr;

                    // Get MainActivity
                    final com.lgallardo.qbittorrentclient.MainActivity m = (com.lgallardo.qbittorrentclient.MainActivity) getActivity();

                    // Get hashes
                    for (int i = 0; mAdapter.getCount() > i; i++) {


                        if (mAdapter.isPositionChecked(i)) {

                            if (hashes == null) {
                                hashes = mAdapter.getData()[i].getHash();
                            } else {
                                hashes = hashes + "|" + mAdapter.getData()[i].getHash();
                            }

                        }

                    }

                    hashesStr = hashes;

                    switch (item.getItemId()) {

                        case R.id.action_pause:
                            m.pauseSelectedTorrents(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;


                        case R.id.action_resume:
                            m.startSelectedTorrents(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_delete:

                            if (!getActivity().isFinishing()) {
                                builder = new AlertDialog.Builder(getActivity());

                                // Message
                                builder.setMessage(R.string.dm_deleteSelectedTorrents).setTitle(R.string.dt_deleteSelectedTorrents);

                                // Cancel
                                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User canceled the dialog
                                    }
                                });

                                // Ok
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User accepted the dialog
                                        m.deleteSelectedTorrents(hashesStr);

                                    }
                                });

                                // Create dialog
                                dialog = builder.create();

                                // Show dialog
                                dialog.show();

                                // Clear selection
                                nr = 0;

                                // Enable SwipeRefresh
                                mSwipeRefreshLayout.setEnabled(true);

                                mAdapter.clearSelection();
                                mode.finish();

                            }

                            return true;
                        case R.id.action_delete_drive:

                            if (!getActivity().isFinishing()) {
                                builder = new AlertDialog.Builder(getActivity());

                                // Message
                                builder.setMessage(R.string.dm_deleteDriveSelectedTorrents).setTitle(R.string.dt_deleteDriveSelectedTorrents);

                                // Cancel
                                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User canceled the dialog
                                    }
                                });

                                // Ok
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User accepted the dialog
                                        m.deleteDriveSelectedTorrents(hashesStr);

                                    }
                                });

                                // Create dialog
                                dialog = builder.create();

                                // Show dialog
                                dialog.show();

                                // Clear selection
                                nr = 0;

                                // Enable SwipeRefresh
                                mSwipeRefreshLayout.setEnabled(true);

                                mAdapter.clearSelection();
                                mode.finish();

                            }

                            return true;

                        case R.id.action_increase_prio:
                            m.increasePrioTorrent(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_decrease_prio:
                            m.decreasePrioTorrent(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_max_prio:
                            m.maxPrioTorrent(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_min_prio:
                            m.minPrioTorrent(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_upload_rate_limit:

                            m.uploadRateLimitDialog(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        case R.id.action_download_rate_limit:

                            m.downloadRateLimitDialog(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;
                        case R.id.action_recheck:

                            m.recheckTorrents(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;
                        case R.id.action_sequential_download:

                            m.toggleSequentialDownload(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;
                        case R.id.action_firts_last_piece_prio:

                            m.toggleFirstLastPiecePrio(hashes);

                            // Clear selection
                            nr = 0;

                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);

                            mAdapter.clearSelection();
                            mode.finish();

                            return true;

                        default:
                            // Enable SwipeRefresh
                            mSwipeRefreshLayout.setEnabled(true);
                            return true;


                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    if (mAdapter != null) {
                        mAdapter.clearSelection();
                    }
                    ItemstFragment.mActionMode = null;
                    mSwipeRefreshLayout.setEnabled(true);


                }
            });

            getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    if(com.lgallardo.qbittorrentclient.MainActivity.listViewRefreshing) {
                        return true;
                    }
                    getListView().setItemChecked(position, !mAdapter.isPositionChecked(position));
                    return false;
                }
            });


        } catch (Exception e) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);

        }

    }


    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        if(!com.lgallardo.qbittorrentclient.MainActivity.listViewRefreshing) {
            ListItemClicked(position);

            Log.d("Debug", "onListItemClick -  not refreshing");
        }
    }

    public void ListItemClicked(int position) {

        ListView lv = this.getListView();

        int count = lv.getCount();


//        if (count == 1 && lv.getItemAtPosition(0).equals(getString(R.string.no_results))) {
//
//            Log.d("Debug", "Torrents not found");
//
//            return;
//        }


        Torrent torrent = com.lgallardo.qbittorrentclient.MainActivity.lines[position];

        if (torrent.getHash().equals(com.lgallardo.qbittorrentclient.TorrentDetailsFragment.hashToUpdate) && getActivity().findViewById(R.id.fragment_container) != null) {

            // Update torrent details

            FragmentManager fragmentManager = getFragmentManager();
            if(!(fragmentManager.findFragmentByTag("secondFragment") instanceof AboutFragment)) {



                detailsFragment = (com.lgallardo.qbittorrentclient.TorrentDetailsFragment) fragmentManager.findFragmentByTag("secondFragment");

                if (detailsFragment != null && torrent != null) {

                    detailsFragment.updateDetails(torrent);
                }
            }
            else{
                newDetailsFragment(position);
            }
        } else {

            newDetailsFragment(position);

        }
    }

    private void newDetailsFragment(int position){

        detailsFragment = new com.lgallardo.qbittorrentclient.TorrentDetailsFragment();

        // Get torrent from MainActivity
        detailsFragment.setTorrent(com.lgallardo.qbittorrentclient.MainActivity.lines[position]);

        detailsFragment.setPosition(position);

        if (detailsFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();

            if (getActivity().findViewById(R.id.one_frame) != null) {
                fragmentManager.beginTransaction().replace(this.getSecondFragmentContainer(), detailsFragment, "firstFragment").addToBackStack("secondFragment").commit();

                // Change toolbar home button behaviour
                originalListener = com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.getToolbarNavigationClickListener();

                com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
                com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
                com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Set default toolbar behaviour
                        ((com.lgallardo.qbittorrentclient.MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                        com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.setDrawerIndicatorEnabled(true);
                        com.lgallardo.qbittorrentclient.MainActivity.drawerToggle.setToolbarNavigationClickListener(originalListener);

                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();
                    }
                });


            } else {
                fragmentManager.beginTransaction().replace(this.getSecondFragmentContainer(), detailsFragment, "secondFragment").addToBackStack("secondFragment").commit();
            }
        }

    }

    // @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.main, menu);
        // super.onCreateOptionsMenu(menu, inflater);


        if (menu != null) {
            menu.findItem(R.id.action_refresh).setVisible(true);
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_resume_all).setVisible(true);
            menu.findItem(R.id.action_pause_all).setVisible(true);
            menu.findItem(R.id.action_add).setVisible(true);

            if (menu.findItem(R.id.action_resume) != null) {
                menu.findItem(R.id.action_resume).setVisible(false);
            }

            if (menu.findItem(R.id.action_pause) != null) {
                menu.findItem(R.id.action_pause).setVisible(false);
            }

            if (menu.findItem(R.id.action_increase_prio) != null) {
                menu.findItem(R.id.action_increase_prio).setVisible(false);
            }

            if (menu.findItem(R.id.action_decrease_prio) != null) {
                menu.findItem(R.id.action_decrease_prio).setVisible(false);
            }

            if (menu.findItem(R.id.action_max_prio) != null) {
                menu.findItem(R.id.action_max_prio).setVisible(false);
            }

            if (menu.findItem(R.id.action_min_prio) != null) {
                menu.findItem(R.id.action_min_prio).setVisible(false);
            }

            if (menu.findItem(R.id.action_delete) != null) {
                menu.findItem(R.id.action_delete).setVisible(false);
            }

            if (menu.findItem(R.id.action_delete_drive) != null) {
                menu.findItem(R.id.action_delete_drive).setVisible(false);
            }

            if (menu.findItem(R.id.action_upload_rate_limit) != null) {
                menu.findItem(R.id.action_upload_rate_limit).setVisible(false);
            }

            if (menu.findItem(R.id.action_download_rate_limit) != null) {
                menu.findItem(R.id.action_download_rate_limit).setVisible(false);
            }

            if (menu.findItem(R.id.action_recheck) != null) {
                menu.findItem(R.id.action_recheck).setVisible(false);
            }

            if (menu.findItem(R.id.action_firts_last_piece_prio) != null) {
                menu.findItem(R.id.action_firts_last_piece_prio).setVisible(false);
            }

            if (menu.findItem(R.id.action_sequential_download) != null) {
                menu.findItem(R.id.action_sequential_download).setVisible(false);
            }

            if (menu.findItem(R.id.action_priority_menu) != null) {
                menu.findItem(R.id.action_priority_menu).setVisible(false);
            }


            menu.findItem(R.id.action_sortby_name).setVisible(true);
            menu.findItem(R.id.action_sortby_eta).setVisible(true);
            menu.findItem(R.id.action_sortby_priority).setVisible(true);
            menu.findItem(R.id.action_sortby_progress).setVisible(true);
            menu.findItem(R.id.action_sortby_ratio).setVisible(true);
            menu.findItem(R.id.action_sortby_downloadSpeed).setVisible(true);
            menu.findItem(R.id.action_sortby_uploadSpeed).setVisible(true);


            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("Name")) {
                menu.findItem(R.id.action_sortby_name).setIcon(R.drawable.ic_stat_completed);

            }


            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("ETA")) {
                menu.findItem(R.id.action_sortby_eta).setIcon(R.drawable.ic_stat_completed);
            }

            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("Priority")) {
                ;
                menu.findItem(R.id.action_sortby_priority).setIcon(R.drawable.ic_stat_completed);
            }

            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("Progress")) {
                menu.findItem(R.id.action_sortby_progress).setIcon(R.drawable.ic_stat_completed);
            }

            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("Ratio")) {

                menu.findItem(R.id.action_sortby_ratio).setIcon(R.drawable.ic_stat_completed);
            }

            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("DownloadSpeed")) {
                menu.findItem(R.id.action_sortby_downloadSpeed).setIcon(R.drawable.ic_stat_completed);
            }

            if (com.lgallardo.qbittorrentclient.MainActivity.sortby.equals("UploadSpeed")) {
                menu.findItem(R.id.action_sortby_uploadSpeed).setIcon(R.drawable.ic_stat_completed);
            }

        }
    }

}

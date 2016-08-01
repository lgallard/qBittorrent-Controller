/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

package com.lgallardo.qbittorrentclient;

/**
 * Created by lgallard on 28/08/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackersRecyclerViewAdapter extends RecyclerView.Adapter<TrackersRecyclerViewAdapter.ViewHolder> {

    // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_TRACKER_ITEM = 1;


    // All items
    public static ArrayList<TorrentDetailsItem> items;

    private static MainActivity mainActivity;
    private Context context;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewFile;
        TextView textViewInfo;
        TextView textViewPriorityInfo;
        TextView textViewPercentage;
        ProgressBar progressBar;


        public ViewHolder(final View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            textViewFile = (TextView) itemView.findViewById(R.id.file);
            textViewInfo = (TextView) itemView.findViewById(R.id.info);
            textViewPriorityInfo = (TextView) itemView.findViewById(R.id.priorityInfo);
            textViewPercentage = (TextView) itemView.findViewById(R.id.percentage);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);

        }


        // In order to track the item position in RecyclerView
        // Handle item click and set the selection
        @Override
        public void onClick(View view) {


            TorrentDetailsItem recyclerItem;


            // Get item
            recyclerItem = items.get(getLayoutPosition());


            // Perform Action
//            Log.d("Debug", "onClicked invoked!");


            // Add tracker
            if (recyclerItem.getAction().equals("addTracker")) {

//                Log.d("Debug", "addTracker");
//                mainActivity.openContextMenu(itemView);

                //notifyDataSetChanged();
            }


        }

    }


    TrackersRecyclerViewAdapter(MainActivity mainActivity, Context context, ArrayList<TorrentDetailsItem> trackerItems) {

        this.mainActivity = mainActivity;
        this.context = context;


        // All items
        TrackersRecyclerViewAdapter.items = new ArrayList<TorrentDetailsItem>();

        // Add items
        TrackersRecyclerViewAdapter.items.addAll(trackerItems);


//        Log.d("Debug", "TrackersRecyclerViewAdapter instantiated");
    }


    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public TrackersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        Log.d("Debug", "onCreateViewHolder invoked");

        //inflate your layout and pass it to view holder
        if (viewType == TYPE_TRACKER_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracker_row, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object

        }

        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(TrackersRecyclerViewAdapter.ViewHolder holder, int position) {

        TorrentDetailsItem item = items.get(position);

//        Log.d("Debug", "onBindViewHolder - item.info: " + item.getInfo());


        if (item.getType() == TorrentDetailsItem.TRACKER) {

            holder.textViewInfo.setText(item.getInfo());
        }


        // Uncomment for long click menu
//        holder.itemView.setLongClickable(true);


    }


    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        // Return the number of items in the list (header + item actions)

//        Log.d("Debug", "getItemCount: " + items.size());

        return items.size();
    }


    // With the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {

//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - items.size(): " + items.size());
//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);

//        if (items.get(position).getType() == TYPE_TRACKER_ITEM) {
//            Log.d("Debug", "ContentFilesRecyclerViewAdapter - TYPE_TRACKER_ITEM");
//            return TYPE_TRACKER_ITEM;
//        }

        // Default
//        Log.d("Debug", "TrackersRecyclerViewAdapter - TYPE_TRACKER_ITEM");
        return TYPE_TRACKER_ITEM;

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    public void refreshTrackers(ArrayList<TorrentDetailsItem> trackerItems) {


        TrackersRecyclerViewAdapter.items = new ArrayList<TorrentDetailsItem>();

        // Add items
//        Log.d("Debug","Tracker size: " + trackerItems.size());
//        TrackersRecyclerViewAdapter.items.addAll(TrackersRecyclerViewAdapter.fileItems);
        TrackersRecyclerViewAdapter.items.addAll(trackerItems);

        // Refresh
        notifyDataSetChanged();

    }


}

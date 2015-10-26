package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lgallard on 10/25/15.
 */

interface DynamicHeight {
    void setContentFileHeight (int position, int height);
}

class MyFileAdapter2 extends RecyclerView.Adapter<MyFileAdapter2.ViewHolder> implements DynamicHeight {


    private Context context;
    public ArrayList<ContentFile> items;

    public MyFileAdapter2(Context context, ArrayList<ContentFile> items) {

        this.context = context;

        this.items = new ArrayList<ContentFile>();

        // Add items
        this.items.addAll(items);


    }


    public void setContentFiles(ArrayList<ContentFile> items) {


        this.items = items;

        Log.d("Debug", "contentFiles size: " + items.size());


    }


    public int getTotalHeight() {

        int totalHeight = 0;

        Log.d("Debug", "Height - size: >" + items.size());

        // Sum all content file heights
        Iterator iterator = items.iterator();

        while (iterator.hasNext()) {

            ContentFile item = (ContentFile) iterator.next();

            Log.d("Debug", "ContentFile Height: " + item.getRecyclerViewItemHeight());
            totalHeight = totalHeight + item.getRecyclerViewItemHeight();

        }

        return totalHeight;

    }


    public void setContentFileHeight(int position, int height) {


        Log.d("Debug", "setContentFile Height: " + height);

        ContentFile cf = items.get(position);
        cf.setRecyclerViewItemHeight(height);

        items.set(position, cf);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // These are the values to be set in contentfile_row.xml

        TextView textViewFile;
        TextView textViewInfo;
        TextView textViewPriorityInfo;
        TextView textViewPercentage;
        ProgressBar progressBarProgressBar;


        public ViewHolder(final View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            itemView.setClickable(true);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            textViewFile = (TextView) itemView.findViewById(R.id.file);
            textViewInfo = (TextView) itemView.findViewById(R.id.info);
            textViewPriorityInfo = (TextView) itemView.findViewById(R.id.priorityInfo);
            textViewPercentage = (TextView) itemView.findViewById(R.id.percentage);
//            progressBarProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);


            Log.d("Debug", "MyFileAdapter2 - ViewHolder completed!");


        }


        // In order to track the item position in RecyclerView
        // Handle item click and set the selection
        @Override
        public void onClick(View view) {

            Log.d("Debug", "MyFileAdapter2 - onClick completed!");

        }

    }

    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public MyFileAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate your layout and pass it to view holder

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentfile_row, parent, false); //Inflating the layout
        ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view


        Log.d("Debug", "MyFileAdapter2 - onCreateViewHolder completed!");

        // Returning the created object
        return vhItem;


    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final MyFileAdapter2.ViewHolder holder, final int position) {


        ContentFile item = items.get(position);


        holder.textViewFile.setText(item.getName());
        holder.textViewInfo.setText(item.getSize());
        holder.textViewPriorityInfo.setText("" + item.getPriority());
//        holder.progressBarProgressBar.setProgress((item.getProgress()).intValue());

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {

                int cellWidth = holder.itemView.getWidth();// this will give you cell width dynamically
                int cellHeight = holder.itemView.getHeight();// this will give you cell height dynamically

                setContentFileHeight(position, cellHeight); //call your iterface hear

                Log.d("Debug", "onBindViewHolder - height >>>>>: " + cellHeight);


            }
        });


        Log.d("Debug", "onBindViewHolder completed!");

    }


    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        // Return the number of items in the list

        Log.d("Debug", "onBindViewHolder - getItemCount: " + items.size());
        return items.size();
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {

        Log.d("Debug", "MyFileAdapter2 - items.size(): " + items.size());
//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);

        return 0;

    }


}



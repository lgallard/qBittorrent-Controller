package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

class torrentListAdapter extends ArrayAdapter<String> {

    private String[] torrentsNames;
    private Torrent[] torrentsData;
    private Context context;

    private static HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public torrentListAdapter(Context context, String[] torrentsNames, Torrent[] torrentsData) {
        // TODO Auto-generated constructor stub
        super(context, R.layout.row, R.id.file, torrentsNames);

        this.context = context;
        this.torrentsNames = torrentsNames;
        this.torrentsData = torrentsData;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub}
        return (torrentsNames != null) ? torrentsNames.length : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = super.getView(position, convertView, parent);

        String state = torrentsData[position].getState();

        TextView info = (TextView) row.findViewById(R.id.info);

        info.setText("" + torrentsData[position].getInfo());

        ImageView icon = (ImageView) row.findViewById(R.id.icon);

        if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
            icon.setImageResource(R.drawable.paused);
        }

        if ("stalledUP".equals(state)) {
            icon.setImageResource(R.drawable.stalledup);
        }

        if ("stalledDL".equals(state)) {
            icon.setImageResource(R.drawable.stalleddl);
        }

        if ("downloading".equals(state)) {
            icon.setImageResource(R.drawable.downloading);
        }

        if ("uploading".equals(state)) {
            icon.setImageResource(R.drawable.uploading);
        }

        if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
            icon.setImageResource(R.drawable.queued);
        }

        row.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light)); //default color

        if (mSelection.get(position) != null) {
            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it blue
        } else {
//            Log.i("torrentListAdapter","size: "+mSelection.size());
//            Log.i("torrentListAdapter","position: "+position);
        }

        return (row);
    }


    public void setNewSelection(int position, boolean value) {

        Log.i("torrentListAdapter", "setNewSelection invoked " + position + " " + value);

        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    public void setNames(String[] names) {
        this.torrentsNames = names;
    }

    public void setData(Torrent[] data) {
        this.torrentsData = data;
    }

    public Torrent[] getData() {
        return this.torrentsData;
    }
}
package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by lgallard on 6/4/15.
 */
public class RSSFeedChannelListAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] rssChannelTitles;

    public RSSFeedChannelListAdapter(Context context, String[] rssChannelTitles) {

        super(context, R.layout.rss_channel_row, R.id.rss_channel_title, rssChannelTitles);

        this.context = context;
        this.rssChannelTitles = rssChannelTitles;

    }


    @Override
    public int getCount() {
        return (rssChannelTitles != null) ? rssChannelTitles.length : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.rss_channel_row, parent, false);

        String rssChannelTitle = rssChannelTitles[position];

        TextView title = (TextView) row.findViewById(R.id.rss_channel_title);
        title.setText(rssChannelTitle);

        return row;
    }
}

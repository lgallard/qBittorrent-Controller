package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lgallard on 6/4/15.
 */
public class RSSFeedChannelListAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> rssChannelTitles;
    private ArrayList<String> rssChannelLinks;
    private ArrayList<String> rssChannelPubDates;

    public RSSFeedChannelListAdapter(Context context, ArrayList<String> rssChannelTitles, ArrayList<String> rssChannelLinks, ArrayList<String> rssChannelPubDates) {

        super(context, R.layout.rss_channel_row, R.id.rss_channel_title, rssChannelTitles);

        this.context = context;
        this.rssChannelTitles = rssChannelTitles;
        this.rssChannelLinks = rssChannelLinks;
        this.rssChannelPubDates = rssChannelPubDates;

    }


    @Override
    public int getCount() {
        return (rssChannelTitles != null) ? rssChannelTitles.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.rss_channel_row, parent, false);

        String rssChannelTitle = rssChannelTitles.get(position);
        String rssChannelLink = rssChannelLinks.get(position);


        TextView title = (TextView) row.findViewById(R.id.rss_channel_title);
        title.setText(rssChannelTitle);

        TextView link = (TextView) row.findViewById(R.id.rss_channel_link);
        link.setText(rssChannelLink);


        return row;
    }

    public ArrayList<String> getRssChannelTitles() {
        return rssChannelTitles;
    }

    public void setRssChannelTitles(ArrayList<String> rssChannelTitles) {
        this.rssChannelTitles = rssChannelTitles;
    }

    private void addChannelTitle(String channelTitle){

        this.rssChannelTitles.add(channelTitle);
    }


    private void addChannelLink(String channelLink){

        this.rssChannelLinks.add(channelLink);
    }

    private void addChannelPubDates(String channelPubDates){

        this.rssChannelLinks.add(channelPubDates);
    }

    public void addChannel(String title, String link){

        this.addChannelTitle(title);
        this.addChannelLink(link);
        this.addChannelPubDates("");

    }




}

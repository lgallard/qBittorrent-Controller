package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lgallard on 6/4/15.
 */
public class RSSFeedChannelListAdapter extends ArrayAdapter<RSSFeed> {

    private Context context;
    public static ArrayList<RSSFeed> rssChannels;


    public RSSFeedChannelListAdapter(Context context, ArrayList<RSSFeed> rssChannels) {

        super(context, R.layout.rss_channel_row, R.id.rss_channel_title, rssChannels);

        this.context = context;
        this.rssChannels = rssChannels;

    }


    @Override
    public int getCount() {
        return (rssChannels != null) ? rssChannels.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.rss_channel_row, parent, false);

        String rssChannelTitle = rssChannels.get(position).getChannelTitle();
        String rssChannelLink = rssChannels.get(position).getChannelLink();
        String rssChannelPubDate = rssChannels.get(position).getChannelPubDate();
        String rssCahnnelNewItems = rssChannels.get(position).getItemCount() + "";


        TextView title = (TextView) row.findViewById(R.id.rss_channel_title);
        title.setText(rssChannelTitle);

        TextView link = (TextView) row.findViewById(R.id.rss_channel_link);
        link.setText(rssChannelLink);


        Date predefined = new Date();

        String dateAsString = "";

        try {

            if(rssChannelPubDate != null) {
                // Tue, 02 Jun 2015 17:37:32
                predefined = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").parse(rssChannelPubDate);

                dateAsString = new SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(predefined);
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }


        TextView pubDate = (TextView) row.findViewById(R.id.rss_channel_pudDate);
        pubDate.setText(dateAsString);


        TextView items = (TextView) row.findViewById(R.id.rss_channel_item_count);
        items.setText(rssCahnnelNewItems);



        return row;
    }



    public void addChannel(RSSFeed rssFeed){

        this.rssChannels.add(rssFeed);

    }


    public ArrayList<RSSFeed> getRssChannels() {
        return rssChannels;
    }

    public void setRssChannels(ArrayList<RSSFeed> rssChannels) {
        RSSFeedChannelListAdapter.rssChannels = rssChannels;
    }
}

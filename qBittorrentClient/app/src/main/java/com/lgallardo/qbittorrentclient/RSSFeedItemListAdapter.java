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
public class RSSFeedItemListAdapter extends ArrayAdapter<RSSFeedItem> {

    private Context context;
    public static ArrayList<RSSFeedItem> items;


    public RSSFeedItemListAdapter(Context context, ArrayList<RSSFeedItem> items) {

        super(context, R.layout.rss_item_row, R.id.rss_item_title, items);

        this.context = context;
        this.items = items;

    }


    @Override
    public int getCount() {
        return (items != null) ? items.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.rss_item_row, parent, false);

        String itemlTitle = items.get(position).getTitle();
        String itemLink = items.get(position).getLink();
        String itemPubDate = items.get(position).getPubDate();


        TextView title = (TextView) row.findViewById(R.id.rss_item_title);
        title.setText(itemlTitle);

        Date predefined = new Date();

        String dateAsString = "";

        try {

            if(itemPubDate != null) {
                // Tue, 02 Jun 2015 17:37:32
                predefined = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(itemPubDate);

                dateAsString = new SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(predefined);
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }



        TextView pubDate = (TextView) row.findViewById(R.id.rss_item_pudDate);
        pubDate.setText(dateAsString);


        return row;
    }



    public void addItem(RSSFeedItem item){

        this.items.add(item);

    }


    public ArrayList<RSSFeedItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<RSSFeedItem> items) {
        RSSFeedItemListAdapter.items = items;
    }
}

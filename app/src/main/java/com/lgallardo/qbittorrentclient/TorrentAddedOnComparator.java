/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TorrentAddedOnComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentAddedOnComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        Date date1 = null;
        Date date2 = null;

        String d1 = t1.getAddedOn();
        String d2 = t2.getAddedOn();

//        Log.d("Debug", "d1: " + d1);
//        Log.d("Debug", "d2: " + d2);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        try {
            date1 = sdf.parse("1970-01-01T00:00:00");
            date1 = sdf.parse(d1);
        } catch (Exception e) {
        }

        try {
            date2 = sdf.parse("1970-01-01T00:00:00");
            date2 = sdf.parse(d2);
        } catch (Exception e) {
        }

//        Log.d("Debug", "Date1: " + date2.toString());
//        Log.d("Debug", "Date2: " + date2.toString());

        if (reversed) {
            // Ascending order
            return date1.compareTo(date2);
        } else {
            // Descending order
            return date2.compareTo(date1);

        }
    }
}


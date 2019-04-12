/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

import java.util.Comparator;

public class TorrentCompletedOnTimestampComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentCompletedOnTimestampComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

//        String d1 = t1.getCompletionOn();
//        String d2 = t2.getCompletionOn();

        String d1 = "" + t1.getCompletion_on();
        String d2 = "" + t2.getCompletion_on();

        long p1 = Long.parseLong(d1);
        long p2 = Long.parseLong(d2);


        // Not downloaded fix (awful workaround)
        // Note: take into account the distance between t1 and t2 (math stuff)
        try {

//            if (Integer.parseInt(t1.getPercentage()) != 100 && Integer.parseInt(t2.getPercentage()) == 100) {
//                p1 = -349714800000l;
//            }
        } catch (Exception e) {
            p1 = -349714800000l;
        }

        try {
//            if (Integer.parseInt(t2.getPercentage()) != 100 && Integer.parseInt(t1.getPercentage()) == 100) {
//                p2 = 349714800000l;
//            }
        } catch (Exception e) {
            p2 = 349714800000l;
        }

        try {
//            if (Integer.parseInt(t1.getPercentage()) != 100 && Integer.parseInt(t2.getPercentage()) != 100) {
//
//                p1 = 349714800000l;
//                p2 = -349714800000l;
//            }
        } catch (Exception e) {
            p1 = 349714800000l;
            p2 = -349714800000l;
        }


        // Sorting
        if (reversed) {
            // Ascending order
            return (int) (p2 - p1);
        } else {
            // Descending order
            return (int) (p1 - p2);
        }

    }
}


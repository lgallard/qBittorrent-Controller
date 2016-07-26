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

        String d1 = t1.getCompletionOn();
        String d2 = t2.getCompletionOn();

        long p1 = Long.parseLong(d1);
        long p2 = Long.parseLong(d2);

        int returnValue = -1;

        if (reversed) {
            // Ascending order
            if(p1 < p2){
                returnValue = -1;
            }else{
                returnValue = 1;
            }
        } else {
            // Descending order
            if(p1 < p2){
                returnValue = 1;
            }else{
                returnValue = -1;
            }

        }

        return returnValue;
    }
}


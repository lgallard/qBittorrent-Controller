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

public class TorrentAddedOnTimestampComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentAddedOnTimestampComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        String d1 = t1.getAddedOn();
        String d2 = t2.getAddedOn();

        long p1 = Long.parseLong(d1);
        long p2 = Long.parseLong(d2);

        if (reversed) {
            // Ascending order
            return (int) (p1 -p2);
        } else {
            // Descending order
            return (int) (p2 -p1);
        }

    }
}


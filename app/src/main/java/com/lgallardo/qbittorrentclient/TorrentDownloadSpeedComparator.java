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

public class TorrentDownloadSpeedComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentDownloadSpeedComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        int w1 = t1.getDownloadSpeedWeight();
        int w2 = t2.getDownloadSpeedWeight();

        if (reversed) {
            // Ascending order
            return w1 - w2;
        } else {
            // Descending order
            return w2 - w1;
        }

    }
}


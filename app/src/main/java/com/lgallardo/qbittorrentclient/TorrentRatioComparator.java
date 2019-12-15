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

public class TorrentRatioComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentRatioComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        float r1 = t1.getRatio();
        float r2 = t2.getRatio();

        if (reversed) {
            // Ascending order
            return (int) (r1 - r2);
        } else {
            // Descending order
            return (int) (r2 - r1);
        }
    }
}


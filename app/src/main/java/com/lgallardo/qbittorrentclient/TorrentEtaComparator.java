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

public class TorrentEtaComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentEtaComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {


        long e1 = t1.getEta();
        long e2 = t2.getEta();

        if (reversed) {
            // Ascending order
            return  (e1 > e2 ? 1: -1);
        } else {
            // Descending order
            return (e2 > e1 ? 1: -1);
        }


    }
}

/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import java.util.Comparator;

public class TorrentEtaComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentEtaComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        int eta1 = t1.getEtaInMinutes();
        int eta2 = t2.getEtaInMinutes();

        if (reversed) {
            // Descending order
            return eta2 - eta1;
        } else {
            // Ascending order
            return eta1 - eta2;
        }

    }
}
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

public class TorrentRatioComparator implements Comparator<Torrent> {

    boolean reversed = false;

    TorrentRatioComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(Torrent t1, Torrent t2) {

        String ratio1 = t1.getRatio();
        String ratio2 = t2.getRatio();

        float r1 = Float.parseFloat(ratio1) * 100;
        float r2 = Float.parseFloat(ratio2) * 100;

        if (reversed) {
            // Ascending order
            return (int) (r1 - r2);
        } else {
            // Descending order
            return (int) (r2 - r1);
        }
    }
}


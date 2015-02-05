/*******************************************************************************
 * Copyright (c) 2015 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/

package com.lgallardo.qbittorrentclient;

import java.util.HashMap;

public interface QBServiceListener {
    void updateTorrentList(Torrent[] torrents);

    void notifyCompleted(HashMap torrentsHashmap);
}

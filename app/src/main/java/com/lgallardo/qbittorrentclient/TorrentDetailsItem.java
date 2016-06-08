/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

public class TorrentDetailsItem {

    public static final int FILE = 0;
    public static final int TRACKER = 1;

    //ContentFile properties
    private String name;
    private String size;
    private Double progress;
    private int priority;

    // Tracker Properties

    public String info;

    // Item properties
    public int type;
    private String action;


    // Constructor
    public TorrentDetailsItem(String name, String size, Double progress, int priority, String info, int type, String action) {

        this.name = name;
        this.size = size;
        this.progress = progress;
        this.priority = priority;

        this.info = info;

        this.type = type;
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        if (this.action == null) {
            return "";
        }

        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

public class ObjectDrawerItem {

    public int icon;
    public String name;
    public int type;
    public boolean active;
    private String action;


    // Constructor
    public ObjectDrawerItem(int icon, String name, int type, boolean active, String action) {

        this.icon = icon;
        this.name = name;
        this.type = type;
        this.active = active;
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

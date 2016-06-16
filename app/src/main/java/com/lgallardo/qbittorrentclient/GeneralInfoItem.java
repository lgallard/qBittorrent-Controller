/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */
package com.lgallardo.qbittorrentclient;

public class GeneralInfoItem {

    public static final int GENERALINFO = 2;


    // General Info properties
    private String label;
    private String value;

    // Item properties
    public int type;
    private String action;


    // Constructor
    public GeneralInfoItem(String label, String value, int type, String action) {

        this.label = label;
        this.value = value;

        this.type = type;
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

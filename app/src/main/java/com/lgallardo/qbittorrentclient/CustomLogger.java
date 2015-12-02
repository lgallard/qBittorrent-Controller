package com.lgallardo.qbittorrentclient;
/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

/**
 * Created by lgallard on 17/11/15.
 */
public class CustomLogger {


    public static String reportLog = "";
    public static boolean notifierServiceReporting = false;
    public static boolean mainActivityReporting = false;


    public static void saveReportMessage(String tag, String message) {

        CustomLogger.reportLog = CustomLogger.reportLog + "\n" + "[" + tag + "]" + " " + message;

    }

    public static String getReport() {
        return reportLog;
    }

    public static boolean isNotifierServiceReportReporting() {
        return notifierServiceReporting;
    }

    public static void setNotifierServiceReportReporting(boolean notifierServiceReportReporting) {
        CustomLogger.notifierServiceReporting = notifierServiceReportReporting;
    }

    public static boolean isMainActivityReporting() {
        return mainActivityReporting;
    }

    public static void setMainActivityReporting(boolean mainActivityReporting) {
        CustomLogger.mainActivityReporting = mainActivityReporting;
    }
}
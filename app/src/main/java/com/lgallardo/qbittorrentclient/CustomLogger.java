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
    public static boolean reporting = false;
    public static boolean notifierServiceReportReady = false;
    public static boolean mainActivityReportReady = false;


    public static void saveReportMessage(String tag, String message) {

        CustomLogger.reportLog = CustomLogger.reportLog + "\n" + "[" + tag + "]" + " " + message;

    }

    public static String getReport() {
        return reportLog;
    }

    public static boolean isReporting() {
        return reporting;
    }

    public static void setReporting(boolean reporting) {
        CustomLogger.reporting = reporting;
    }


    public static boolean isNotifierServiceReportReady() {
        return notifierServiceReportReady;
    }

    public static void setNotifierServiceReportReady(boolean notifierServiceReportReady) {
        CustomLogger.notifierServiceReportReady = notifierServiceReportReady;
    }

    public static boolean isMainActivityReportReady() {
        return mainActivityReportReady;
    }

    public static void setMainActivityReportReady(boolean mainActivityReportReady) {
        CustomLogger.mainActivityReportReady = mainActivityReportReady;
    }
}
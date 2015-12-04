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


    public static String reportMainLog = "";
    public static String reportNotifierLog = "";
    public static String reportDescription = "";

    public static boolean mainActivityReporting = false;


    public static void saveReportMessage(String tag, String message) {

        if(tag.equals("Main")){

            if (CustomLogger.reportMainLog.equals("")){

                CustomLogger.reportMainLog = "\n[" + tag + "]";
            }
            CustomLogger.reportMainLog = CustomLogger.reportMainLog + "\n " + message;
        }

        if(tag.equals("Notifier")){
            if (CustomLogger.reportNotifierLog.equals("")){

                CustomLogger.reportNotifierLog = "\n[" + tag + "]";
            }
            CustomLogger.reportNotifierLog = CustomLogger.reportNotifierLog + "\n " + message;

        }

    }

    public static String getReport() {

        String report = getReportDescription() + "\n\n" + reportMainLog + "\n" + reportNotifierLog;

        return report;
    }

    public static void deleteMainReport() {
        reportMainLog = "";
    }

    public static void deleteNotifierReport() {
        reportNotifierLog = "";
    }


    public static boolean isMainActivityReporting() {
        return mainActivityReporting;
    }

    public static void setMainActivityReporting(boolean mainActivityReporting) {
        CustomLogger.mainActivityReporting = mainActivityReporting;
    }


    public static String getReportDescription() {
        return reportDescription;
    }

    public static void setReportDescription(String reportDescription) {
        CustomLogger.reportDescription = reportDescription;
    }
}
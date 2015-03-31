/*******************************************************************************
 * Copyright (c) 2015 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by lgallard on 1/30/15.
 */
public class Common {

    public static String calculateSize(String value) {

        long bytes = Long.parseLong(value);

        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }

    public static double humanSizeToBytes(String value) {
        String scalar;
        int unit = 1024;
        int exp;
        char c;
        double returnValue = 0;


        String[] words = value.split("\\s+");

//        Log.d("Debug", "words length:" + words.length);

        if (words.length == 2) {

//            Log.d("Debug", "words[0]:" + words[0]);
//            Log.d("Debug", "words[1]:" + words[1]);


            try {
                scalar = words[0].replace(",", ".");

//                Log.d("Debug", "scalar:" + scalar);


                exp = "BKMGTPE".indexOf((words[1]).toCharArray()[0]);

//                Log.d("Debug", "exp:" + exp);


                returnValue = Double.parseDouble(scalar) * Math.pow(unit, exp);

            } catch (Exception e) {
            }


        }

        return returnValue;
    }

    public static String unixTimestampToDate(String unixDate) {


        long dv = Long.valueOf(unixDate) * 1000;// its need to be in milisecond
        Date df = new Date(dv);
        String dateString = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(df);

        return dateString;
    }

    public static String secondsToEta(String secs) {

        if (!secs.equals("∞")) {

            long seconds = Long.parseLong(secs);

            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

            secs = "";

            if (day >= 100) {
                secs = "∞";
            } else {

                if (day > 0) {
                    secs = day + "d " + hours + "h";
                } else {

                    if (hours > 0) {
                        secs = hours + "h " + minute + "m";
                    } else {

                        if (minute > 0) {
                            secs = minute + "m";
                        } else {
                            secs = second + "s";
                        }

                    }


                }

            }
        }

        return secs;
    }

}

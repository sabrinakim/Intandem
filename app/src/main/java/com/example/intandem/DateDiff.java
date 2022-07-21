package com.example.intandem;

import java.util.Date;

public class DateDiff {

    // Function to print difference in
    // time start_date and end_date
    static String findDifference(Date d1, Date d2) {
        boolean largestUnitFound = false;
        long timeLeft = 0;

        long diffMill = d2.getTime() - d1.getTime(); // diff in milliseconds

        long diffYears = (diffMill / (1000L * 60 * 60 * 24 * 365));

        if (diffYears != 0) {
            largestUnitFound = true;
            timeLeft = diffYears;
        }

        long diffDays = (diffMill / (1000 * 60 * 60 * 24)) % 365;

        if (diffDays != 0 && !largestUnitFound) {
            largestUnitFound = true;
            timeLeft = diffDays;
        } else if (largestUnitFound) {
            if (diffDays > (365 / 2)) {
                return timeLeft + 1 + " year";
            }
            return timeLeft + " year";
        }

        long diffHrs = (diffMill / (1000 * 60 * 60)) % 24;

        if (diffHrs != 0 && !largestUnitFound) {
            largestUnitFound = true;
            timeLeft = diffHrs;
        } else if (largestUnitFound) {
            if (diffHrs > 12) {
                return timeLeft + 1 + " day";
            }
            return timeLeft + " day";
        }

        long diffMins = (diffMill / (1000 * 60)) % 60;

        if (diffMins != 0 && !largestUnitFound) {
            largestUnitFound = true;
            timeLeft = diffMins;
        } else if (largestUnitFound) {
            if (diffMins > 30) {
                return timeLeft + 1 + " hr";
            }
            return timeLeft + " hr";
        }

        long diffSecs = (diffMill / 1000) % 60;

        if (diffSecs != 0 && !largestUnitFound) {
//            largestUnitFound = true;
            timeLeft = diffHrs;
        } else if (largestUnitFound) {
            if (diffSecs > 30) {
                return timeLeft + 1 + " min";
            }
            return timeLeft + " min";
        }

        return timeLeft + " sec";
    }
}

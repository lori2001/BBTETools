package ScheduleGenerator;

import Common.logging.LogPanel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;

public class TimeFormatter {
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("H[:][.][mm]"); // the expected

    private static LocalTime convertToLocalTime(String hour) {
        return LocalTime.parse(hour, HOUR_FORMATTER);
    }

    public static LocalTime[] convertToLocalTimeArr(String interval) {
        try {
            int indOfLine = interval.indexOf('-');
            String start = interval.substring(0, indOfLine);
            String end = interval.substring(indOfLine + 1);

            LocalTime startTime = convertToLocalTime(start);
            LocalTime endTime = convertToLocalTime(end);

            if(startTime != null && endTime != null && startTime.isAfter(endTime)) {
                LogPanel.logln("HIBA: Az adott kezdõ és végsõ pontok fordítva vannak: " + startTime + " " + endTime, SG_LOG_INSTANCE);
                return null;
            }

            return new LocalTime[] { startTime, endTime };
        } catch (Exception e) {
            LogPanel.logln("HIBA: Az adott intervallum felismerése sikertelen: " + interval, SG_LOG_INSTANCE);
            return null;
        }
    }

    public static String localTimeArrToDisplayFormat(LocalTime[] t) {
        return t[0] + "\n" + t[1];
    }

    /*public static String formatInterval(String interval) {
        LocalTime[] t = convertToLocalTimeArr(interval);
        return localTimeArrToDisplayFormat(t);
    }*/
}

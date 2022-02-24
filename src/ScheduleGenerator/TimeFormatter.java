package ScheduleGenerator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("H[:][.][mm]"); // the expected

    public static LocalTime convertToLocalTime(String hour) {
        return LocalTime.parse(hour, HOUR_FORMATTER);
    }

    public static LocalTime[] convertToLocalTimeArr(String interval) {
        int indOfLine = interval.indexOf('-');
        String start = interval.substring(0, indOfLine);
        String end = interval.substring(indOfLine + 1);

        LocalTime startTime = convertToLocalTime(start);
        LocalTime endTime = convertToLocalTime(end);

        return new LocalTime[] { startTime, endTime };
    }

    public static String localTimeArrToDisplayFormat(LocalTime[] t) {
        return t[0] + "\n" + t[1];
    }

    public static String formatInterval(String interval) {
        LocalTime[] t = convertToLocalTimeArr(interval);
        return localTimeArrToDisplayFormat(t);
    }
}

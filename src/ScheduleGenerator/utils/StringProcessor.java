package ScheduleGenerator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringProcessor {
    // finds the first integer from a string
    // if it doesn't find any, returns null
    public static String findFirstInt(String stringToSearch) {
        Pattern integerPattern = Pattern.compile("-?\\d+");
        Matcher matcher = integerPattern.matcher(stringToSearch);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public static String getLongestLine(String text) {
        int longestLineLenght = 0;
        String longestLine = text;

        String[] lines = text.split("\n");
        for (String line: lines) {
            if(line.length() > longestLineLenght) {
                longestLineLenght = line.length();
                longestLine = line;
            }
        }

        return longestLine;
    }
}

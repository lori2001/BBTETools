package ScheduleGenerator.records;

import java.awt.*;

// ex. lab, szem, kurz
public record CourseType(String name, String abbreviation, String firstLetter, Color color) {

    public Color getCol() {
        return color;
    }
    
    public String getStr() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFirstLetter() {
        return firstLetter;
    }
}

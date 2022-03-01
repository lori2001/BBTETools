package ScheduleGenerator.models;

import java.awt.*;

// ex. lab, szem, kurz
public record CourseType(String name, Color color) {

    public Color getCol() {
        return color;
    }

    public String getStr() {
        return name;
    }
}

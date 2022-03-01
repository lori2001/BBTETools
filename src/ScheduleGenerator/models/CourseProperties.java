package ScheduleGenerator.models;

import java.awt.*;
import java.util.HashMap;

import static ScheduleGenerator.data.SGData.SUBJECT_NAME_ALIAS_MAP;

public class CourseProperties {
    private final Color color;
    private final String alias;

    public CourseProperties(String str, Color color) {
        this.color = color;
        this.alias = getAlias(str);
    }

    public Color getColor() {
        return color;
    }

    public String getAlias() {
        return alias;
    }

    private String getAlias(String str) {
        String ret = SUBJECT_NAME_ALIAS_MAP.get(str);
        if(ret == null) ret = str; // if there is no alias return original name
        return ret;
    }
}

package ScheduleGenerator;

import ScheduleGenerator.data.SGData;
import ScheduleGenerator.models.CourseProperties;
import ScheduleGenerator.models.CourseType;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Course {
    private static final String[] HEADERS = new String[]{
            "Ziua",
            "Orele",
            "Frecventa",
            "Sala",
            "Formatia",
            "Tipul",
            "Disciplina",
            "Cadrul didactic",
    };

    private final String day;
    private final String interval;
    private final String freq;
    private final String hall;
    private final String formation;
    private final String type;
    private final String courseName;
    private final String teacher;

    static HashMap<String, CourseProperties> courseProperties = new HashMap<>();

    public Course(HashMap<String, String> contentMap) {
        day = contentMap.get(HEADERS[0]);
        interval = contentMap.get(HEADERS[1]);
        freq = contentMap.get(HEADERS[2]);
        hall = contentMap.get(HEADERS[3]);
        formation = contentMap.get(HEADERS[4]);
        type = contentMap.get(HEADERS[5]);
        courseName = contentMap.get(HEADERS[6]);
        teacher = contentMap.get(HEADERS[7]);

        if(!courseProperties.containsKey(courseName)) {
            courseProperties.put(courseName, new CourseProperties(courseName, new Color(1,1,1)));
        }
    }

    private static final ArrayList<String> RO_DAYS = new ArrayList<>() {{
        add("Luni");
        add("Marti");
        add("Miercuri");
        add("Joi");
        add("Vineri");
    }};

    public int getDayIndex() {
        return RO_DAYS.indexOf(day);
    }

    public String getInterval() {
        return interval;
    }

    public LocalTime[] getIntervalAsLocalTimeArr() {
        return TimeFormatter.convertToLocalTimeArr(interval);
    }

    public String getFreq() {
        if(freq.equals("")) return null;

        String digits = "123456789";
        for(int i = 0; i < digits.length(); i++) {
            String digit = String.valueOf(digits.charAt(i));
            if(freq.contains(digit)) {
                return digit + ". hét";
            }
        }

        return freq;
    }

    public String getHall() {
        return hall;
    }

    public String getFormation() {
        return formation;
    }

    public String getType() {
        return type;
    }

    private static final Map<String, CourseType> RO_TO_HU_TYPES = new HashMap<>() {{
        put("Curs", new CourseType("Kurzus", SGData.Colors.COURSE_TYPE_COLORS[0]));
        put("Seminar", new CourseType("Szeminárium", SGData.Colors.COURSE_TYPE_COLORS[1]));
        put("Laborator", new CourseType("Labor", SGData.Colors.COURSE_TYPE_COLORS[2]));
    }};

    public CourseType getHuType() {
        return RO_TO_HU_TYPES.get(type);
    }

    public String getCourseAlias() {
        return courseProperties.get(courseName).getAlias();
    }

    public Color getCourseColor() {
        return courseProperties.get(courseName).getColor();
    }

    public String getTeacher() {
        return teacher;
    }

    // for debugging
    void printInfo() {
        System.out.print(
            day + " " + interval + " " + freq + " " + hall + " " + formation + " " + type + " " + courseName + " " + teacher + "\n"
        );
    }
}

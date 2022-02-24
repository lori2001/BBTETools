package ScheduleGenerator;

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

    private static final ArrayList<String> RO_DAYS = new ArrayList<>() {{
        add("Luni");
        add("Marti");
        add("Miercuri");
        add("Joi");
        add("Vineri");
    }};

    private static final Map<String, String> RO_TO_HU_TYPES = new HashMap<>() {{
        put("Curs", "Kurzus");
        put("Laborator", "Labor");
        put("Seminar", "Szeminárium");
    }};

    String day;
    String interval;
    String freq;
    String hall;
    String formation;
    String type;
    String courseName;
    String teacher;

    public Course(HashMap<String, String> contentMap) {
        day = contentMap.get(HEADERS[0]);
        interval = contentMap.get(HEADERS[1]);
        freq = contentMap.get(HEADERS[2]);
        hall = contentMap.get(HEADERS[3]);
        formation = contentMap.get(HEADERS[4]);
        type = contentMap.get(HEADERS[5]);
        courseName = contentMap.get(HEADERS[6]);
        teacher = contentMap.get(HEADERS[7]);
    }

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

    public String getHuType() {
        return RO_TO_HU_TYPES.get(type);
    }

    public String getCourseName() {
        return courseName;
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

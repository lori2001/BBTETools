package ScheduleGenerator;

import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.CourseType;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Course {
    public static final String[] HEADERS = new String[]{
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
    private final String subgroup;
    private final String type;
    private final String courseName;
    private final String teacher;

    private final String formattedSubgroup; // subgroup as [1, 2, ... n, group]
    private boolean isDuplicate;

    private static final HashMap<String, SubjectProperties> subjectProperties = new HashMap<>();

    public Course(HashMap<String, String> contentMap, String group) {
        day = contentMap.get(HEADERS[0]);
        interval = contentMap.get(HEADERS[1]);
        freq = contentMap.get(HEADERS[2]);
        hall = contentMap.get(HEADERS[3]);
        subgroup = contentMap.get(HEADERS[4]);
        type = contentMap.get(HEADERS[5]);
        courseName = contentMap.get(HEADERS[6]);
        teacher = contentMap.get(HEADERS[7]);

        if(!subjectProperties.containsKey(courseName)) {
            subjectProperties.put(courseName,
                    new SubjectProperties(courseName, SubjectProperties.getUnusedColor(SGData.Colors.CLASS_COLORS), group)
            );
        }

        formattedSubgroup = formatSubGroup(subgroup, group);
    }

    public static String formatSubGroup(String nonFormattedSubGr, String group) {
        int subgroupSplitIndex = nonFormattedSubGr.indexOf('/');
        if(subgroupSplitIndex != -1) {
            return nonFormattedSubGr.substring(subgroupSplitIndex + 1);
        } else {
            if(group == null) return null;

            if(nonFormattedSubGr.contains(group)) {
                return group;
            }
            return null;
        }
    }

    public String getFormattedSubgroup() {
        return formattedSubgroup;
    }

    public boolean isPartOfSubgroup(String formattedSubGr) {
        if(formattedSubgroup == null || formattedSubgroup.equals(subjectProperties.get(courseName).getGroup()))
            return true;
        return formattedSubGr.equals(formattedSubgroup);
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }
    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    private static final ArrayList<String> RO_DAYS = new ArrayList<>() {{
        add("Luni");
        add("Marti");
        add("Miercuri");
        add("Joi");
        add("Vineri");
    }};

    public String getDay() {
        return day;
    }
    public int getDayIndexInRO_DAYS() {
        return RO_DAYS.indexOf(day);
    }

    public String getInterval() {
        return interval;
    }

    public LocalTime[] getIntervalAsLocalTimeArr() {
        return TimeFormatter.convertToLocalTimeArr(interval);
    }

    public int getFreqAsNum() {
        if(freq.equals("")) return -1;

        for(int i = 0; i <= 9; i++) {
            if(freq.contains(Integer.toString(i))) {
                return i;
            }
        }

        return -1;
    }

    public String getFreqInHu() {
        if(freq.equals("")) return null;

        int freqNum = getFreqAsNum();
        if(freqNum == -1) return freq;

        return freqNum + ". hét";
    }

    public String getFreq() {
        return freq;
    }

    public String getHall() {
        return hall;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public String getType() {
        return type;
    }

    private static final Map<String, CourseType> RO_TO_HU_TYPES = new HashMap<>() {{
        put("Curs", new CourseType("Kurzus", "Kurz." ,"K", SGData.Colors.COURSE_TYPE_COLORS[0]));
        put("Seminar", new CourseType("Szeminárium", "Szem." , "SZ", SGData.Colors.COURSE_TYPE_COLORS[1]));
        put("Laborator", new CourseType("Labor", "Lab." , "L", SGData.Colors.COURSE_TYPE_COLORS[2]));
    }};

    public CourseType getTypeInHu() {
        return RO_TO_HU_TYPES.get(type);
    }

    public String getSubjectAlias() {
        return subjectProperties.get(courseName).getAlias();
    }

    public Color getSubjectColor() {
        return subjectProperties.get(courseName).getColor();
    }

    public String getSubjectGroup() { return subjectProperties.get(courseName).getGroup(); }

    public String getTeacher() {
        return teacher;
    }

    // for debugging
    void printInfo() {
        System.out.print(
            day + " " + interval + " " + freq + " " + hall + " " + subgroup + " " + type + " " + courseName + " " + teacher
                + " " + isDuplicate + "\n"
        );
    }
}

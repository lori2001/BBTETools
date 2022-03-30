package ScheduleGenerator;

import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.CourseType;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Course {
    public enum HEADER_CONTENT {
        DAY,
        INTERVAL,
        FREQ,
        HALL,
        SUBGROUP,
        TYPE,
        COURSE_NAME,
        TEACHER
    }
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

    private final ArrayList<String> content = new ArrayList<>();
    private final String formattedSubgroup; // subgroup as [1, 2, ... n, group]
    private boolean isDuplicate;

    private static final HashMap<String, SubjectProperties> subjectProperties = new HashMap<>();
    public static SubjectProperties[] getSubjectProperties() {
        return subjectProperties.values().toArray(new SubjectProperties[0]);
    }

    public Course(String[] contentStr, String group) {
        content.addAll(Arrays.asList(contentStr).subList(0, HEADERS.length));

        if(!subjectProperties.containsKey(getContent(HEADER_CONTENT.COURSE_NAME))) {
            subjectProperties.put(getContent(HEADER_CONTENT.COURSE_NAME),
                    new SubjectProperties(getContent(HEADER_CONTENT.COURSE_NAME), SubjectProperties.getUnusedColor(SGData.Colors.CLASS_COLORS), group)
            );
        }

        formattedSubgroup = formatSubGroup(getContent(HEADER_CONTENT.SUBGROUP), group);
    }

    public Course(HashMap<String, String> contentMap, String group) {
        for (String header : HEADERS) {
            content.add(contentMap.get(header));
        }

        if(!subjectProperties.containsKey(getContent(HEADER_CONTENT.COURSE_NAME))) {
            subjectProperties.put(getContent(HEADER_CONTENT.COURSE_NAME),
                    new SubjectProperties(getContent(HEADER_CONTENT.COURSE_NAME), SubjectProperties.getUnusedColor(SGData.Colors.CLASS_COLORS), group)
            );
        }

        formattedSubgroup = formatSubGroup(getContent(HEADER_CONTENT.SUBGROUP), group);
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
        if(formattedSubgroup == null || formattedSubgroup.equals(subjectProperties.get(getContent(HEADER_CONTENT.COURSE_NAME)).getGroup()))
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

    public int getDayIndexInRO_DAYS() {
        return RO_DAYS.indexOf(getContent(HEADER_CONTENT.DAY));
    }

    public LocalTime[] getIntervalAsLocalTimeArr() {
        return TimeFormatter.convertToLocalTimeArr(getContent(HEADER_CONTENT.INTERVAL));
    }

    public int getFreqAsNum() {
        if(getContent(HEADER_CONTENT.FREQ).equals("")) return -1;

        for(int i = 0; i <= 9; i++) {
            if(getContent(HEADER_CONTENT.FREQ).contains(Integer.toString(i))) {
                return i;
            }
        }

        return -1;
    }

    public String getFreqInHu() {
        if(getContent(HEADER_CONTENT.FREQ).equals("")) return null;

        int freqNum = getFreqAsNum();
        if(freqNum == -1) return getContent(HEADER_CONTENT.FREQ);

        return freqNum + ". hét";
    }

    private static final Map<String, CourseType> RO_TO_HU_TYPES = new HashMap<>() {{
        put("Curs", new CourseType("Kurzus", "Kurz." ,"K", SGData.Colors.COURSE_TYPE_COLORS[0]));
        put("Seminar", new CourseType("Szeminárium", "Szem." , "SZ", SGData.Colors.COURSE_TYPE_COLORS[1]));
        put("Laborator", new CourseType("Labor", "Lab." , "L", SGData.Colors.COURSE_TYPE_COLORS[2]));
    }};

    public CourseType getTypeInHu() {
        return RO_TO_HU_TYPES.get(getContent(HEADER_CONTENT.TYPE));
    }

    public String getSubjectAlias() {
        return subjectProperties.get(getContent(HEADER_CONTENT.COURSE_NAME)).getAlias();
    }

    public Color getSubjectColor() {
        return subjectProperties.get(getContent(HEADER_CONTENT.COURSE_NAME)).getColor();
    }

    public String getContent(HEADER_CONTENT at) {
        return content.get(at.ordinal());
    }

    public ArrayList<String> getContentList() {
        return content;
    }

    public String getSubjectGroup() { return subjectProperties.get(getContent(HEADER_CONTENT.COURSE_NAME)).getGroup(); }

    // for debugging
   /* void printInfo() {
        System.out.print(
            day + " " + interval + " " + freq + " " + hall + " " + subgroup + " " + type + " " + courseName + " " + teacher
                + " " + isDuplicate + "\n"
        );
    }*/
}

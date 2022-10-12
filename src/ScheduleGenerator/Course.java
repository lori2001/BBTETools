package ScheduleGenerator;

import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.CourseType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    public Course(String[] contentStr, String group) {
        content.addAll(Arrays.asList(contentStr).subList(0, HEADERS.length));
        formattedSubgroup = formatSubGroup(getContent(HEADER_CONTENT.SUBGROUP), group);
    }

    public Course(HashMap<String, String> contentMap, String group) {
        for (String header : HEADERS) {
            content.add(contentMap.get(header));
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
        if(formattedSubgroup == null)
            return true;
        return formattedSubGr.equals(formattedSubgroup);
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


    public CourseType getTypeInHu() {
        return SGData.RO_TO_HU_TYPES.get(getContent(HEADER_CONTENT.TYPE));
    }

    public String getSubjectAlias() {
        return SGData.getAlias(getContent(HEADER_CONTENT.COURSE_NAME));
    }

    public String getContent(HEADER_CONTENT at) {
        return content.get(at.ordinal());
    }

    public ArrayList<String> getContentList() {
        return content;
    }

    // for debugging
    public String toString() {
        return String.join(",", content);
    }
}

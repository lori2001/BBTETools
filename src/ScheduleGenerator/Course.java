package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.CourseType;
import com.google.gson.Gson;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;

public class Course {
    public enum HEADER_CONTENT {
        DAY,
        INTERVAL,
        FREQ,
        HALL,
        FORMATION,
        TYPE,
        COURSE_NAME,
      //  TEACHER
    }
    public static final String[] HEADERS = new String[]{
            "Ziua",
            "Orele",
            "Frecventa",
            "Sala",
            "Formatia",
            "Tipul",
            "Disciplina",
       //     "Cadrul didactic",
    };

    private final ArrayList<String> content = new ArrayList<>();
    private final String subGroup; // subgroup as [1, 2, ... n, group]

    public Course(String[] contentStr, String group) {
        content.addAll(Arrays.asList(contentStr).subList(0, HEADERS.length));
        subGroup = formatFormationAsSubGroup(getContent(HEADER_CONTENT.FORMATION), group);
    }

    public Course(Course cloneFrom) {
        content.addAll(cloneFrom.content);
        subGroup = formatFormationAsSubGroup(getContent(HEADER_CONTENT.FORMATION), "621");
    }

    public Course(HashMap<String, String> contentMap, String group) {
        for (String header : HEADERS) {
            content.add(contentMap.get(header));
        }
        subGroup = formatFormationAsSubGroup(getContent(HEADER_CONTENT.FORMATION), group);
    }

    public static String formatFormationAsSubGroup(String subGr, String group) {
        int subgroupSplitIndex = subGr.indexOf('/');
        if(subgroupSplitIndex != -1) {
            return subGr.substring(subgroupSplitIndex + 1);
        } else {
            if(group == null) return null;

            if(subGr.contains(group)) {
                return group;
            }
            return null;
        }
    }

    public boolean isPartOfSubgroup(String subGr) {
        if(subGroup == null)
            return true;
        return subGr.equals(subGroup);
    }

    public int getDayIndexInRO_DAYS() {
        if(!SGData.RO_DAYS.contains(getContent(HEADER_CONTENT.DAY))) {
            LogPanel.logln("HIBA: Sikertelen volt feldolgozni a következő nap nevét: " + getContent(HEADER_CONTENT.DAY), SG_LOG_INSTANCE);
        }
        return SGData.RO_DAYS.indexOf(getContent(HEADER_CONTENT.DAY));
    }

    public LocalTime[] getIntervalAsLocalTimeArr() {
        return TimeFormatter.convertToLocalTimeArr(getContent(HEADER_CONTENT.INTERVAL));
    }

    public int getFreqAsNum() {
        if (getContent(HEADER_CONTENT.FREQ).equals("")) {
            return -1;
        }

        for (int i = 0; i <= 9; i++) {
            if (getContent(HEADER_CONTENT.FREQ).contains(Integer.toString(i))) {
                return i;
            }
        }

        // LogPanel.logln("HIBA: Sikertelen volt feldolgozni a következő frekvencia stringet: " + getContent(HEADER_CONTENT.FREQ), SG_LOG_INSTANCE);
        return -1;
    }

    public String getFreqInHu() {
        if(getContent(HEADER_CONTENT.FREQ).equals("")) return null;

        int freqNum = getFreqAsNum();
        if(freqNum == -1) return getContent(HEADER_CONTENT.FREQ);

        return freqNum + ". hét";
    }

    public CourseType getTypeInHu() {
        try {
            String key = getContent(HEADER_CONTENT.TYPE).toLowerCase().substring(0, 1);

            if(!SGData.RO_TO_HU_TYPES.containsKey(key)){
                throw new Exception("Type does not contain key");
            }

            return SGData.RO_TO_HU_TYPES.get(key);
        } catch (Exception e) {
            LogPanel.logln("HIBA: Sikertelen volt feldolgozni a következő típusnevet: " + getContent(HEADER_CONTENT.TYPE), SG_LOG_INSTANCE);
            return null;
        }
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

    // for debugging return as json
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

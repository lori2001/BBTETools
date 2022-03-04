package ScheduleGenerator.utils;

import Common.logging.LogPanel;
import ScheduleGenerator.records.Major;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.MAJORS;

public class Convert {
    public static Major groupToMajor(String group) {
        Major major = null;

        // process group num
        try {
            if(group.length() != 3) throw new Exception("HIBA: A csoport szám" + group + " hossza váratlan!");
            int groupNum = Integer.parseInt(group);

            int majorNum = groupNum / 100;
            major = MAJORS.get(majorNum);

            if(major == null) throw new Exception("HIBA: Az órarend link feldolgozó által kapott csoport számnak megfelelõ szak nem talált!");
        }
        catch (NumberFormatException e) {
            LogPanel.logln("HIBA: A " + group + " csoportszámhoz tartozó szakot nem sikerült megtalálni!", SG_LOG_INSTANCE);
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return major;
    }

    public static String groupToStudYear(String group) {
        String studYear = null;

        // process group num
        try {
            if(group.length() != 3) throw new Exception("HIBA: A csoport szám" + group + " hossza váratlan!");
            int groupNum = Integer.parseInt(group);

            int sY = (groupNum / 10) % 10;
            studYear = Integer.toString(sY);
        }
        catch (NumberFormatException e) {
            LogPanel.logln("HIBA: A " + group + " csoportszámhoz tartozó tanuló-évet nem sikerült megtalálni!", SG_LOG_INSTANCE);
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return studYear;
    }

}

package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.data.SGData;

import java.awt.*;
import java.util.ArrayList;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.SUBJECT_NAME_ALIAS_MAP;

public class Subject {
    private final String alias;
    private final String name;
    private final String group;

    public Subject(String name, Color color, String group) {
        if(!usedColors.contains(color)) {
            usedColors.add(color);
        }

        this.name = name;
       //  this.color = color;
        this.group = group;
        this.alias = getAlias(name);
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getGroup() {
        return group;
    }

    private String getAlias(String str) {
        String ret = SUBJECT_NAME_ALIAS_MAP.get(str);
        if(ret == null) ret = str; // if there is no alias return original name
        return ret;
    }

    private static final ArrayList<Color> usedColors = new ArrayList<>();

    public static void clearUsedColors() {
        usedColors.clear();
    }

    public static Color getUnusedColor(Color[] from) {
        for(Color col : from) {
            if(!usedColors.contains(col)) {
                return col;
            }
        }

        LogPanel.logln("MEGJEGYZÉS: Elfogytak az ajánlott színek a tantárgyakra. Ezek szürkék lesznek", SG_LOG_INSTANCE);
        return SGData.Colors.BASE_COLOR;
    }

}

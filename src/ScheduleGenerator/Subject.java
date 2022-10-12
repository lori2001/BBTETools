package ScheduleGenerator;

import static ScheduleGenerator.data.SGData.SUBJECT_NAME_ALIAS_MAP;

public class Subject {
    /*
    private final Color color;
    private final String alias;
    private final String name;
    private final String group;
    private final ArrayList<Course> courses;

    Subject(ArrayList<Course> courses, String name, Color color, String group) {
        if(!usedColors.contains(color)) {
            usedColors.add(color);
        }

        this.name = name;
        this.color = color;
        this.group = group;
        this.alias = getAlias(name);
        this.courses = courses;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public String getAlias() {
        return alias;
    }
    public String getGroup() {
        return group;
    }
    public ArrayList<Course> getCourses() {
        return courses;
    }

    private static final ArrayList<Color> usedColors = new ArrayList<>();
    private static void clearUsedColors() {
        usedColors.clear();
    }
    private static Color getUnusedColor(Color[] from) {
        for(Color col : from) {
            if(!usedColors.contains(col)) {
                return col;
            }
        }

        LogPanel.logln("MEGJEGYZÉS: Elfogytak az ajánlott színek a tantárgyakra. Ezek szürkék lesznek", SG_LOG_INSTANCE);
        return SGData.Colors.BASE_COLOR;
    }*/

    public static String getAlias(String str) {
        String ret = SUBJECT_NAME_ALIAS_MAP.get(str);
        if(ret == null) ret = str; // if there is no alias return original name
        return ret;
    }

}

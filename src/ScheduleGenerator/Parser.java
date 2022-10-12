package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.records.Major;
import ScheduleGenerator.utils.Convert;
import ScheduleGenerator.utils.StringProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.*;

public class Parser {
    public static ArrayList<LocalTime[]> getHourIntervals() {
        ArrayList<LocalTime[]> hourIntervals = new ArrayList<>();
        String url = getLinkPrefix() + "grafic/IM1.html";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements table = doc.select("tr");

            int i = 0;

            ArrayList<String> duplicateChecker = new ArrayList<>();
            for (Element t : table) {
                if (i >= 3) {
                    List<String> thElements = t.select("th").eachText();
                    String intervalStr = null;

                    for (String thElement : thElements) {
                        intervalStr = thElement;
                        try {
                            NumberFormat.getInstance().parse(intervalStr).intValue();
                            break;
                        } catch (Exception ignore) {
                        }
                    }

                    if (intervalStr != null && intervalStr.contains("-")) {
                        // if intervals start repeating stop searching more
                        if (duplicateChecker.contains(intervalStr)) break;

                        LocalTime[] interval = TimeFormatter.convertToLocalTimeArr(intervalStr);

                        duplicateChecker.add(intervalStr);
                        hourIntervals.add(interval);
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogPanel.logln("HIBA: Az óra-intervallumok leolvasása az internetrõl nem sikerült! ", SG_LOG_INSTANCE);
        }

        return hourIntervals;
    }

    public static ArrayList<String> genGroupsFor(String lang, String major, String studYear) {
        Integer majorNum = -1;
        for (Map.Entry<Integer, Major> set : MAJORS.entrySet()) {
            if (Objects.equals(set.getValue().getName(), major) && Objects.equals(set.getValue().getLang(), lang)) {
                majorNum = set.getKey();
            }
        }

        if (majorNum == -1) {
            LogPanel.logln("HIBA: A kiválasztott adatok alapján sikertelen volt kialakítani egy csoportot", SG_LOG_INSTANCE);
            return null;
        }

        ArrayList<String> groups = new ArrayList<>();
        try {
            String url = getLinkPrefix() + "tabelar/" + MAJORS.get(majorNum).getCode() + studYear + ".html";
            Document doc = Jsoup.connect(url).get();

            Elements groupElements = doc.select("h1");
            for (int i = 1; i < groupElements.size(); i++) {
                groups.add(StringProcessor.findFirstInt(groupElements.get(i).text()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogPanel.logln("HIBA: A csoportokat nem sikerült beszerezni! ", SG_LOG_INSTANCE);
            return null;
        }

        return groups;
    }

    public static ArrayList<String> genSubGroupsFor(String lang, String major, String studYear, String group) {
        ArrayList<String> subGroups = new ArrayList<>();

        Integer majorNum = -1;
        for (Map.Entry<Integer, Major> set : MAJORS.entrySet()) {
            if (Objects.equals(set.getValue().getName(), major) && Objects.equals(set.getValue().getLang(), lang)) {
                majorNum = set.getKey();
            }
        }
        if (majorNum == -1) {
            LogPanel.logln("HIBA: A kiválasztott adatok alapján sikertelen volt kialakítani egy alcsoportot", SG_LOG_INSTANCE);
        }

        String url = getLinkPrefix() + "tabelar/" + MAJORS.get(majorNum).getCode() + studYear + ".html";
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen volt a tantárgyak beszerzése! " + e, SG_LOG_INSTANCE);
            return null;
        }

        Elements groupElements = doc.select("h1");
        int tableI = -1; // -1 because there's a "bonus" header
        boolean found = false;
        for (String gr : groupElements.eachText()) {
            if (gr.contains(group)) {
                found = true;
                break;
            }
            tableI++;
        }
        if (!found) {
            LogPanel.logln("HIBA: A kiválasztott csoport(" + group + ") nem talált a " + url + " linken!", SG_LOG_INSTANCE);
        }

        Element table = doc.select("table").get(tableI);
        if (table == null) {
            LogPanel.logln("Az " + url + " en levõ adatokat sikertelen volt értelmezni!", SG_LOG_INSTANCE);
            return null;
        }

        String[] headers = null;
        Elements tableRows = table.select("tr");
        int i = 0;

        for (Element tableRow : tableRows) {
            if (i == 0) {
                Elements hdrs = tableRow.select("th");

                headers = new String[hdrs.size()];
                for (int j = 0; j < hdrs.size(); j++) {
                    headers[j] = hdrs.get(j).text();
                }
            } else {
                Elements courseContent = tableRow.select("td");

                for (int j = 0; j < courseContent.size(); j++) {
                    if (headers[j].equals(Course.HEADERS[4])) {
                        // "Formatia"'s data
                        String content = courseContent.get(j).text();
                        int subgroupSplitIndex = content.indexOf('/');
                        if (subgroupSplitIndex != -1) {
                            String sG = content.substring(subgroupSplitIndex + 1);
                            if (!subGroups.contains(sG)) subGroups.add(sG);
                        }
                    }
                }
            }

            i++;
        }

        if (subGroups.size() == 0) {
            // LogPanel.logln("MEGJEGYZÉS: Nincs alcsoport!", SG_LOG_INSTANCE);
            subGroups.add("nincs");
        }

        return subGroups;
    }

    public static ArrayList<Course> genCourses(String group, String subGroup) {
        System.out.println("GENERATE COURSES FOR: " + group + " " + subGroup);
        ArrayList<Course> courses = new ArrayList<>();

        Major major = Convert.groupToMajor(group);
        String studYear = Convert.groupToStudYear(group);

        String[] headers = null;
        String url = getLinkPrefix() + "tabelar/" + major.getCode() + studYear + ".html";

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            e.printStackTrace();
            LogPanel.logln("Sikertelen kapcsolódás az " + url + " linkhez!", SG_LOG_INSTANCE);
            return null;
        }

        int tableI = findTableIndexForGroup(doc, group);
        if(tableI == -1) {
            LogPanel.logln("A kiválasztott csoport(" + group + ") nem talált a " +  url + " linken!", SG_LOG_INSTANCE);
            return null;
        }

        Element table = doc.select("table").get(tableI);
        if(table == null) {
            LogPanel.logln("Az " + url + " en levõ adatokat sikertelen volt értelmezni!", SG_LOG_INSTANCE);
            return null;
        }

        Elements tableRows = table.select("tr");

        int i = 0;
        for (Element tableRow : tableRows) {
            if (i == 0) { // header
                Elements hdrs = tableRow.select("th");

                headers = new String[hdrs.size()];
                for(int j = 0; j < hdrs.size(); j++) {
                    headers[j] = hdrs.get(j).text();
                }

            } else {
                Elements courseContent = tableRow.select("td");
                HashMap<String, String> contentMap = new HashMap<>();

                for(int j = 0; j < courseContent.size(); j++) {
                    contentMap.put(headers[j], courseContent.get(j).text());
                }

                Course courseToAdd = new Course(contentMap, group);

                if(courseToAdd.isPartOfSubgroup(subGroup) || subGroup.equals("nincs")) {
                    courses.add(courseToAdd);
                }
            }

            i++;
        }

        return courses;
    }

    public static int findTableIndexForGroup(Document doc, String group) {
        Elements groupElements = doc.select("h1");
        int tableI = -1; // -1 because there's a "bonus" header
        boolean found = false;
        for(String gr : groupElements.eachText()) {
            if(gr.contains(group)) {
                found = true;
                break;
            }
            tableI++;
        }
        return found ? tableI : -1;
    }
}

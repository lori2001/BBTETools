package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.records.Major;
import ScheduleGenerator.utils.StringProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.MAJORS;
import static ScheduleGenerator.data.SGData.getLinkPrefix;

public class GlobalParser {
    private static ArrayList<LocalTime[]> hourIntervals = null;

    // returns true if give course has to be added to the array, false otherwise
    public static boolean solveDuplicates(Course courseToAdd, ArrayList<Course> courses) {
        boolean shouldBeAdded = true;

        int courseOnSameTimeIndex = -1;
        for(int j=0; j < courses.size(); j++) {
            if(courses.get(j).getInterval().contains(courseToAdd.getInterval()) &&
                    courses.get(j).getDay().contains(courseToAdd.getDay())) {
                courseOnSameTimeIndex = j;
                break;
            }
        }

        if(courseOnSameTimeIndex != -1)
        {
            String groupOfCourseToAdd = courseToAdd.getSubjectGroup();
            String subGroupOfCourseToAdd = courseToAdd.getFormattedSubgroup();
            String groupOfDuplicate = courses.get(courseOnSameTimeIndex).getSubjectGroup();
            String subGroupOfDuplicate = courses.get(courseOnSameTimeIndex).getFormattedSubgroup();

            if(subGroupOfCourseToAdd == null && Objects.equals(subGroupOfDuplicate, groupOfDuplicate)) {
                shouldBeAdded = false;
            } else if(subGroupOfDuplicate == null && Objects.equals(subGroupOfCourseToAdd, groupOfCourseToAdd)) {
                courses.set(courseOnSameTimeIndex, courseToAdd);
                shouldBeAdded = false;
            }
            else {
                courses.get(courseOnSameTimeIndex).setDuplicate(true);
                courseToAdd.setDuplicate(true);
            }
        }

        return shouldBeAdded;
    }
    public static ArrayList<LocalTime[]> getHourIntervals() {
        if(hourIntervals != null) {
            return hourIntervals;
        }

        hourIntervals = new ArrayList<>();
        String url = getLinkPrefix() + "grafic/IM1.html";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements table = doc.select("tr");

            int i = 0;

            ArrayList<String> duplicateChecker = new ArrayList<>();
            for (Element t: table) {
                if(i >= 3){
                    List<String> thElements = t.select("th").eachText();
                    String intervalStr = null;

                    for (String thElement : thElements) {
                        intervalStr = thElement;
                        try {
                            NumberFormat.getInstance().parse(intervalStr).intValue();
                            break;
                        } catch (Exception ignore) {}
                    }

                    if(intervalStr != null && intervalStr.contains("-"))
                    {
                        // if intervals start repeating stop searching more
                        if(duplicateChecker.contains(intervalStr)) break;

                        LocalTime[] interval = TimeFormatter.convertToLocalTimeArr(intervalStr);

                        duplicateChecker.add(intervalStr);
                        hourIntervals.add(interval);
                    }
                }
                i++;
            }
        } catch (Exception e) {
            LogPanel.logln("HIBA: Az óra-intervallumok leolvasása az internetrõl nem sikerült! "
                    + Arrays.toString(e.getStackTrace()), SG_LOG_INSTANCE);
        }

        return hourIntervals;
    }
    public static ArrayList<String> genGroupsFor(String lang, String major, String studYear) {
        Integer majorNum = -1;
        for (Map.Entry<Integer, Major> set : MAJORS.entrySet()) {
            if(Objects.equals(set.getValue().getName(), major) && Objects.equals(set.getValue().getLang(), lang)) {
                majorNum = set.getKey();
            }
        }

        if(majorNum == -1) {
            LogPanel.logln("HIBA: A kiválasztott adatok alapján sikertelen volt kialakítani egy csoportot", SG_LOG_INSTANCE);
        }

        ArrayList<String> groups = new ArrayList<>();
        try {
            String url = getLinkPrefix() + "tabelar/" + MAJORS.get(majorNum).getCode() + studYear + ".html";
            Document doc = Jsoup.connect(url).get();

            Elements groupElements = doc.select("h1");
            for(int i = 1; i < groupElements.size(); i ++) {
                groups.add(StringProcessor.findFirstInt(groupElements.get(i).text()));
            }
        } catch (Exception e) {
            LogPanel.logln("HIBA: A csoportokat nem sikerült beszerezni! " + e, SG_LOG_INSTANCE);
        }

        return groups;
    }
    public static ArrayList<String> genSubGroupsFor(String lang, String major, String studYear, String group) {
        ArrayList<String> subGroups = new ArrayList<>();

        Integer majorNum = -1;
        for (Map.Entry<Integer, Major> set : MAJORS.entrySet()) {
            if(Objects.equals(set.getValue().getName(), major) && Objects.equals(set.getValue().getLang(), lang)) {
                majorNum = set.getKey();
            }
        }
        if(majorNum == -1)
            LogPanel.logln("HIBA: A kiválasztott adatok alapján sikertelen volt kialakítani egy csoportot", SG_LOG_INSTANCE);

        try {
            String url = getLinkPrefix() + "tabelar/" + MAJORS.get(majorNum).getCode() + studYear + ".html";
            Document doc = Jsoup.connect(url).get();

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
            if(!found)
                LogPanel.logln("HIBA: A kiválasztott csoport(" + group + ") nem talált a " +  url + " linken!" , SG_LOG_INSTANCE);

            Element table = doc.select("table").get(tableI);
            if(table == null) throw new Exception("Az " + url + " en levõ adatokat sikertelen volt értelmezni!");

            String[] headers = null;
            Elements tableRows = table.select("tr");
            int i = 0;

            for (Element tableRow : tableRows) {
                if (i == 0) {
                    Elements hdrs = tableRow.select("th");

                    headers = new String[hdrs.size()];
                    for(int j = 0; j < hdrs.size(); j++) {
                        headers[j] = hdrs.get(j).text();
                    }
                } else {
                    Elements courseContent = tableRow.select("td");

                    for(int j = 0; j < courseContent.size(); j++) {
                        if(headers[j].equals(Course.HEADERS[4])){
                            // "Formatia"'s data
                            String content = courseContent.get(j).text();
                            int subgroupSplitIndex = content.indexOf('/');
                            if(subgroupSplitIndex != -1) {
                                String sG = content.substring(subgroupSplitIndex + 1);
                                if(!subGroups.contains(sG)) subGroups.add(sG);
                            }
                        }
                    }
                }

                i++;
            }
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen volt a tantárgyak beszerzése! " + e, SG_LOG_INSTANCE);
        }

        if(subGroups.size() == 0){
            // LogPanel.logln("MEGJEGYZÉS: Nincs alcsoport!", SG_LOG_INSTANCE);
            subGroups.add("nincs");
        }

        return subGroups;
    }
}

package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.records.Major;
import ScheduleGenerator.utils.Convert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static ScheduleGenerator.GlobalParser.solveDuplicates;
import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.*;

public class Parser {
    private Major major = null;
    private String group = "";
    private String studYear = null;
    private String subGroup = "";
    private ArrayList<Course> courses;


    public Parser(String group, String subGroup) {
        reparseCourses(group, subGroup);
    }

    public void reparseCourses(String group, String subGroup) {
        if(this.group.equals(group) && this.subGroup.equals(subGroup)) return;

        this.group = group;
        this.subGroup = subGroup;
        this.major = Convert.groupToMajor(group);
        this.studYear = Convert.groupToStudYear(group);

        SubjectProperties.clearUsedColors();
        courses = genCourses();
    }

    public String getTopLeftContent() {
        /*if(major.getCode() != null && studYear != null && getCalendarSemester() != -1) {
            return major.getCode() + "\n" + studYear + "." + getCalendarSemester();
        } else {
            LogPanel.logln("HIBA: A tanuló évfolyamának meghatározásakor", SG_LOG_INSTANCE);
        }
        return null;*/

        if(Objects.equals(subGroup, "nincs")){
            return group;
        }

        return group + "\n" + subGroup;
    }

    private ArrayList<Course> genCourses() {
        String[] headers = null;
        ArrayList<Course> courses = new ArrayList<>();

        try {
            String url = getLinkPrefix() + "tabelar/" + major.getCode() + studYear + ".html";
            Document doc = Jsoup.connect(url).get();

            System.out.println(url);

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
                        if(solveDuplicates(courseToAdd, courses)) {
                            courses.add(courseToAdd);
                        }
                    }
                }

                i++;
            }
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen volt a tantárgyak beszerzése! " + e, SG_LOG_INSTANCE);
        }

        return courses;
    }

    public Major getMajor() {
        return major;
    }
    public String getGroup() {
        return group;
    }
    public String getStudYear() {
        return studYear;
    }
    public String getSubGroup() {
        return subGroup;
    }
    public ArrayList<Course> getCourses() {
        return courses;
    }
}
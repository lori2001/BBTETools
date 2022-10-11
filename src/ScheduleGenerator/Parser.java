package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.records.Major;
import ScheduleGenerator.utils.Convert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static ScheduleGenerator.GlobalParser.markDuplicates;
import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.*;

public class Parser {
    private Major major = null;
    private String group = "";
    private String studYear = null;
    private String subGroup = "";
    private ArrayList<Course> courses;

    private boolean parsingFailed = false;

    private final JFrame connectionPrompt = new JFrame("Internet kapcsolat...");

    public Parser(String group, String subGroup) {
        connectionPrompt.setBounds(0, 0, 400, 150);
        connectionPrompt.setLocationRelativeTo(null); // Sets position on center of the screen
        connectionPrompt.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        JLabel text = new JLabel(
                "<html><center><h1>Internet csatlakozás...</h1></center>" +
                        "<p>Az órarend elemzése internet kapcsolattal történik.<br>" +
                        "Ha nincs interneted, várj egy kicsit. Az app hamarosan elindul.</p>" +
                        "</html>");
        connectionPrompt.add(text);

        connectionPrompt.setVisible(true);

        reparseCourses(group, subGroup);
    }

    public void reparseCourses(String group, String subGroup) {
        System.out.println("REPARSE COURSES");
        if((this.group.equals(group) && this.subGroup.equals(subGroup)) || parsingFailed) return;

        this.group = group;
        this.subGroup = subGroup;
        this.major = Convert.groupToMajor(group);
        this.studYear = Convert.groupToStudYear(group);

        SubjectProperties.clearUsedColors();

        try {
            Thread internetConn = new Thread(
                    () -> {
                        try{
                            courses = genCourses();
                            connectionPrompt.setVisible(false);
                        } catch (Exception e){
                            parsingFailed = true;
                            connectionPrompt.setVisible(false);
                            LogPanel.logln("VIGYÁZAT: Sikertelen volt a tantárgyak beszerzése! ", SG_LOG_INSTANCE);
                            LogPanel.logln("MEGJEGYZÉS: A manuális órarendkészítési funkciók attól még mûködnek." + e, SG_LOG_INSTANCE);
                        }
                    }
            );
            internetConn.start();
            while(internetConn.isAlive()){
                Thread.sleep(1000); // 1 second
                // TODO: Some kind of loding notifier
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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

    private int findTableIndexForGroup(Document doc, String group) {
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

    private ArrayList<Course> genCourses() throws Exception {
        System.out.println("GENERATE COURSES FOR: " + major.getCode() + studYear + " " + group + " " + subGroup);

        String[] headers = null;
        ArrayList<Course> courses = new ArrayList<>();

        String url = getLinkPrefix() + "tabelar/" + major.getCode() + studYear + ".html";
        Document doc = Jsoup.connect(url).get();

        int tableI = findTableIndexForGroup(doc, group);
        if(tableI == -1) {
            throw new Exception("A kiválasztott csoport(" + group + ") nem talált a " +  url + " linken!");
        }
        System.out.println("LEFUTE");

        Element table = doc.select("table").get(tableI);
        if(table == null) {
            throw new Exception("Az " + url + " en levõ adatokat sikertelen volt értelmezni!");
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
                    if(markDuplicates(courseToAdd, courses)) {
                            courses.add(courseToAdd);
                    }
                }
            }

            i++;
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

    public boolean isParsingFailed() {
        return parsingFailed;
    }
}

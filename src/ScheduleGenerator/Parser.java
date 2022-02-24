package ScheduleGenerator;

import HomeworkGatherer.logging.LogPanel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Parser {
    // before the 8th month it's considered 2nd semester of previous year
    // after this it' s considered first semester of given year
    private static final int SEMESTER_SPLITTER_MONTH = 8;

    private static final Map<Integer, String> MAJORS_CODES = new HashMap<>() {{
        put(1, "M");
        put(2, "I");
        put(3, "MI");
        put(4, "MM");
        put(5, "IM");
        put(6, "MIM");
        put(7, "IG");
        put(8, "MIE");
        put(9, "IE");
    }};

    private final String yearAndSem = getSysYearAndSemester(); // "2019-2";

    private ArrayList<LocalTime[]> hourIntervals = null;

    public ArrayList<LocalTime[]> getHourIntervals() {
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
            LogPanel.logln("HIBA: A ! " + e);
        }

        return hourIntervals;
    }

    public ArrayList<Course> getCourses(String group) {
        System.out.println(getScheduleUrl(group));

        String[] headers = null;
        ArrayList<Course> courses = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(getScheduleUrl(group)).get();

            Elements tableRows = doc.select("tr");

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

                    courses.add(new Course(contentMap));
                }

                i++;
            }
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen verzió ellenõrzés! " + e);
        }

        return courses;
    }

    // assumes group num means [majorNum, studYear, groupId]
    private String getScheduleUrl(String group) {
        String url = getLinkPrefix();

        try {
            if(group.length() != 3) throw new Exception("HIBA: Az órarend link feldolgozó által kapott csoport szám hossza váratlan!");
            int groupNum = Integer.parseInt(group);

            int majorNum = groupNum / 100;
            String majorString = MAJORS_CODES.get(majorNum);
            if(majorString == null) throw new Exception("HIBA: Az órarend link feldolgozó által kapott csoport számnak megfelelõ szak nem talált!");

            int studYear = (groupNum / 10) % 10;

            url += "tabelar/" + majorString + studYear + ".html";
        }
        catch (NumberFormatException e) {
            System.out.println("HIBA: Az órarend link feldolgozó által kapott csoport szám váratlan karaktereket tartalmaz!");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    private String getLinkPrefix() {
        return "https://www.cs.ubbcluj.ro/files/orar/" + yearAndSem + "/";
    }

    // returns year-sem (ex. 2021-2)
    private String getSysYearAndSemester() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
        LocalDateTime now = LocalDateTime.now();
        String[] date = dtf.format(now).split("/");

        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);

        int semester = 2;
        if(month >= SEMESTER_SPLITTER_MONTH) {
            semester = 1;
        } else {
            year--;
        }

        return year + "-" + semester;
    }

}

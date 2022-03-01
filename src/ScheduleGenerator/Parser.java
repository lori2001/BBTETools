package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.models.Major;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ScheduleGenerator.data.SGData.*;

public class Parser {
    private Major major = null;
    private final String group;
    private int studYear = -1;

    public Parser(String group) {
        this.group = group;
        System.out.println(group);

        // process group num
        try {
            if(group.length() != 3) throw new Exception("HIBA: Az órarend link feldolgozó által kapott csoport szám hossza váratlan!");
            int groupNum = Integer.parseInt(group);

            int majorNum = groupNum / 100;
            major = MAJORS.get(majorNum);
            if(major == null) throw new Exception("HIBA: Az órarend link feldolgozó által kapott csoport számnak megfelelõ szak nem talált!");

            studYear = (groupNum / 10) % 10;
        }
        catch (NumberFormatException e) {
            System.out.println("HIBA: Az órarend link feldolgozó által kapott csoport szám váratlan karaktereket tartalmaz!");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Major getMajor() {
        return major;
    }

    public String getGroup() {
        return group;
    }

    public int getStudYear() {
        return studYear;
    }

    public String getTopLeftCont() {
        if(major.getCode() != null && studYear != -1 && getCalendarSemester() != -1) {
            return major.getCode() + "\n" + studYear + "." + getCalendarSemester();
        } else {
            LogPanel.logln("HIBA: A tanuló évfolyamának meghatározásakor");
        }
        return null;
    }

    public ArrayList<Course> getCourses() {
        String[] headers = null;
        ArrayList<Course> courses = new ArrayList<>();

        try {
            String url = getScheduleUrl();
            Document doc = Jsoup.connect(url).get();

            Elements groupElements = doc.select("h");
            int tableI = 0;
            for(Element gr : groupElements) {
                if(gr.text().equals(group)) break;
                tableI++;
            }

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

                    courses.add(new Course(contentMap));
                }

                i++;
            }
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen volt a tantárgyak beszerzése! " + e);
        }

        return courses;
    }

    // assumes group num means [majorNum, studYear, groupId]
    private String getScheduleUrl() {
        return genLinkPrefix() + "tabelar/" + major.getCode() + studYear + ".html";
    }

    private static ArrayList<LocalTime[]> hourIntervals = null;
    public static ArrayList<LocalTime[]> getHourIntervals() {
        if(hourIntervals != null) {
            return hourIntervals;
        }

        hourIntervals = new ArrayList<>();
        String url = genLinkPrefix() + "grafic/IM1.html";
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
                    + Arrays.toString(e.getStackTrace()));
        }

        return hourIntervals;
    }

    private static String genLinkPrefix() {
        return "https://www.cs.ubbcluj.ro/files/orar/" + YEAR_AND_SEM + "/";
    }

    public static ArrayList<String> genGroupsFor(String lang, String major, String studYear) {
        Integer majorNum = -1;
        for (Map.Entry<Integer, Major> set : MAJORS.entrySet()) {
            if(Objects.equals(set.getValue().getName(), major) && Objects.equals(set.getValue().getLang(), lang)) {
                majorNum = set.getKey();
            }
        }

        if(majorNum == -1) {
            LogPanel.logln("HIBA: A kiválasztott adatok alapján sikertelen volt kialakítani egy csoportot");
        }

        ArrayList<String> groups = new ArrayList<>();
        try {
            String url = genLinkPrefix() + "tabelar/" + MAJORS.get(majorNum).getCode() + studYear + ".html";
            System.out.println(url);
            Document doc = Jsoup.connect(url).get();

            Elements groupElements = doc.select("h1");
            for(int i = 1; i < groupElements.size(); i ++) {
                groups.add(findFirstInt(groupElements.get(i).text()));
            }
        } catch (Exception e) {
            LogPanel.logln("HIBA: A csoportokat nem sikerült beszerezni! " + e);
        }

        return groups;
    }

    // gets numbers from anywhere in the string
    // if it doesn't find any, returns null
    private static String findFirstInt(String stringToSearch) {
        Pattern integerPattern = Pattern.compile("-?\\d+");
        Matcher matcher = integerPattern.matcher(stringToSearch);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }


}

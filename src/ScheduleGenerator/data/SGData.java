package ScheduleGenerator.data;

import ScheduleGenerator.records.Major;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SGData {

    public static final String HWGInfo =
            "<html><center><h1>Órarend Generáló Infók</h1></center>" +
                    "<p>Ez az app egy nyomtatható heti órarendet<br>" +
                    "generál. Az egyetemi órarend nem esztétikus,<br>" +
                    "nem nyomtatható és nem igazán szabható személyre<br>" +
                    "sem. Az itt generált órarendek viszont igen.</p>" +
                    "<center><h2>Hogyan használható?</h2></center>" +
                    "<p>Válaszd ki a szakot majd a tantárgyakat<br>" +
                    "amiket bele szeretnél tenni az órarendedbe.<br>" +
                    "Az app generál neked egy órarendet a kíválasztott<br>" +
                    "tantárgyakkal<br>" +
                    "Ha az automatikus verzió nem jó, esetleg nem felel<br>" +
                    "meg a valóságnak, állíthatod egyenként is bármely óra<br>" +
                    "attribútumát.<br></p>" +
                    "<center><h2>Hogyan működik?</h2></center>" +
                    "<p>Az app bejárja a netten található órarendet<br>" +
                    "és elemzi azt. Innen építi vissza a személyre<br>" +
                    "szabott verziót. A megfelelõ mûködéshez kell<br>" +
                    "internet kapcsolat. Ha nincs nett, csak manuálisan<br>" +
                    "lehet új órákat hozzáadni az órarendhez.<br>" +
                    "Appen belül minden változtatás lementõdik az<br>" +
                    "órarend exportálásakor." +
                    "</html>";

    public static final HashMap<String, String> SUBJECT_NAME_ALIAS_MAP = new HashMap<>() {{
        put("Sisteme de operare", "Linux");
        put("Limba engleza (2)", "Angol");
        put("Analiza numerica", "Num. Analizis");
        put("Probabilitati", "Val. szám.");
        put("Didactica specialitatii", "Pedagógia");
        put("Sisteme de gestiune a bazelor de date", "Adatbázisok");
        put("Mecanica teoretica", "Mechanika");
        put("Programare orientata obiect", "OOP");
        put("Inteligenta artificiala", "Mest. int.");
        put("Programare Web", "Webprog.");
    }
    };

    public static final String[] DAYS_OF_WEEK_HU = { "Hé", "Ke", "Sze", "Csü", "Pé" };

    // before the 8th month it's considered 2nd semester of previous year
    // after this it' s considered first semester of given year
    public static final int SEMESTER_SPLITTER_MONTH = 8;

    public static final Map<Integer, Major> MAJORS = new HashMap<>() {{
        put(1, new Major("M", "Matek", "Román"));
        put(2, new Major("I", "Infó", "Román"));
        put(3, new Major("MI", "Matek-Infó", "Román"));
        put(4, new Major("MM", "Matek", "Magyar"));
        put(5, new Major("IM", "Infó", "Magyar"));
        put(6, new Major("MIM", "Matek-Infó", "Magyar"));
        put(7, new Major("IG", "Infó", "Német"));
        put(8, new Major("MIE", "Matek-Infó", "Angol"));
        put(9, new Major("IE", "Infó", "Angol"));
    }};

    public static final String[] LANGUAGE_OPTIONS = getLanguageOptions();
    private static String[] getLanguageOptions() {
        ArrayList<String> langs = new ArrayList<>();
        for (Map.Entry<Integer,Major> entry : MAJORS.entrySet()) {
            if(langs.stream().noneMatch(entry.getValue().getLang()::equals)) {
                langs.add(entry.getValue().getLang());
            }
        }

        String [] langsArr = new String[langs.size()];
        langsArr = langs.toArray(langsArr);
        return langsArr;
    }

    public static String[] getMajorOptionsFor(String language) {
        ArrayList<String> majors = new ArrayList<>();
        for (Map.Entry<Integer,Major> entry : MAJORS.entrySet()) {
            if(majors.stream().noneMatch(entry.getValue().getName()::equals)
                && entry.getValue().getLang().contains(language)) {
                majors.add(entry.getValue().getName());
            }
        }

        String [] majorsArr = new String[majors.size()];
        majorsArr = majors.toArray(majorsArr);
        return majorsArr;
    }

    private static int calendarSemester = -1;
    public static int getCalendarSemester() {
        return calendarSemester;
    }

    private static int calendarYear = -1;
    public static int getCalendarYear() {
        return calendarYear;
    }

    public static final String YEAR_AND_SEM = findYearAndSem();
    private static String findYearAndSem() {
        // get year and semester from pc's year
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

        calendarYear = year;
        calendarSemester = semester;

        return calendarYear + "-" + calendarSemester;
    }

    public static String getLinkPrefix() {
        return "https://www.cs.ubbcluj.ro/files/orar/" + YEAR_AND_SEM + "/";
    }

    public static final String[] STUD_YEAR_OPTIONS = new String[]{"1", "2", "3"};

    public static class Colors {
        public static final Color BACKGROUND_COLOR = new Color(211, 211, 211);
        public static final Color FONT_COLOR = new Color(218,218,218);
        public static final Color BASE_COLOR = new Color(57,57,57);
        public static final Color TEXT_BG_COLOR = new Color(0,0,0, 178);
        public static final Color[] COURSE_TYPE_COLORS = new Color[]{
                new Color(237, 28, 36),
                new Color(0, 166, 81),
                new Color(233, 127, 36)
        };
        public static final Color[] SUBJECT_COLORS = new Color[]{
                new Color(0, 166, 81),
                new Color(46,49, 146),
                new Color(233,127,36),
                new Color(237,28,36),
                new Color(17, 138, 178),
                new Color(239, 71, 111),
                new Color(252,68,15),
                new Color(67, 188 ,205),
                new Color(136,132,255),
                new Color(117,76,36),
                new Color(234,122,244),
                new Color(238, 169, 10),
                new Color(142,74,73),
                new Color(9, 103, 32),
                new Color(164,14,76),
                new Color(215, 36, 131)
        };
    }
}

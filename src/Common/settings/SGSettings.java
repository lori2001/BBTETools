package Common.settings;

import Common.logging.LogPanel;
import ScheduleGenerator.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;

public class SGSettings {
    private static final String filePath = "sgsettings.properties";

    private static String courses = "";

    private static final Gson gson = new Gson();

    public static void setCourses(ArrayList<Course> courses) {
        Gson gson = new Gson();
        SGSettings.courses = gson.toJson(courses);
    }
    public static ArrayList<Course> getCourses() {
        TypeToken<ArrayList<Course>> mapType = new TypeToken<>(){};
        return gson.fromJson(courses, mapType.getType());
    }

    public static void read() {
        try (InputStream input = new FileInputStream(filePath)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            courses = prop.getProperty("courses");

        } catch (IOException e) {
            LogPanel.logln("MEGJEGYZÉS: A kurzus file olvasása sikertelen. Új kurzusfile", SG_LOG_INSTANCE);
        }
    }

    public static void save() {
        try (OutputStream output = new FileOutputStream(filePath)) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("courses", courses);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException e) {
            LogPanel.logln("VIGYÁZAT: A kurzusfile mentése sikertelen. a táblázatban hozott változások elveszõdhetnek.", SG_LOG_INSTANCE);
            e.printStackTrace();
        }
    }
}

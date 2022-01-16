package settings;

import clsPresets.Classes;
import models.StudData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Settings {

    private static final String[] DEFAULTS = new String[] {
            "1",
            "Írd ide a neved",
            "621",
            "xyam123",
            "C:\\",
            "C:\\",
            Classes.Algoritmika.toString()
    };

    private static final String settingsFile = "assets/settings.txt";
    private static final String[] fileContent = new String[Setting.SIZE.ordinal()];

    public static void readFromFile() {
        // read input file
        try {
            File inFile = new File(settingsFile);

            // scan file into fileContent
            Scanner reader = new Scanner(inFile);
            int n = 0;
            while (reader.hasNextLine() && n < fileContent.length) {
                fileContent[n] = reader.nextLine();
                n++;
            }

            reader.close();
        } catch (IOException e) { // if no file
            System.out.println("Hiba történt a köv. file olvasásakor: " + settingsFile + ", a beállítások alapértelmezettre lesznek visszaállítva!");

            // RESET ALL to DEFAULTS
            System.arraycopy(DEFAULTS, 0, fileContent, 0, DEFAULTS.length);

            save();
        }
    }

    private static void save() {
        try {
            FileWriter writer = new FileWriter(settingsFile);

            Arrays.stream(fileContent).sequential().forEach(s -> {
                try {
                    writer.write(s + "\n");
                } catch (IOException err1) {
                    err1.printStackTrace();
                }
            });

            writer.close();
        } catch (IOException err2) {
            System.out.println("HIBA történt a köv. file készítésekor: "  + settingsFile);
            err2.printStackTrace();
        }
    }

    public static void saveToFile(StudData studData, String inputFold, String outputFold, String clsString) {
        fileContent[Setting.HwNum.ordinal()] = String.valueOf(studData.hwNum);
        fileContent[Setting.Name.ordinal()] = studData.name;
        fileContent[Setting.GroupNum.ordinal()] = String.valueOf(studData.group);
        fileContent[Setting.StudId.ordinal()] = studData.idStr;
        fileContent[Setting.InputFolder.ordinal()] = inputFold;
        fileContent[Setting.OutputFolder.ordinal()] = outputFold;
        fileContent[Setting.ClsPreset.ordinal()] = clsString;

        save();
    }

    public static String getFileContent(Setting setting) {
        return fileContent[setting.ordinal()];
    }

    public static void setFileContent(Setting setting, String val) {
        fileContent[setting.ordinal()] = val;
    }

    public static String getDefault(Setting setting) {
        return DEFAULTS[setting.ordinal()];
    }

    public static StudData getStudData() {
        return new StudData(
                Integer.parseInt(fileContent[Setting.HwNum.ordinal()]),
                fileContent[Setting.Name.ordinal()],
                Integer.parseInt(fileContent[Setting.GroupNum.ordinal()]),
                fileContent[Setting.StudId.ordinal()]);
    }
}

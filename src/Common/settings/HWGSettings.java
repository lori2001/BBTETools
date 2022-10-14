package Common.settings;

import HomeworkGatherer.clsPresets.Classes;
import Common.logging.LogPanel;
import Common.models.StudData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;

public class HWGSettings {

    private static final String[] DEFAULTS = new String[] {
            "1",
            "Írd ide a neved",
            "621",
            "xyam0123",
            "C:\\",
            "C:\\",
            Classes.Algoritmika.toString()
    };

    private static final String settingsFile = "assets/settings.txt";
    private static final String[] fileContent = new String[HWGSetting.SIZE.ordinal()];

    public static void read() {
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
            LogPanel.log("MEGJEGYZÉS: A " + settingsFile + " file olvasása helytelen, a beállítások alapértelmezettre lesznek visszaállítva!",
                    HWG_LOG_INSTANCE);

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
                    LogPanel.logln("HIBA: "  + settingsFile + " ba való íráskor!", HWG_LOG_INSTANCE);
                }
            });

            writer.close();
        } catch (IOException err2) {
            LogPanel.logln("HIBA: A következő file készítésekor: "  + settingsFile, HWG_LOG_INSTANCE);
        }
    }

    public static void saveToFile(StudData studData, String inputFold, String outputFold, String clsString) {
        fileContent[HWGSetting.HwNum.ordinal()] = studData.hwNum;
        fileContent[HWGSetting.Name.ordinal()] = studData.name;
        fileContent[HWGSetting.GroupNum.ordinal()] = studData.group;
        fileContent[HWGSetting.StudId.ordinal()] = studData.idStr;
        fileContent[HWGSetting.InputFolder.ordinal()] = inputFold;
        fileContent[HWGSetting.OutputFolder.ordinal()] = outputFold;
        fileContent[HWGSetting.ClsPreset.ordinal()] = clsString;

        save();
    }

    public static String getFileContent(HWGSetting HWGSetting) {
        return fileContent[HWGSetting.ordinal()];
    }

    /*public static void setFileContent(HWGSetting HWGSetting, String val) {
        fileContent[HWGSetting.ordinal()] = val;
    }

    public static String getDefault(HWGSetting HWGSetting) {
        return DEFAULTS[HWGSetting.ordinal()];
    }*/

    public static StudData getStudData() {
        return new StudData(
                fileContent[HWGSetting.HwNum.ordinal()],
                fileContent[HWGSetting.Name.ordinal()],
                fileContent[HWGSetting.GroupNum.ordinal()],
                fileContent[HWGSetting.StudId.ordinal()]);
    }
}

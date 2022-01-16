package clsPresets;

import models.StudData;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class ClsOOP extends ClsPreset {

    public ClsOOP(StudData studData) {
        super(studData);
        validExtensions = new String[]{"cpp", "h", "hpp"};
    }

    private String exerciseNrStr; // exercise number in string format

    @Override
    public String getNewFileName(String origName) {
        String ext = getExtension(origName);

        if(ext.equals("h") || ext.equals("hpp")) {
            return origName;
        }

        try {
            exerciseNrStr = genExerciseString(origName);
            NumberFormat.getInstance().parse(exerciseNrStr).intValue(); // check if name could be converted into number
        }
        catch (ParseException e) {
            return origName;
        }

        return genFileNamePrefix() + exerciseNrStr + "." + ext;
    }

    @Override
    public void processContent(String fileContent, String origName, FileWriter writer) throws IOException {
        String ext = getExtension(origName);
        // if(ext.equals("txt")) return; // do not process text files

        boolean hasComment = strExAsCommCpp(fileContent, studData.name) &&
                             strExAsCommCpp(fileContent, Integer.toString(studData.group)) &&
                             strExAsCommCpp(fileContent, exerciseNrStr);

        // write comments to start of file
        if(!hasComment) {
            writer.write(genComment());
        } else {
            System.out.println("NOTE: Comment found in " + origName + " -> no auto comment added!");
        }
    }

    @Override
    public boolean folderForEachFile() {
        return true;
    }

    @Override
    public boolean parentFolder() {
        return true;
    }

    @Override
    protected JLabel genLeftDesc() {
        return new JLabel(
                "<html>" +
                        "<h3>filenév:</h3>" +
                        "<p>n.cpp ->" + genFileNamePrefix() + "n.cpp</p>" +
                        "<h3>komment: </h3>" +
                        "<p>"  + genComment(studData.name, studData.group, "n", true) + "</p>" +
                        "</html>"
        );
    }

    @Override
    protected JLabel genRightDesc() {
        return new JLabel(
                "<html><ul>" +
                        "<li><h4>felismert kiterjesztések:<br>" +
                        Arrays.deepToString(validExtensions) + "</h4></li>" +
                        "<li><h4>kimenet: fileok a<br>megadott folderbe</h4></li>" +
                        "<li><h4>automatikus tesztelés</h4></li>" +
                        "</ul></html>"
        );
    }

    private boolean strExAsCommCpp(String cont, String str) {
        return stringExistsAsComment(cont, str, "//", "/*", "*/");
    }

    private boolean strExAsCommPas(String cont, String str) {
        return stringExistsAsComment(cont, str, "//", "{", "}");
    }

    private String genComment() {
        return genComment(studData.name, studData.group, exerciseNrStr, false);
    }

    private String genComment(String stdN, int grN, String exS, boolean isHtml) {
        String nL = "\n";
        if(isHtml) nL = "<br>";

        return "// " + stdN + nL +
                "//   " + grN + "-es csoport" + nL +
                "//   " + exS + ".Feladat" + nL + nL;
    }

    private final String[] fileNamePrefConsts = {"_L",  "_"};
    private String genFileNamePrefix() {
        return studData.idStr + fileNamePrefConsts[0] + studData.hwNum + fileNamePrefConsts[1];
    }
    // generates possible versions of file name prefixes to know what user input
    // to search for
    private String[] genFileNamePrefixVariations() {
        // saam0123_L05_
        // saam0123_L5_
        // saam123_L05_
        // saam123_L5_
        try{
            String s1 = studData.idStr.substring(0, 4);
            String s2 = studData.idStr.substring(4);

            int studIdNum =
                    NumberFormat.getInstance().parse(s2).intValue();

            // s2 with leading zeros
            String s2WithLz = String.format("%04d", studIdNum);
            String hwNumWithLz = String.format("%02d", studData.hwNum);

            return new String[] {
                    s1 + s2 + fileNamePrefConsts[0] + studData.hwNum + fileNamePrefConsts[1],
                    s1 + s2WithLz + fileNamePrefConsts[0] + hwNumWithLz + fileNamePrefConsts[1],
                    s1 + s2 + fileNamePrefConsts[0] + hwNumWithLz + fileNamePrefConsts[1],
                    s1 + s2WithLz + fileNamePrefConsts[0] + studData.hwNum + fileNamePrefConsts[1],
            };

        } catch (Exception e){
            return new String[]{genFileNamePrefix()};
        }
    }
    private String genExerciseString(String origName) {
        // try (number).ext
        String exerciseStr = "";
        try {
            exerciseStr = origName.substring(0, origName.lastIndexOf('.'));
            NumberFormat.getInstance().parse(exerciseStr).intValue(); // check if string can be parsed to int
        } catch (Exception e) {
            // try already properly named files
            try {
                boolean found = false;
                for(String variation : genFileNamePrefixVariations()) {
                    int prefixInd = origName.indexOf(variation);
                    if(prefixInd != -1) {
                        int numPos = prefixInd + variation.length();
                        exerciseStr = origName.substring(numPos, origName.lastIndexOf('.'));
                        try {
                            Integer.parseInt(exerciseStr); // check if string can be parsed to int
                            found = true;
                            break;
                        } catch (Exception ignored) {}
                    }
                }

                if(!found) throw new Exception("a file hibásan van elnevezve");
            } catch (Exception ex) {
                // if all renaming attempts fail warn the user and write file name as exerciseStr
                System.out.println("VIGYÁZAT: Erre a filera nem talált a feladat szám: " + origName);
                exerciseStr = origName.substring(0, origName.lastIndexOf('.'));
            }
        }

        return exerciseStr;
    }
}

package clsPresets;

import models.StudData;
import utils.FileProcessingUtils;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Arrays;

import static utils.FilenameUtils.getExtension;

public class ClsAlgoritmika extends ClsPreset{

    public ClsAlgoritmika(StudData studData) {
        super(studData);
        validExtensions = new String[]{"cpp", "c", "pas"};
    }

    private String exerciseNrStr; // exercise number in string format

    @Override
    public String getNewFileName(Path origPath) {
        exerciseNrStr = genExerciseString(origPath);
        return genFileNamePrefix() + exerciseNrStr + "." + getExtension(origPath.getFileName().toString());
    }

    @Override
    public String processContent(String fileContent, String origName) throws IOException {
        String ext = getExtension(origName);
        String newContent = null;

        boolean hasComment = false;
        if(ext.equals("cpp") || ext.equals("c")) {
            hasComment =
            strExAsCommCpp(fileContent, studData.name) &&
            strExAsCommCpp(fileContent, Integer.toString(studData.group)) &&
            strExAsCommCpp(fileContent, exerciseNrStr);
        }
        else if(ext.equals("pas")) {
            hasComment =
            strExAsCommPas(fileContent, studData.name) &&
            strExAsCommPas(fileContent, Integer.toString(studData.group)) &&
            strExAsCommPas(fileContent, exerciseNrStr);
        }

        // write comments to start of file
        if(!hasComment) {
            newContent = genComment();
        } else {
            System.out.println("MEGJEGYZÉS: Comment talált a " + origName + " fileban -> " +
                    "auto komment nem lesz hozzádva az ebbõl generált kimeneti filehoz!");
        }

        int exerciseNum = -1;
        try {
            exerciseNum = Integer.parseInt(exerciseNrStr);
        }
        catch (Exception ignored){}

        if(exerciseNum != -1) {
            String inFileInputText = "\"bemenet" + String.format("%02d", exerciseNum) + ".txt\"";
            String inFileOutputText = "\"kimenet" + String.format("%02d", exerciseNum) + ".txt\"";

            // check c++ file contents for ifstream and ofstream
            if(strExAsNotCommCpp(fileContent, "ifstream") && !strExAsNotCommCpp(fileContent, inFileInputText))
            {
                System.out.println("VIGYÁZAT: A " + origName + " fileban talált az 'ifstream', de nem talált a '"
                        + inFileInputText + "'! Ellenõrizd hogy a projekt megfelel-e a házi kritériumainak.");
            }
            if(strExAsNotCommCpp(fileContent, "ofstream") && !strExAsNotCommCpp(fileContent, inFileOutputText)) {
                System.out.println("VIGYÁZAT: A " + origName + " fileban talált 'ifstream', de nem talált '"
                        + inFileOutputText + "'! Ellenõrizd hogy a projekt megfelel-e a házi kritériumainak.");
            }
        }

        // add back original contents to file
        newContent += fileContent;

        return newContent;
    }

    @Override
    public boolean folderForEachFile() {
        return false;
    }

    @Override
    public String getParentZipName() {
        return null;
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
                "<li><h4>bemenet/kimenet \".txt\" ellenõrzés</h4></li>" +
            "</ul></html>"
        );
    }

    private boolean strExAsCommCpp(String cont, String str) {
        return FileProcessingUtils.stringExistsAsComment(cont, str, "//", "/*", "*/");
    }
    private boolean strExAsNotCommCpp(String cont, String str) {
        return FileProcessingUtils.stringExistsAndIsNotComment(cont, str, "//", "/*", "*/");
    }

    private boolean strExAsCommPas(String cont, String str) {
        return FileProcessingUtils.stringExistsAsComment(cont, str, "//", "{", "}");
    }

    private String genComment() {
        return genComment(studData.name, studData.group, exerciseNrStr, false);
    }

    private String genComment(String stdN, int grN, String exS, boolean isHtml) {
        String nL = "\n";
        if(isHtml) nL = "<br>";

        return "// " + stdN + nL +
               "// " + grN + "-es csoport" + nL +
               "// " + exS + ".Feladat" + nL + nL;
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

    private String genExerciseString(Path origPath) {
        // try (number).ext
        String origName = origPath.getFileName().toString();
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
                System.out.println("VIGYÁZAT: Erre a filera nem talált a feladat szám: " + origPath);
                exerciseStr = origName.substring(0, origName.lastIndexOf('.'));
            }
        }

        return exerciseStr;
    }
}

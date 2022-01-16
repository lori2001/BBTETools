package clsPresets;

import models.StudData;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public abstract class ClsPreset {
    protected StudData studData = new StudData();
    protected String[] validExtensions;

    public ClsPreset(StudData sd) {
        studData = sd;
    }

    // ----- ABSTRACT CLASSES ---------
    public abstract String getNewFileName(String origName);
    public abstract void processContent(String fileContent, String origName, FileWriter writer) throws IOException;
    public abstract boolean folderForEachFile();
    public abstract boolean parentFolder();
    protected abstract JLabel genLeftDesc();
    protected abstract JLabel genRightDesc();
    // ----- ABSTRACT CLASSES ---------

    // ----- PUBLIC CLASSES ---------
    public void addDescriptionToPanel(JPanel panel) {
        panel.setLayout(new GridLayout(1, 2, 5, 0));

        JLabel leftDesc = genLeftDesc();
        leftDesc.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel rightDesc = genRightDesc();
        rightDesc.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        panel.add(leftDesc);
        panel.add(rightDesc);
    }

    public boolean extensionIsValid(String filePath) {
        return Arrays.asList(validExtensions).contains(getExtension(filePath));
    }

    public void setStudData(StudData studData) {
        this.studData = studData;
    }
    // ----- PUBLIC CLASSES ---------

    // ----- UTILITY CLASSES ---------
    protected String getExtension(String f) {
        return f.substring(f.lastIndexOf(".") + 1);
    }

    // params: file content, string to be searched, one line comment mark,
    // multi line start comment mark, multi line end comment mark
    // MIGHT GET CONFUSED BY "//*" strings
    protected boolean stringExistsAsComment(String content, String str, String olComm, String sComm, String eComm) {
        int strPos = content.indexOf(str); // check if string exists
        boolean existsAndIsInComm = false;

        if(strPos != -1) { // if string is found
            if(content.substring(0, strPos).contains(sComm) && content.substring(strPos).contains(eComm))
            {
                existsAndIsInComm = true;
            } else {
                // search string's line
                int newLineInd = 0;
                for(int i = strPos; i >= 0 && content.charAt(i) != '\n'; i--) {
                    newLineInd = i;
                }

                if(content.substring(newLineInd, strPos).contains(olComm)) {
                    existsAndIsInComm = true;
                }
            }
        }

        return existsAndIsInComm;
    }
    // ----- UTILITY CLASSES ---------

}

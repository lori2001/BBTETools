package clsPresets;

import models.StudData;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static utils.FilenameUtils.getExtension;

public abstract class ClsPreset {
    protected StudData studData;
    protected String[] validExtensions;

    public ClsPreset(StudData sd) {
        studData = sd;
    }

    // ----- ABSTRACT CLASSES ---------
    public abstract String getNewFileName(Path origPath);
    public abstract String processContent(String fileContent, String origName) throws IOException;
    public abstract boolean folderForEachFile();
    // return null if you don't want zipping
    public abstract String getParentZipName();
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
}

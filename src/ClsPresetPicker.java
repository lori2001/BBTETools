import clsPresets.Classes;
import clsPresets.ClsAlgoritmika;
import clsPresets.ClsOOP;
import clsPresets.ClsPreset;
import logging.LogPanel;
import models.Vec;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

public class ClsPresetPicker extends JComboBox<String> {
    private ClsPreset activeClsPreset;
    private String activeSelection;

    Object[] classes = Classes.Algoritmika.getDeclaringClass().getEnumConstants();

    public ClsPresetPicker(Vec pos, Vec size, String initialSelection) {
        Arrays.stream(classes).map(Object::toString).forEach(this::addItem);

        setSelectedItem(initialSelection);
        setBounds(pos.x, pos.y, size.x, size.y);

        // align comboBox to center
        DefaultListCellRenderer listRenderer;
        listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        setRenderer(listRenderer);
    }

    public String getActiveClsString() {
        return activeSelection;
    }

    public ClsPreset getClsPreset(StudInfo studInfo){
        String currSelection = Objects.requireNonNull(getSelectedItem()).toString();

        if(!currSelection.equals(activeSelection)) {
            if (Classes.Algoritmika.toString().equals(currSelection)) {
                activeClsPreset = new ClsAlgoritmika(studInfo.getStudData());
            } else if (Classes.OOP.toString().equals(currSelection)) {
                activeClsPreset = new ClsOOP(studInfo.getStudData());
            }
            else {
                LogPanel.logln("HIBA: ez a \"class preset\" nem létezik");
                System.exit(1);
            }
            activeSelection = currSelection;
        }

        return activeClsPreset;
    }
}

package ScheduleGenerator;

import Common.logging.LogPanel;
import Common.settings.HWGSettings;
import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.Major;
import ScheduleGenerator.utils.Convert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.*;

public class ControlPanel extends JPanel {
    private final JComboBox<String> languageSelector = new JComboBox<>(
            LANGUAGE_OPTIONS
    );
    private final JComboBox<String> yearSelector = new JComboBox<>(
            STUD_YEAR_OPTIONS
    );

    private final JComboBox<String> majorSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> majorsModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> groupSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> groupModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> subGroupSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> subGroupModel = new DefaultComboBoxModel<>();

    private final JPanel dateSettings = new JPanel(new GridLayout(2, 2, 5, 0));
    private final JTextField parseYear = new JTextField(4);
    private final JTextField parseSemester = new JTextField(1);

    private static boolean actionTaken = false;

    public ControlPanel() {
        setLayout(new GridLayout(2, 6, 10, 0));

        String initGroup = HWGSettings.getStudData().group;
        Major initMajor = Convert.groupToMajor(initGroup);
        String initStudYear = Convert.groupToStudYear(initGroup);

        JLabel languageL = new JLabel("Nyelv:");
        JLabel majorL = new JLabel("Szak:");
        JLabel yearL = new JLabel("Tanulói év:");
        JLabel groupL = new JLabel("Csoport:");
        JLabel subGroupL = new JLabel("Alcsoport:");
        JLabel scheduleYearL = new JLabel("Órarend év:");
        JLabel semesterL = new JLabel("Félév:");

        parseYear.setText(String.valueOf(SGData.getCalendarYear()));
        parseSemester.setText(String.valueOf(SGData.getCalendarSemester()));
        parseYear.setEditable(false);
        parseSemester.setEditable(false);

        add(scheduleYearL);
        add(semesterL);
        add(languageL);
        add(majorL);
        add(yearL);
        add(groupL);
        add(subGroupL);

        yearSelector.setSelectedItem(initStudYear);
        languageSelector.setSelectedItem(initMajor.getLang());

        majorSelector.setModel(majorsModel);
        genMajorOptions();
        majorSelector.setSelectedItem(initMajor.getName());

        groupSelector.setModel(groupModel);
        genGroupOptions();
        groupSelector.setSelectedItem(initGroup);

        subGroupSelector.setModel(subGroupModel);
        genSubGroupOptions();
        subGroupSelector.setSelectedIndex(0);

        languageSelector.addActionListener(e -> {
            if(!actionTaken) {
                actionTaken = true;
                genMajorOptions();
                genGroupOptions();
                genSubGroupOptions();
                actionTaken = false;
            }
        });
        majorSelector.addActionListener(e -> {
            if(!actionTaken) {
                actionTaken = true;
                genGroupOptions();
                genSubGroupOptions();
                actionTaken = false;
            }
        });
        yearSelector.addActionListener(e -> {
            if(!actionTaken) {
                actionTaken = true;
                genGroupOptions();
                genSubGroupOptions();
                actionTaken = false;
            }
        });
        groupSelector.addActionListener(e -> {
            if(!actionTaken) {
                actionTaken = true;
                genSubGroupOptions();
                actionTaken = false;
            }
        });

        add(parseYear);
        add(parseSemester);
        add(languageSelector);
        add(majorSelector);
        add(yearSelector);
        add(groupSelector);
        add(subGroupSelector);
    }

    public void addActionListener(ActionListener actionListener) {
        subGroupSelector.addActionListener(actionListener); // this field depends on every other field
    }

    public String getGroup() {
        return (String) groupSelector.getSelectedItem();
    }
    public String getSubGroup() {
        return (String) subGroupSelector.getSelectedItem();
    }

    private void genMajorOptions() {
        String selectedLang = (String) languageSelector.getSelectedItem();
        String[] majors = SGData.getMajorOptionsFor(selectedLang);

        if(majors.length != 0) {
            // removing old data
            majorsModel.removeAllElements();
            for (String major : majors) {
                majorsModel.addElement(major);
            }
        }else {
            LogPanel.logln("HIBA: Az adott nyelvre nem találtak a szakok", SG_LOG_INSTANCE);
        }
    }

    private void genGroupOptions()
    {
        ArrayList<String> groups = Parser.genGroupsFor(
                (String) languageSelector.getSelectedItem(),
                (String) majorSelector.getSelectedItem(),
                (String) yearSelector.getSelectedItem()
        );

        if(groups != null && groups.size() != 0) {
            // removing old data
            groupModel.removeAllElements();
            for (String gr : groups) {
                groupModel.addElement(gr);
            }
        } else {
            LogPanel.logln("HIBA: Az adott szakra nem találtak a csoportok", SG_LOG_INSTANCE);
        }
    }

    private void genSubGroupOptions() {
        ArrayList<String> subgroups = Parser.genSubGroupsFor(
                (String) languageSelector.getSelectedItem(),
                (String) majorSelector.getSelectedItem(),
                (String) yearSelector.getSelectedItem(),
                (String) groupSelector.getSelectedItem());

        if(subgroups != null && subgroups.size() != 0) {
            // removing old data
            subGroupModel.removeAllElements();
            for (String major : subgroups) {
                subGroupModel.addElement(major);
            }
        } else {
            LogPanel.logln("HIBA: Az adott csoportnak nem találtak az alcsoportok", SG_LOG_INSTANCE);
        }
    }

}

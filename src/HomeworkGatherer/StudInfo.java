package HomeworkGatherer;

import HomeworkGatherer.logging.LogPanel;
import HomeworkGatherer.models.StudData;
import HomeworkGatherer.models.Vec;
import HomeworkGatherer.settings.Setting;
import HomeworkGatherer.settings.Settings;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Objects;

public class StudInfo extends JPanel {
    private final JTextField hwNum = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField groupNum = new JTextField();
    private final JTextField studId = new JTextField();

    private String loc;

    public StudInfo(Vec pos, Vec size) {
        setLayout(new GridLayout(2, 4, 10 ,0));
        setBounds(pos.x, pos.y, size.x, size.y);

        StudData sd = Settings.getStudData();
        hwNum.setText(Integer.toString(sd.hwNum));
        name.setText(sd.name);
        groupNum.setText(Integer.toString(sd.group));
        studId.setText(sd.idStr);

        int elemHeight = size.y / 2;

        JLabel hwNumL = new JLabel("Házi sorszám:");
        JLabel nameL = new JLabel("Név:");
        JLabel groupNumL = new JLabel("Csoport Szám:");
        JLabel studIdL = new JLabel("Azonosító");

        // highlight this as it's most important
        hwNum.setBackground(new Color(255, 172, 13));

        add(hwNumL);
        add(nameL);
        add(groupNumL);
        add(studIdL);

        add(hwNum);
        add(name);
        add(groupNum);
        add(studId);
    }

    public void addDocumentListener(DocumentListener documentListener) {
        hwNum.getDocument().addDocumentListener(documentListener);
        name.getDocument().addDocumentListener(documentListener);
        groupNum.getDocument().addDocumentListener(documentListener);
        studId.getDocument().addDocumentListener(documentListener);
    }

    public StudData getStudData() {
        return new StudData(
                getHwNum(),
                getStudName(),
                getGroupNum(),
                getStudId()
        );
    }

    private String getStudName() {
        return name.getText();
    }

    private int getHwNum() {
        if(Objects.equals(hwNum.getText(), "")) {
            return 0;
        }

        try {
            return Integer.parseInt(hwNum.getText());
        } catch (NumberFormatException e) {
            hwNum.setText(Settings.getDefault(Setting.HwNum));
            LogPanel.logln("HIBA: Sikertelen számmá alakítás: " + hwNum.getText() + " (házi sorszám)");
            return Integer.parseInt(Settings.getDefault(Setting.HwNum));
        }
    }

    private int getGroupNum() {
        if(Objects.equals(groupNum.getText(), "")) {
            return 0;
        }

        try {
            return Integer.parseInt(groupNum.getText());
        } catch (NumberFormatException e) {
            hwNum.setText(Settings.getDefault(Setting.GroupNum));
            LogPanel.logln("HIBA: Sikertelen számmá alakítás: " + groupNum.getText() + " (tanuló csoportszám)");
            return Integer.parseInt(Settings.getDefault(Setting.GroupNum));
        }
    }

    private String getStudId() {
        return studId.getText();
    }
}

package HomeworkGatherer;

import Common.logging.LogPanel;
import Common.models.StudData;
import Common.settings.HWGSettings;
import HomeworkGatherer.utils.DocumentChanged;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusListener;
import java.util.regex.Pattern;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;

public class StudInfo extends JPanel {
    private final JTextField hwNum = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField groupNum = new JTextField();
    private final JTextField studId = new JTextField();

    private static final Pattern STUD_ID_FORMAT = Pattern.compile("[a-z]{4}[0-9]{4}");

    private final Border DEFAULT_BORDER;
    private final Border WARN_BORDER = new LineBorder(Color.RED, 2, true);

    private String groupWarnMsg, hwNumWarnMsg, studIdWarnMsg;

    public StudInfo(Point pos, Point size) {
        setLayout(new GridLayout(2, 4, 10, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        DEFAULT_BORDER = hwNum.getBorder();

        StudData sd = HWGSettings.getStudData();
        hwNum.setText(sd.hwNum);
        name.setText(sd.name);
        groupNum.setText(sd.group);
        studId.setText(sd.idStr);

        JLabel hwNumL = new JLabel("Házi sorszám:");
        JLabel nameL = new JLabel("Név:");
        JLabel groupNumL = new JLabel("Csoport Szám:");
        JLabel studIdL = new JLabel("Azonosító");

        // highlight this as it's most important
        hwNum.setBackground(new Color(36, 168, 217));

        add(hwNumL);
        add(nameL);
        add(groupNumL);
        add(studIdL);

        add(hwNum);
        add(name);
        add(groupNum);
        add(studId);

        inputChecker(); // check right after loading
        addDocumentListener(new DocumentChanged() {
            @Override
            public void onContentChange(DocumentEvent e) {
                inputChecker();
            }
        });
    }

    public void addDocumentListener(DocumentListener documentListener) {
        hwNum.getDocument().addDocumentListener(documentListener);
        name.getDocument().addDocumentListener(documentListener);
        groupNum.getDocument().addDocumentListener(documentListener);
        studId.getDocument().addDocumentListener(documentListener);
    }

    public void addFocusListener(FocusListener focusListener) {
        hwNum.addFocusListener(focusListener);
        name.addFocusListener(focusListener);
        groupNum.addFocusListener(focusListener);
        studId.addFocusListener(focusListener);
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

    private String getHwNum() {
        return hwNum.getText();
    }

    private String getGroupNum() {
        return groupNum.getText();
    }

    private String getStudId() {
        return studId.getText();
    }

    private void inputChecker() {
        String gN = groupNum.getText();
        groupWarnMsg = null;
        if (gN.length() != 3) {
            groupWarnMsg = "VIGYÁZAT: A csoportszám várt hossza: 3, a kapott hossz:" + gN.length();
        }

        try {
            Integer.parseInt(gN);
        } catch (Exception err) {
            groupWarnMsg = "VIGYÁZAT: A csoportszám(" + gN + ") nem alakítható számmá!";
        }

        if (groupWarnMsg == null) groupNum.setBorder(DEFAULT_BORDER);
        else groupNum.setBorder(WARN_BORDER);

        hwNumWarnMsg = null;
        try {
            Integer.parseInt(hwNum.getText());
        } catch (Exception err) {
            hwNumWarnMsg = "VIGYÁZAT: A laborszám(" + hwNum.getText() + ") nem alakítható számmá!";
        }

        if (hwNumWarnMsg == null) hwNum.setBorder(DEFAULT_BORDER);
        else hwNum.setBorder(WARN_BORDER);

        studIdWarnMsg = null;

        if(!STUD_ID_FORMAT.matcher(studId.getText()).matches()) {
            studIdWarnMsg = "VIGYÁZAT: A tanuló azonosító(" + hwNum.getText() + ") nincs elvárt formátumban (abcd1234)!";
        }

        if (studIdWarnMsg == null) studId.setBorder(DEFAULT_BORDER);
        else studId.setBorder(WARN_BORDER);
    }

    public void printErrMsges() {
        if(groupWarnMsg != null)
            LogPanel.logln(groupWarnMsg, HWG_LOG_INSTANCE);
        if(hwNumWarnMsg != null)
            LogPanel.logln(hwNumWarnMsg, HWG_LOG_INSTANCE);
        if(studIdWarnMsg != null)
            LogPanel.logln(studIdWarnMsg, HWG_LOG_INSTANCE);
    }

}

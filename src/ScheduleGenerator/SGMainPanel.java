package ScheduleGenerator;

import Common.InfoButton;
import Common.ScrollableSoloPane;
import Common.logging.LogPanel;
import Common.settings.SGSettings;
import ScheduleGenerator.data.SGData;
import ScheduleGenerator.graphics.ScheduleDrawer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class SGMainPanel extends JPanel {
    public static final int SG_LOG_INSTANCE = LogPanel.createNewInstance("Nincs üzenet. Építsd fel az órarended a táblázatban, majd töltsd le a \"Letöltés\" gombbal");

    private final CoursesTable coursesTable;

    private final ScheduleDrawer scheduleDrawer;

    private void updateCoursesByControlPanel(String group, String subGroup) {
        SGSettings.setCourses(Parser.genCourses(group, subGroup));
        coursesTable.setData(SGSettings.getCourses());
        if(coursesTable.getCourses() != null) {
            scheduleDrawer.repaintWithNewProps(Parser.getHourIntervals(), coursesTable.getCourses(), group, subGroup);
        }
    }

    public SGMainPanel(JFrame appFrame, Point panelSize) {
        setLayout(null);
        setBounds(0,0, panelSize.x, panelSize.y);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15 ,0));
        topPanel.setBounds(0,0, getWidth(),55);
        ControlPanel controlPanel = new ControlPanel();
        topPanel.add(controlPanel);

        InfoButton infoButton =
                new InfoButton(new Point(0, 0), new Point(35, 35), appFrame, SGData.HWGInfo);
        topPanel.add(infoButton);

        JPanel logPanel = new JPanel();
        logPanel.setBounds(275, panelSize.y - 150, 550, 120);
        LogPanel.getScrollableTextArea(SG_LOG_INSTANCE).setPreferredSize(new Dimension(550, 120));
        logPanel.add(LogPanel.getScrollableTextArea(SG_LOG_INSTANCE));

        ScrollableSoloPane scrollableSoloPane = new ScrollableSoloPane();
        scrollableSoloPane.setBounds(5, 60, getWidth() - 29, 450);
        scheduleDrawer =
                new ScheduleDrawer(
                        new Point2D.Double((double)scrollableSoloPane.getWidth() / 2 - 594.0 / 2, 0),
                        new Point2D.Double(594, 420)
                );

        coursesTable = new CoursesTable(scrollableSoloPane.getBounds());

        scrollableSoloPane.addTab(scheduleDrawer, "Elônézet", "Csökkentett felbontású elõnézet a kimeneti órarendrõl.");
        scrollableSoloPane.addTab(coursesTable, "Táblázat", "A felvett órák változtatható táblázata/");

        coursesTable.addTableModelListener(e -> {
            if(!coursesTable.settingDataIsInProgress()) {
                SGSettings.setCourses(coursesTable.getCourses());
                scheduleDrawer.repaintWithNewProps(Parser.getHourIntervals(), SGSettings.getCourses(), controlPanel.getGroup(), controlPanel.getSubGroup());
            }
        });

        if(SGSettings.getCourses() != null) { // get course data from settings
            coursesTable.setData(SGSettings.getCourses());
            if(coursesTable.getCourses() != null) {
                scheduleDrawer.repaintWithNewProps(Parser.getHourIntervals(), coursesTable.getCourses(), controlPanel.getGroup(), controlPanel.getSubGroup());
            }
        } else { // get course data from control panel
            updateCoursesByControlPanel(controlPanel.getGroup(), controlPanel.getSubGroup());
        }

        controlPanel.addActionListener(e -> {
            if(controlPanel.getGroup() != null && controlPanel.getSubGroup() != null) {
                updateCoursesByControlPanel(controlPanel.getGroup(), controlPanel.getSubGroup());
            }
        });

        JButton downloadSchedule = new JButton("Letöltés");
        downloadSchedule.setBounds(5, getHeight() - 57, 150, 25);
        downloadSchedule.addActionListener(e -> repaint());

        downloadSchedule.addActionListener(e -> {
            try {
                saveSchedule();
            } catch (Exception ex) {
                LogPanel.logln(Arrays.toString(ex.getStackTrace()), SG_LOG_INSTANCE);
            }
        });

        add(topPanel);
        add(logPanel);
        add(scrollableSoloPane);
        add(downloadSchedule);

        setVisible(true);
    }

    public void saveSchedule() throws Exception {
        JFileChooser chooser;
        chooser = new JFileChooser();
        chooser.setDialogTitle("Órarend lementése");

        // choose directory
        chooser.setSelectedFile(new File("orarend.png"));
        int userSelection = chooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if(!path.endsWith(".png")) path += ".png";

            ScheduleDrawer highRes = scheduleDrawer.getHighResVersion();
            Dimension d = highRes.getSize();
            BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            highRes.paintComponent(g2d);
            g2d.dispose();
            ImageIO.write(image, "png", new File(path));
        }
    }


}

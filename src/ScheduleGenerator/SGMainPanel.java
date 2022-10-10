package ScheduleGenerator;

import Common.InfoButton;
import Common.ScrollableSoloPane;
import Common.logging.LogPanel;
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

    private final Parser parser;
    private final SubjectsTable subjectsTable;

    private final ScheduleDrawer scheduleDrawer;

    private static final String HWGInfo =
            "<html><center><h1>Órarend Generáló Infók</h1></center>" +
            "<p>Ez az app egy nyomtatható heti órarendet<br>" +
            "generál. Az egyetemi órarend nem esztétikus,<br>" +
            "nem nyomtatható és nem igazán szabható személyre<br>" +
            "sem. Az itt generált órarendek viszont igen.</p>" +
            "<center><h2>Hogyan használható?</h2></center>" +
            "<p>Válaszd ki a szakot majd a tantárgyakat<br>" +
            "amiket bele szeretnél tenni az órarendedbe.<br>" +
            "Az app generál neked egy órarendet a kíválasztott<br>" +
            "tantárgyakkal<br>" +
            "Ha az automatikus verzió nem jó, esetleg nem felel<br>" +
            "meg a valóságnak, állíthatod egyenként is bármely óra<br>" +
            "attribútumát.<br></p>" +
            "<center><h2>Hogyan működik?</h2></center>" +
            "<p>Az app bejárja a netten található órarendet<br>" +
            "és elemzi azt. Innen építi vissza a személyre<br>" +
            "szabott verziót. A megfelelõ mûködéshez kell<br>" +
            "internet kapcsolat. Ha nincs nett, csak manuálisan<br>" +
            "lehet új órákat hozzáadni az órarendhez.<br>" +
            "Appen belül minden változtatás lementõdik az<br>" +
            "órarend exportálásakor." +
            "</html>";

    public SGMainPanel(JFrame appFrame, Point panelSize, String defaultGroup) {
        setLayout(null);
        setBounds(0,0, panelSize.x, panelSize.y);

        final String defaultSubgroup = "1";
        System.out.println("Default group/subgroup " + defaultGroup + " " + defaultSubgroup);
        parser = new Parser(defaultGroup, defaultSubgroup);

        ScrollableSoloPane scrollableSoloPane = new ScrollableSoloPane();
        scrollableSoloPane.setBounds(5, 60, getWidth() - 29, 450);

        scheduleDrawer =
                new ScheduleDrawer(
                        new Point2D.Double((double)scrollableSoloPane.getWidth() / 2 - 594.0 / 2, 0),
                        new Point2D.Double(594, 420),
                        GlobalParser.getHourIntervals()
                );
        scheduleDrawer.setSpecificProps(parser.getTopLeftContent(), parser.getCourses());
        scrollableSoloPane.addTab(scheduleDrawer, "Elônézet", "Csökkentett felbontású elõnézet a kimeneti órarendrõl.");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25 ,0));
        topPanel.setBounds(50,0,getWidth() - 120,55);
        ControlPanel controlPanel = new ControlPanel(parser.getMajor(), parser.getGroup(), parser.getStudYear(), parser.getSubGroup());
        topPanel.add(controlPanel);

        InfoButton infoButton =
                new InfoButton(new Point(0, 0), new Point(35, 35), appFrame, HWGInfo);
        topPanel.add(infoButton);

        subjectsTable = new SubjectsTable(parser.getCourses(), new Rectangle(10,60, 660, 205));

        controlPanel.addActionListener(e -> {
            String selGroup = controlPanel.getSelectedGroup();
            String selSubGroup = controlPanel.getSelectedSubGroup();
            if(selGroup != null && selSubGroup != null) {
                parser.reparseCourses(selGroup, selSubGroup);
                subjectsTable.setData(parser.getCourses());
                scheduleDrawer.setSpecificProps(parser.getTopLeftContent(), parser.getCourses());
                repaint();
            }
        });

        subjectsTable.addTableModelListener(e -> {
            if(!subjectsTable.settingDataIsInProgress()) scheduleDrawer.setSpecificProps(parser.getTopLeftContent(), subjectsTable.getCourses());
            repaint();
        });
        scrollableSoloPane.addTab(subjectsTable.getScrollPane(), "Táblázat", "A felvett órák változtatható táblázata/");

        // displays clsPresetDesc and Logs in a TabbedPane
        JPanel logPanel = new JPanel();
        logPanel.setBounds(275, panelSize.y - 150, 550, 120);
        LogPanel.getScrollableTextArea(SG_LOG_INSTANCE).setPreferredSize(new Dimension(550, 120));
        logPanel.add(LogPanel.getScrollableTextArea(SG_LOG_INSTANCE));
        // scrollableSoloPane.addTab(LogPanel.getScrollableTextArea(SG_LOG_INSTANCE), "Üzenetek", "A generálási folyamatról ír ki hasznos infókat");

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

        add(logPanel);
        add(scrollableSoloPane);
        add(topPanel);
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

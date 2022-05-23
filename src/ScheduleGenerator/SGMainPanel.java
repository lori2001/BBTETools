package ScheduleGenerator;

import Common.InfoButton;
import Common.logging.LogPanel;
import ScheduleGenerator.graphics.ScheduleDrawer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class SGMainPanel extends JPanel {
    public static final int SG_LOG_INSTANCE = LogPanel.createNewInstance();

    private final Parser parser;
    private final SubjectsTable subjectsTable;

    private final ScheduleDrawer scheduleDrawer;
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        scheduleDrawer.paintComponent(g);
    }

    public SGMainPanel(JFrame appFrame, Point panelSize, String defaultGroup) {
        setLayout(null);
        setBounds(0,0, panelSize.x, panelSize.y);

        parser = new Parser(defaultGroup, "1");

        scheduleDrawer =
                new ScheduleDrawer(
                        new Point2D.Double(5, panelSize.y - 240),
                        new Point2D.Double(297, 210),
                        GlobalParser.getHourIntervals()
                );
        scheduleDrawer.setSpecificProps(parser.getTopLeftContent(), parser.getCourses());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25 ,0));
        topPanel.setBounds(50,0,panelSize.x - 120,55);
        ControlPanel controlPanel = new ControlPanel(parser.getMajor(), parser.getGroup(), parser.getStudYear(), parser.getSubGroup());
        topPanel.add(controlPanel);
        String HWGInfo = "<html><center><h1>Órarend Generáló Infók</h1></center>" +
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
        InfoButton infoButton =
                new InfoButton(new Point(0, 0), new Point(35, 35), appFrame, HWGInfo);
        topPanel.add(infoButton);
        add(topPanel);

        subjectsTable = new SubjectsTable(parser.getCourses(), new Rectangle(10,60, 660, 205));
        add(subjectsTable.getScrollPane());

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
            if(!subjectsTable.settingDataIsInProgress())
            scheduleDrawer.setSpecificProps(parser.getTopLeftContent(), subjectsTable.getCourses());
            repaint();
        });

        // displays clsPresetDesc and Logs in a TabbedPane
        JPanel scrollableSoloPane = new JPanel();
        scrollableSoloPane.setBounds(310, panelSize.y - 245, 360, 215);
        LogPanel.getScrollableTextArea(SG_LOG_INSTANCE).setPreferredSize(new Dimension(360, 210));
        scrollableSoloPane.add(LogPanel.getScrollableTextArea(SG_LOG_INSTANCE));
        // scrollableSoloPane.addTab(LogPanel.getScrollableTextArea(SG_LOG_INSTANCE), "Üzenetek", "A generálási folyamatról ír ki hasznos infókat");
        add(scrollableSoloPane);

        JButton downloadSchedule = new JButton("Letöltés");
        downloadSchedule.setBounds(212, panelSize.y - 65, 80, 25);
        downloadSchedule.setBackground(new Color(255,255,255, 0));
        downloadSchedule.setOpaque(false);
        downloadSchedule.addActionListener(e -> repaint());

        add(downloadSchedule);
        downloadSchedule.addActionListener(e -> {
            try {
                saveSchedule();
            } catch (Exception ex) {
                LogPanel.logln(Arrays.toString(ex.getStackTrace()), SG_LOG_INSTANCE);
            }
        });

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

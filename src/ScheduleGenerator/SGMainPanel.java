package ScheduleGenerator;

import Common.logging.LogPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class SGMainPanel extends JPanel {
    Parser parser;

    ScheduleDrawer scheduleDrawer =
            new ScheduleDrawer(
                    new Point2D.Double(30, 100),
                    new Point2D.Double(1.5, 1.5),
                    Parser.getHourIntervals()
            );

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        scheduleDrawer.paintComponent(g);
    }

    public SGMainPanel(JFrame appFrame, Point appSize) {
        setLayout(new FlowLayout());
        setBounds(0,0, appSize.x, appSize.y);

        JButton btn = new JButton("Generálás");
        btn.addActionListener(e -> repaint());

        add(btn);
        btn.addActionListener(e -> {
            try {
                saveSchedule();
            } catch (Exception ex) {
                LogPanel.logln(Arrays.toString(ex.getStackTrace()));
            }
        });

        parser = new Parser("621");
        scheduleDrawer.setSpecificProps(parser.getTopLeftCont(), parser.getCourses());

        ControlPanel controlPanel = new ControlPanel(new Point(0,0), new Point(300,50),
                parser.getMajor(), parser.getGroup(), Integer.toString(parser.getStudYear()));
        add(controlPanel);

        controlPanel.addActionListener(e -> {
            String selGroup = controlPanel.getSelectedGroup();
            if(selGroup != null) {
                System.out.println("sel group" + selGroup);
                parser = new Parser(selGroup);
                scheduleDrawer.setSpecificProps(parser.getTopLeftCont(), parser.getCourses());
                repaint();
            }
        });

        setVisible(true);
    }

    public void saveSchedule() throws Exception {
        ScheduleDrawer highRes = scheduleDrawer.getHighResVersion();
        Dimension d = highRes.getSize();
        BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        highRes.paintComponent(g2d);
        g2d.dispose();
        ImageIO.write(image, "png", new File("file.png"));
    }


}

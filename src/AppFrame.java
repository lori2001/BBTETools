import Common.AppVersionHandler;
import Common.settings.Settings;
import HomeworkGatherer.HWGMainPanel;
import Common.ScrollableSoloPane;
import Common.logging.LogPanel;
import ScheduleGenerator.SGMainPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AppFrame extends JFrame {
    public static final Point APP_SIZE = new Point(850, 700);
    public static final Point APP_INIT_POS = new Point(100, 100);
    final int headerSize = 38;

    public static void main (String[] args) {
        new AppFrame();
    }

    public AppFrame() {
        super("BBTETools " + AppVersionHandler.VERSION);

        Settings.readFromFile();

        setResizable(false);
        setLayout(null);
        setBounds(APP_INIT_POS.x, APP_INIT_POS.y, APP_SIZE.x, APP_SIZE.y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Point panelSize = new Point(APP_SIZE.x, APP_SIZE.y - headerSize);
        HWGMainPanel homeworkGathererPanel = new HWGMainPanel(this, panelSize);
        SGMainPanel scheduleGeneratorPanel = new SGMainPanel(this, panelSize, Settings.getStudData().group);

        ScrollableSoloPane scrollableSoloPane = new ScrollableSoloPane();
        scrollableSoloPane.setBounds(0, 0, APP_SIZE.x, panelSize.y); // TODO: Change
        scrollableSoloPane.addTab(scheduleGeneratorPanel, "Órarend generáló (BETA)", "Kigenerál egy személyes órarendet html-ben vagy nyomtatható formában");
        scrollableSoloPane.addTab(homeworkGathererPanel, "Házi begyüjtõ", "Begyüjti, majd megfelelõen elnevezi, kommenteli és ellenõrzi a házikat");
        add(scrollableSoloPane);

        // load app icon
        try {
            Image appIcon = ImageIO.read(new File("./assets/icon.png"));
            setIconImage(appIcon);
        }
        catch (Exception e){
            LogPanel.loglnAll("MEGJEGYZÉS: az icon.png file nem található!");
        }

        // save Common.settings when closing window
        addWindowListener(new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  super.windowClosing(e);
                  homeworkGathererPanel.saveSettingsToFile();
              }
          }
        );

        setVisible(true);

        new AppVersionHandler(this);
    }
}
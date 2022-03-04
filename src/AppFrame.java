import Common.AppVersionHandler;
import Common.settings.Settings;
import HomeworkGatherer.HWGMainPanel;
import HomeworkGatherer.InfoPanel;
import Common.logging.LogPanel;
import ScheduleGenerator.SGMainPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;
import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;

public class AppFrame extends JFrame {
    public static final Point APP_SIZE = new Point(700, 555);
    public static final Point APP_INIT_POS = new Point(100, 100);

    private final JTabbedPane tabbedPane = new JTabbedPane();

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

        HWGMainPanel homeworkGathererPanel = new HWGMainPanel(this, APP_SIZE);
        SGMainPanel scheduleGeneratorPanel = new SGMainPanel(this, APP_SIZE, Settings.getStudData().group);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setBounds(0, 0, APP_SIZE.x, APP_SIZE.y); // TODO: Change
        infoPanel.addTab(scheduleGeneratorPanel, "Órarend generáló", "Kigenerál egy személyes órarendet html-ben vagy nyomtatható formában");
        infoPanel.addTab(homeworkGathererPanel, "Házi begyüjtõ", "Begyüjti, majd megfelelõen elnevezi, kommenteli és ellenõrzi a házikat");
        add(infoPanel);

        // load app icon
        try {
            Image appIcon = ImageIO.read(new File("./assets/icon.png"));
            setIconImage(appIcon);
        }
        catch (Exception e){
            LogPanel.logln("MEGJEGYZÉS: az icon.png file nem található!", HWG_LOG_INSTANCE);
            LogPanel.logln("MEGJEGYZÉS: az icon.png file nem található!", SG_LOG_INSTANCE);
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
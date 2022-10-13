import Common.VersionHandler;
import Common.settings.HWGSettings;
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
    final int headerSize = 38;

    public static void main (String[] args) {
        new AppFrame();
    }

    public AppFrame() {
        super("BBTETools " + VersionHandler.VERSION);

        HWGSettings.readFromFile();

        setResizable(false);
        setBounds(-1000, 100, APP_SIZE.x, APP_SIZE.y);

        setLocationRelativeTo(null); // Sets app position on center of the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // load app icon
        try {
            Image appIcon = ImageIO.read(new File("./assets/icon.png"));
            setIconImage(appIcon);
        }
        catch (Exception e){
            LogPanel.loglnAll("MEGJEGYZÉS: az icon.png file nem található!");
        }

        VersionHandler versionHandler = new VersionHandler(this); // Checks version and displays prompt to update if new app version is available

        Point panelSize = new Point(APP_SIZE.x, APP_SIZE.y - headerSize);
        HWGMainPanel homeworkGathererPanel = new HWGMainPanel(this, panelSize);
        SGMainPanel scheduleGeneratorPanel = new SGMainPanel(this, panelSize);

        ScrollableSoloPane scrollableSoloPane = new ScrollableSoloPane();
        scrollableSoloPane.addTab(scheduleGeneratorPanel, "Órarend generáló (BETA)", "Kigenerál egy személyes órarendet html-ben vagy nyomtatható formában");
        scrollableSoloPane.addTab(homeworkGathererPanel, "Házi begyüjtõ", "Begyüjti, majd megfelelõen elnevezi, kommenteli és ellenõrzi a házikat");
        add(scrollableSoloPane);

        // save Common.settings when closing window
        addWindowListener(new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  super.windowClosing(e);
                  homeworkGathererPanel.saveHWGSettingsToFile();
              }
          }
        );

        setVisible(true);
        versionHandler.showUpdatePromptIfActive();
    }
}
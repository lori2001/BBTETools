import Common.AppVersionHandler;
import HomeworkGatherer.HWGMainPanel;
import HomeworkGatherer.InfoPanel;
import HomeworkGatherer.logging.LogPanel;
import Common.models.Vec;
import ScheduleGenerator.SGMainPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AppFrame extends JFrame {
    public static final Vec APP_SIZE = new Vec(700, 555);
    public static final Vec APP_INIT_POS = new Vec(100, 100);

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public static void main (String[] args) {
        new AppFrame();
    }

    public AppFrame() {
        super("BBTETools " + AppVersionHandler.VERSION);
        setResizable(false);
        setLayout(null);
        setBounds(APP_INIT_POS.x, APP_INIT_POS.y, APP_SIZE.x, APP_SIZE.y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        HWGMainPanel homeworkGathererPanel = new HWGMainPanel(this, APP_SIZE);
        SGMainPanel scheduleGeneratorPanel = new SGMainPanel(this, APP_SIZE);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setBounds(0, 0, APP_SIZE.x, APP_SIZE.y); // TODO: Change
        infoPanel.addTab(scheduleGeneratorPanel, "Órarend generáló", "Kigenerál egy személyes órarendet html-ben vagy nyomtatható formában");
        infoPanel.addTab(homeworkGathererPanel, "Hazibegyüjtõ", "Begyüjti, majd megfelelõen elnevezi, kommenteli és ellenõrzi a házikat");
        add(infoPanel);

        // load app icon
        try {
            Image appIcon = ImageIO.read(new File("./assets/icon.png"));
            setIconImage(appIcon);
        }
        catch (Exception e){
            LogPanel.logln("MEGJEGYZÉS: az icon.png file nem található!");
        }

        // save HomeworkGatherer.settings when closing window
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
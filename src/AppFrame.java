import logging.LogPanel;
import models.Vec;
import settings.Setting;
import settings.Settings;
import utils.DocumentChanged;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppFrame extends JFrame {
    private static final Vec APP_SIZE = new Vec(700, 540);
    private static final Vec APP_INIT_POS = new Vec(100, 100);
    public static final String VERSION = "v.2.3.0";

    public static void main (String[] args) {
        new AppFrame();
    }

    public AppFrame() {
        super("Házi Begyüjtő " + VERSION);
        setResizable(false);
        setLayout(null);
        setBounds(APP_INIT_POS.x, APP_INIT_POS.y, APP_SIZE.x,APP_SIZE.y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // displays logs in GUI
        LogPanel.init();

        // load app icon
        try {
            Image appIcon = ImageIO.read(new File("./assets/icon.png"));
            setIconImage(appIcon);
        }
        catch (Exception e){
            LogPanel.logln("MEGJEGYZÉS: az icon.png file nem található!");
        }

        Settings.readFromFile();

        FileInput inputFile = new FileInput("Bemenet:",
                new Vec(15, 25 ),
                new Vec(AppFrame.APP_SIZE.x - 50, 40),
                Settings.getFileContent(Setting.InputFolder)
        );
        add(inputFile);

        FileInput outputFile = new FileInput("Kimenet:",
                new Vec(15, 85 ),
                new Vec(AppFrame.APP_SIZE.x - 50, 40),
                Settings.getFileContent(Setting.OutputFolder)
        );
        add(outputFile);

        StudInfo studInfo = new StudInfo(new Vec(15, 155), new Vec(AppFrame.APP_SIZE.x - 50, 50));
        add(studInfo);

        // choose the uni class for which to target homework gathering on
        ClsPresetPicker clsPresetPicker = new ClsPresetPicker(
                        new Vec(15, AppFrame.APP_SIZE.y - 90),
                        new Vec(150, 35),
                        Settings.getFileContent(Setting.ClsPreset)
        );
        add(clsPresetPicker);

        // display description of class preset
        JPanel clsPresetDesc = new JPanel();
        clsPresetDesc.setBackground(new Color(30, 30, 30));
        clsPresetDesc.setForeground(new Color(212, 212, 212));
        clsPresetPicker.getClsPreset(studInfo).addDescriptionToPanel(clsPresetDesc);
        add(clsPresetDesc);

        // displays clsPresetDesc and Logs in a TabbedPane
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setBounds(15, 220, 655, 210);
        infoPanel.addTab(clsPresetDesc, "Info", "Információkat mutat arról hogy a jelenleg kiválasztott tantárgy mit csinál.");
        infoPanel.addTab(LogPanel.getScrollableTextArea(), "Logs", "A generálási folyamatrol ír ki hasznos infókat");
        add(infoPanel);

        // change preset description whenever preset changes
        clsPresetPicker.addActionListener(e -> updateDescPanel(clsPresetDesc, clsPresetPicker, studInfo));
        studInfo.addDocumentListener(new DocumentChanged() {
            @Override
            public void onContentChange(DocumentEvent e) {
                clsPresetPicker.getClsPreset(studInfo).setStudData(studInfo.getStudData());
                updateDescPanel(clsPresetDesc, clsPresetPicker, studInfo);
            }
        });

        LoadingPrompt loadingPrompt = new LoadingPrompt(
                new Vec( AppFrame.APP_SIZE.x - 80, AppFrame.APP_SIZE.y - 100),
                new Vec (45, 45)
        );
        add(loadingPrompt);

        AppInfoButton appInfoButton =
                new AppInfoButton(new Vec(395, AppFrame.APP_SIZE.y - 90), new Vec(35, 35));
        appInfoButton.toggleInfoFrameOnClick(this);
        add(appInfoButton);

        JButton gatherHw = new JButton("Házi Begyüjtése");
        gatherHw.setBounds( 180, AppFrame.APP_SIZE.y - 90, 200, 35);
        add(gatherHw);
        DirectoryProcessor directoryProcesser = new DirectoryProcessor();
        gatherHw.addActionListener(e -> new Thread(){
            @Override
            public void run() {
                super.run();
                // switch tab to logs
                LogPanel.clearLogs(); // clear old logs
                infoPanel.setSelectedIndex(1);
                // enable loading icon
                loadingPrompt.isLoading(true);
                // auto-save settings
                Settings.saveToFile(studInfo.getStudData(),
                        inputFile.getPath().toString(),
                        outputFile.getPath().toString(),
                        clsPresetPicker.getActiveClsString()
                );
                // process files
                boolean success = directoryProcesser.processDir(inputFile.getPath(), outputFile.getPath(), clsPresetPicker.getClsPreset(studInfo));
                // disable loading icon
                loadingPrompt.isLoading(false);
                if(success) {
                    dirProcWasGood();
                } else {
                    dirProcWasBad();
                }
            }
        }.start());

        // save settings when closing window
        addWindowListener(new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  super.windowClosing(e);
                  Settings.saveToFile(studInfo.getStudData(),
                          inputFile.getPath().toString(),
                          outputFile.getPath().toString(),
                          clsPresetPicker.getActiveClsString()
                  );
              }
          }
        );

        setVisible(true);

        // check for updates
        try {
            URL u = new URL("https://github.com/lori2001/BBTETools/releases/latest");
            HttpURLConnection hr = (HttpURLConnection) u.openConnection();
            if(hr.getResponseCode() == 200) {
                String githubUrl = hr.getURL().toString(); // the url changes(and shows latest version) as a result of opening github page
                String lastVersion = githubUrl.substring(githubUrl.lastIndexOf("/") + 1);

                if(!githubUrl.contains(VERSION)) {
                    // display update prompt
                    UpdatePrompt updatePrompt = new UpdatePrompt(this, lastVersion);
                }
            }
            else throw new Exception();
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen verzió ellenõrzés! " + e);
        }
    }

    private void updateDescPanel(JPanel clsPresetDesc, ClsPresetPicker clsPresetPicker, StudInfo studInfo) {
        clsPresetDesc.removeAll();
        clsPresetPicker.getClsPreset(studInfo).addDescriptionToPanel(clsPresetDesc);
        clsPresetDesc.repaint();
        clsPresetDesc.revalidate();
    }

    private void dirProcWasGood() {
        // play "good" sound
        try {
            File audioFile = new File("assets/complete.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            LogPanel.logln("MEGJEGYZÉS: A \"kész\" hang lejátszása sikertelen!");
        }

        // log "good" message
        MutableAttributeSet attributes = new SimpleAttributeSet(LogPanel.getDefaultAttributes());
        StyleConstants.setBackground(attributes, new Color(15, 117, 21));
        StyleConstants.setFontSize(attributes, 14);
        StyleConstants.setBold(attributes, true);
        LogPanel.logln("SIKERES GENERÁLÁS!", attributes);
    }

    private void dirProcWasBad() {
        // log "bad" message
        MutableAttributeSet attributes = new SimpleAttributeSet(LogPanel.getDefaultAttributes());
        StyleConstants.setBackground(attributes, new Color(185, 8, 8));
        StyleConstants.setFontSize(attributes, 14);
        StyleConstants.setBold(attributes, true);
        LogPanel.logln("SIKERTELEN GENERÁLÁS!", attributes);
    }
}

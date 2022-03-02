package HomeworkGatherer;

import Common.logging.LogPanel;
import Common.logging.LogsListener;
import Common.settings.Setting;
import Common.settings.Settings;
import HomeworkGatherer.utils.DocumentChanged;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class HWGMainPanel extends JPanel {
    StudInfo studInfo;
    FileInput inputFile;
    FileInput outputFile;
    ClsPresetPicker clsPresetPicker;

    public static final int HWG_LOG_INSTANCE = LogPanel.createNewInstance();

    public HWGMainPanel(JFrame appFrame, Point appSize) {
        setLayout(null);
        setBounds(0,0, appSize.x, appSize.y);

        inputFile = new FileInput("Bemenet:",
                new Point(12, 15 ),
                new Point(appSize.x - 50, 40),
                Settings.getFileContent(Setting.InputFolder)
        );
        add(inputFile);

        outputFile = new FileInput("Kimenet:",
                new Point(12, 75 ),
                new Point(appSize.x - 50, 40),
                Settings.getFileContent(Setting.OutputFolder)
        );
        add(outputFile);

        studInfo = new StudInfo(new Point(15, 145), new Point(appSize.x - 50, 50));
        add(studInfo);

        // choose the uni class for which to target homework gathering on
        clsPresetPicker = new ClsPresetPicker(
                        new Point(12, 440),
                        new Point(150, 35),
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
        infoPanel.setBounds(12, 210, 655, 210);
        infoPanel.addTab(clsPresetDesc, "Info", "Információkat mutat arról hogy a jelenleg kiválasztott tantárgy mit csinál.");
        infoPanel.addTab(LogPanel.getScrollableTextArea(HWG_LOG_INSTANCE), "Logs", "A generálási folyamatrol ír ki hasznos infókat");
        add(infoPanel);
        LogPanel.addListener(new LogsListener() {
            @Override
            public void logsCleared() {
                infoPanel.setSelectedIndex(1);
            }

            @Override
            public void logsWritten() {}
        }, HWG_LOG_INSTANCE);

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
                new Point( appSize.x - 80, 440),
                new Point(45, 45)
        );
        add(loadingPrompt);

        AppInfoButton appInfoButton =
                new AppInfoButton(new Point(395, 440), new Point(35, 35));
        appInfoButton.toggleInfoFrameOnClick(appFrame);
        add(appInfoButton);

        JButton gatherHw = new JButton("Házi Begyüjtése");
        gatherHw.setBounds( 180, 440, 200, 35);
        add(gatherHw);
        DirectoryProcessor directoryProcesser = new DirectoryProcessor();
        gatherHw.addActionListener(e -> new Thread(){
            @Override
            public void run() {
                super.run();

                studInfo.printErrMsges();

                LogPanel.clearLogs(HWG_LOG_INSTANCE); // clear old logs
                loadingPrompt.isLoading(true); // enable loading icon

                saveSettingsToFile(); // auto-save Common.settings
                boolean success = directoryProcesser.processDir(inputFile.getPath(), outputFile.getPath(), clsPresetPicker.getClsPreset(studInfo)); // process files

                loadingPrompt.isLoading(false); // disable loading icon

                if(success) {
                    dirProcWasGood();
                } else {
                    dirProcWasBad();
                }
            }
        }.start());

        setVisible(true);
    }

    public void saveSettingsToFile() {
        Settings.saveToFile(studInfo.getStudData(),
                inputFile.getPath().toString(),
                outputFile.getPath().toString(),
                clsPresetPicker.getActiveClsString()
        );
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
            LogPanel.logln("MEGJEGYZÉS: A \"kész\" hang lejátszása sikertelen!", HWG_LOG_INSTANCE);
        }

        // log "good" message
        MutableAttributeSet attributes = new SimpleAttributeSet(LogPanel.getDefaultAttributes());
        StyleConstants.setBackground(attributes, new Color(15, 117, 21));
        StyleConstants.setFontSize(attributes, 14);
        StyleConstants.setBold(attributes, true);
        LogPanel.logln("SIKERES GENERÁLÁS!", attributes, HWG_LOG_INSTANCE);
    }

    private void dirProcWasBad() {
        // log "bad" message
        MutableAttributeSet attributes = new SimpleAttributeSet(LogPanel.getDefaultAttributes());
        StyleConstants.setBackground(attributes, new Color(185, 8, 8));
        StyleConstants.setFontSize(attributes, 14);
        StyleConstants.setBold(attributes, true);
        LogPanel.logln("SIKERTELEN GENERÁLÁS!", attributes, HWG_LOG_INSTANCE);
    }
}

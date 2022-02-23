package HomeworkGatherer;

import HomeworkGatherer.logging.LogPanel;
import Common.models.Vec;
import HomeworkGatherer.settings.Setting;
import HomeworkGatherer.settings.Settings;
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

    public HWGMainPanel(JFrame appFrame, Vec appSize) {
        setLayout(null);
        setBounds(0,0, appSize.x, appSize.y);

        // displays logs in GUI
        LogPanel.init();

        Settings.readFromFile();

        inputFile = new FileInput("Bemenet:",
                new Vec(12, 15 ),
                new Vec(appSize.x - 50, 40),
                Settings.getFileContent(Setting.InputFolder)
        );
        add(inputFile);

        outputFile = new FileInput("Kimenet:",
                new Vec(12, 75 ),
                new Vec(appSize.x - 50, 40),
                Settings.getFileContent(Setting.OutputFolder)
        );
        add(outputFile);

        studInfo = new StudInfo(new Vec(15, 145), new Vec(appSize.x - 50, 50));
        add(studInfo);

        // choose the uni class for which to target homework gathering on
        clsPresetPicker = new ClsPresetPicker(
                        new Vec(12, 440),
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
        infoPanel.setBounds(12, 210, 655, 210);
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
                new Vec( appSize.x - 80, 440),
                new Vec (45, 45)
        );
        add(loadingPrompt);

        AppInfoButton appInfoButton =
                new AppInfoButton(new Vec(395, 440), new Vec(35, 35));
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
                // switch tab to logs
                LogPanel.clearLogs(); // clear old logs
                infoPanel.setSelectedIndex(1);
                // enable loading icon
                loadingPrompt.isLoading(true);
                saveSettingsToFile(); // auto-save HomeworkGatherer.settings

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

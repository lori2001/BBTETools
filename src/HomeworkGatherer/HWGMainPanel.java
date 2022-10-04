package HomeworkGatherer;

import Common.InfoButton;
import Common.ScrollableSoloPane;
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
    private final StudInfo studInfo;
    private final FileInput inputFile;
    private final FileInput outputFile;
    private final ClsPresetPicker clsPresetPicker;

    public static final int HWG_LOG_INSTANCE = LogPanel.createNewInstance("Nincs üzenet. Begyüjtéshez kattincs a \"Házi Begyüjtése\" gombra!");

    public HWGMainPanel(JFrame appFrame, Point panelSize) {
        setLayout(null);
        setBounds(0,0, panelSize.x, panelSize.y);

        inputFile = new FileInput("Bemenet:",
                new Point(12, 15 ),
                new Point(panelSize.x - 50, 40),
                Settings.getFileContent(Setting.InputFolder)
        );
        add(inputFile);

        outputFile = new FileInput("Kimenet:",
                new Point(12, 75 ),
                new Point(panelSize.x - 50, 40),
                Settings.getFileContent(Setting.OutputFolder)
        );
        add(outputFile);

        studInfo = new StudInfo(new Point(15, 145), new Point(panelSize.x - 50, 50));
        add(studInfo);

        // choose the uni class for which to target homework gathering on
        clsPresetPicker = new ClsPresetPicker(
                        new Point(12, getHeight() - 70),
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
        ScrollableSoloPane scrollableSoloPane = new ScrollableSoloPane();
        scrollableSoloPane.setBounds(12, 210, getWidth() - 45, getHeight() - 300);
        scrollableSoloPane.addTab(clsPresetDesc, "Infók", "Információkat mutat, arról hogy a jelenleg kiválasztott tantárgy mit csinál.");
        scrollableSoloPane.addTab(LogPanel.getScrollableTextArea(HWG_LOG_INSTANCE), "Üzenetek", "A generálási folyamatról ír ki hasznos infókat");
        add(scrollableSoloPane);
        LogPanel.addListener(new LogsListener() {
            @Override
            public void logsCleared() {
                scrollableSoloPane.setSelectedIndex(1);
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
                new Point( panelSize.x - 80, getHeight() - 70),
                new Point(45, 45)
        );
        add(loadingPrompt);

        String HWGInfo = "<html><center><h1>Házi Begyüjtõ Infók</h1></center>" +
                "<p>Ez az app a házi begyüjtésének unalmas folyamatát<br>" +
                "automatizálja. Kézzel elnevezni és bekomentelni<br>" +
                "minden házit időigényes és könnyen elrontható,<br>" +
                "s elnevezési hiba esetén néhány tantárgyból a <br>" +
                "diák a teljes pontszámát elveszítheti.</p>" +
                "<center><h2>Hogyan használhatod?</h2></center>" +
                "<p>Készítsd el az (algoritmika) házid összes alpontját egy<br>" +
                "folderbe (vagy annak bármely alfolderébe) és nevezd<br>" +
                "el \"alpont.cpp\"-nek. Például 1.cpp,2.cpp stb.<br>" +
                "Töltsd ki az appet az adataiddal illetve a kékkel<br>" +
                "megjelölt mezőre írd be hogy hányas labort akarsz<br>" +
                "generálni. Végûl pedig az appben válaszd ki <br>" +
                "bemenetnek a házis foldert, illetve kimenetnek<br>" +
                "bármely mappát, és kattints a \"Begyüjtés\" gombra</p>" +
                "<center><h2>Hogyan működik?</h2></center>" +
                "<p>A program bejárja a bemenetként adott foldert és<br>" +
                "annak minden alfolderét. Megkeresi a megfelelő<br>" +
                "file típust (például algoritmikából a \".cpp\") és<br>" +
                "az appbe beírt infóknak megfelelően kimásolja az<br>" +
                "összes filet, majd megfelelően átnevezi,<br>" +
                "bekommenteli és (egyes tantárgyakból)<br>" +
                "plusz ellenõrzéseket is végrehajt.</p>" +
                "</html>";
        InfoButton infoButton =
                new InfoButton(new Point(395, getHeight() - 70), new Point(35, 35), appFrame, HWGInfo);
        add(infoButton);

        JButton gatherHw = new JButton("Házi Begyüjtése");
        gatherHw.setBounds( 180, getHeight() - 70, 200, 35);
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

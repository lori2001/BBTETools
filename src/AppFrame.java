import clsPresets.Classes;
import models.Vec;
import settings.Setting;
import settings.Settings;
import utils.DocumentChanged;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class AppFrame extends JFrame {
    public static final Vec APP_SIZE = new Vec(700, 540);
    public static final Vec APP_INIT_POS = new Vec(600, 50);
    public static final String version = "v2.1";
    private static Image appIcon;

    public static Image getAppIcon() {
        return appIcon;
    }

    public static void main (String[] args) {
        new AppFrame();
    }

    public AppFrame() {
        super("Házi Begyüjtő " + version);
        setResizable(false);
        setLayout(null);
        setBounds(APP_INIT_POS.x, APP_INIT_POS.y, APP_SIZE.x,APP_SIZE.y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // load app icon
        try {
            appIcon = ImageIO.read(new File("./assets/icon.png"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        setIconImage(getAppIcon());

        Settings.readFromFile();

        FileInput inputFile = new FileInput("Bemenet:",
                new Vec(25, 25 ),
                new Vec(AppFrame.APP_SIZE.x - 50, 40),
                Settings.getFileContent(Setting.InputFolder)
        );
        add(inputFile);

        FileInput outputFile = new FileInput("Kimenet:",
                new Vec(25, 100 ),
                new Vec(AppFrame.APP_SIZE.x - 50, 40),
                Settings.getFileContent(Setting.OutputFolder)
        );
        add(outputFile);

        StudInfo studInfo = new StudInfo(new Vec(25, 200), new Vec(AppFrame.APP_SIZE.x - 50, 50));
        add(studInfo);

        // choose the uni class for which to target homework gathering on
        ClsPresetPicker clsPresetPicker = new ClsPresetPicker(
                        new Vec(25, AppFrame.APP_SIZE.y - 270),
                        new Vec(200, 30),
                        Settings.getFileContent(Setting.ClsPreset)
        );
        add(clsPresetPicker);

        // display description of class preset
        JPanel clsPresetDesc = new JPanel();
        clsPresetDesc.setBounds(240, AppFrame.APP_SIZE.y - 270, 420, 150);
        clsPresetPicker.getClsPreset(studInfo).addDescriptionToPanel(clsPresetDesc);
        clsPresetDesc.setBackground(new Color(248, 199, 106));
        add(clsPresetDesc);

        // change preset description whenever preset changes
        clsPresetPicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDescPanel(clsPresetDesc, clsPresetPicker, studInfo);
            }
        });
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
                new AppInfoButton(new Vec(240, AppFrame.APP_SIZE.y - 97), new Vec(40, 40));
        appInfoButton.toggleInfoFrameOnClick();
        add(appInfoButton);

        JButton gatherHw = new JButton("Házi Begyüjtése");
        gatherHw.setBounds( 25, AppFrame.APP_SIZE.y - 100, 200, 50);
        add(gatherHw);
        gatherHw.addActionListener(e -> {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    // enable loading icon
                    loadingPrompt.isLoading(true);
                    // process files
                    new ProcessDir(inputFile.getPath(), outputFile.getPath(), clsPresetPicker.getClsPreset(studInfo));
                    // play "done" sound
                    try {
                        File audioFile = new File("assets/complete.wav");
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                        AudioFormat format = audioStream.getFormat();
                        DataLine.Info info = new DataLine.Info(Clip.class, format);
                        Clip audioClip = (Clip) AudioSystem.getLine(info);
                        audioClip.open(audioStream);
                        audioClip.start();
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        ex.printStackTrace();
                    }
                    // disable loading icon
                    loadingPrompt.isLoading(false);
                }
            }.start();
        });

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
    }

    private void updateDescPanel(JPanel clsPresetDesc, ClsPresetPicker clsPresetPicker, StudInfo studInfo) {
        clsPresetDesc.removeAll();
        clsPresetPicker.getClsPreset(studInfo).addDescriptionToPanel(clsPresetDesc);
        clsPresetDesc.repaint();
        clsPresetDesc.revalidate();
    }

}

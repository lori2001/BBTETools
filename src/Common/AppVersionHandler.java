package Common;

import HomeworkGatherer.logging.LogPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AppVersionHandler {

    public static final String VERSION = "v.3.0.0";
    JFrame updatePrompt;

    public AppVersionHandler(JFrame parentFrame) {
        try {
            URL u = new URL("https://github.com/lori2001/BBTETools/releases/latest");
            HttpURLConnection hr = (HttpURLConnection) u.openConnection();
            if (hr.getResponseCode() == 200) {
                String githubUrl = hr.getURL().toString(); // the url changes(and shows latest version) as a result of opening github page
                String lastVersion = githubUrl.substring(githubUrl.lastIndexOf("/") + 1);

                if (!githubUrl.contains(VERSION)) {
                    // display update prompt
                    displayUpdatePrompt(parentFrame, VERSION, lastVersion);
                }
            } else throw new Exception();
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen verzió ellenõrzés! " + e);
        }
    }

    public void displayUpdatePrompt(JFrame parentFrame, String currentVer, String newestVer) {
        updatePrompt = new JFrame("Frissités!");

        int width = 350;
        int height = 180;
        updatePrompt.setBounds(
                parentFrame.getX() + parentFrame.getWidth() / 2 - width / 2,
                parentFrame.getY() + parentFrame.getHeight() / 2 - height / 2,
                width, height);
        updatePrompt.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        updatePrompt.setIconImage(parentFrame.getIconImage());

        JLabel text = new JLabel(
                "<html><center><h1>Frissítés szükséges!</h1></center>" +
                        "<p>A letöltött app verziója: <b>" + currentVer + "</b><br>" +
                        "A legújabb verzió: <b>" + newestVer + "</b></p>" +
                        "</html>");
        updatePrompt.add(text);

        String linkText = "Innen letöltheted a legújabb verziót!";
        String unfocusedText = "<html><center><h3>" + linkText + "</h3></center></html>";
        JLabel link = new JLabel(unfocusedText);

        link.setForeground(Color.BLUE.darker());
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/lori2001/BBTETools/releases/latest/"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                link.setText(unfocusedText);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                link.setText("<html><center><h3><a href=''>" + linkText + "</a></h3></center></html>"); // adds underline
            }

        });
        updatePrompt.add(link);

        updatePrompt.setVisible(true);
    }
}

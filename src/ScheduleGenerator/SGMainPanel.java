package ScheduleGenerator;

import HomeworkGatherer.logging.LogPanel;
import HomeworkGatherer.models.Vec;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SGMainPanel extends JPanel {

    public SGMainPanel(JFrame appFrame, Vec appSize) {
        setLayout(null);
        setBounds(0,0, appSize.x, appSize.y);

        try {
            URL u = new URL("https://www.cs.ubbcluj.ro/files/orar/2021-2/grafic/MIM2.html");
            HttpURLConnection hr = (HttpURLConnection) u.openConnection();
            if (hr.getResponseCode() == 200) {
                String pageContent = new BufferedReader(
                        new InputStreamReader(hr.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                System.out.println(pageContent);

            } else throw new Exception();
        } catch (Exception e) {
            LogPanel.logln("VIGYÁZAT: Sikertelen verzió ellenõrzés! " + e);
        }

        setVisible(true);
    }

    // ex.: tag = table means get content in between
    // <table class="whatever">[this]</table>
    /*public static String getContentOfHtmlTag(String from, String tag) {
        tag = "<table>";

        int strPos = content.indexOf(str); // check if string exists
        boolean existsAndIsInComm = false;

        if(strPos != -1) { // if string is found
            if(content.substring(0, strPos).contains(sComm) && content.substring(strPos).contains(eComm))
            {
                existsAndIsInComm = true;
            } else {
                // search string's line
                int newLineInd = 0;
                for(int i = strPos; i >= 0 && content.charAt(i) != '\n'; i--) {
                    newLineInd = i;
                }

                if(content.substring(newLineInd, strPos).contains(olComm)) {
                    existsAndIsInComm = true;
                }
            }
        }

        return existsAndIsInComm;
    }*/
}

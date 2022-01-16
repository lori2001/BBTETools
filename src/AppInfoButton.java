import models.Vec;

import javax.swing.*;
import java.awt.*;

public class AppInfoButton extends JPanel{
    private JButton button;
    private JFrame infoScreen = null;

    public AppInfoButton(Vec pos, Vec size) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        String iconLoc = "./assets/info.png";
        ImageIcon icon = new ImageIcon(iconLoc);

        if(icon.getIconWidth() != -1) {
            icon.setImage(icon.getImage().getScaledInstance((int)(size.x*0.85), (int)(size.x*0.85), Image.SCALE_DEFAULT));
            button = new JButton(icon);
        } else {
            button = new JButton("info");
            System.out.println("\""+iconLoc+"\" not found! Replacing with text");
        }

        button.setPreferredSize(size.toDim());
        add(button);
    }

    public void toggleInfoFrameOnClick() {
        button.addActionListener(e -> {
                if(infoScreen == null) {
                    infoScreen = new JFrame("info");

                    int yOffset = 10;
                    infoScreen.setBounds(
                            AppFrame.APP_INIT_POS.x + AppFrame.APP_SIZE.x / 4,
                            AppFrame.APP_INIT_POS.y + yOffset,
                            AppFrame.APP_SIZE.x / 2,
                            AppFrame.APP_SIZE.y - yOffset * 2);
                    infoScreen.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    infoScreen.setIconImage(AppFrame.getAppIcon());

                    JLabel text = new JLabel(
                            "<html><center><h1>App Információ</h1></center>" +
                            "<p>Ez az app a házi begyüjtésének unalmas folyamatát<br>" +
                               "automatizálja. Kézzel elnevezni és bekomentelni<br>" +
                               "minden házit idõigényes és könnyen elrontható,<br>" +
                               "s elnevezési hiba esetén néhány tantárgyból a <br>" +
                               "diák a teljes pontszámát elveszítheti.</p>" +
                            "<center><h2>Hogyan használható?</h2></center>" +
                                "<p>Készítsd el az összes algoritmika házid egy<br>" +
                                "folderbe vagy annak bármely alfolderébe és nevezd<br>" +
                                "el \"<alpont>.cpp\"-nek. Például 1.cpp,2.cpp stb.<br>" +
                                "Töltsd ki az appet az adataiddal illetve a pirossal<br>" +
                                "megjelölt mezõre írd be hogy hányas labort akarsz<br>" +
                                "generálni. Végül pedig állítsd be bemenetnek a<br>" +
                                "házis foldert, illetve kimenetnek bármely elérhetõ<br>" +
                                "mappát, és kattints a \"Begyüjtés\" gombra</p>" +
                            "<center><h2>Hogyan mûködik?</h2></center>" +
                            "<p>A program bejárja a bemenetként adott foldert és<br>" +
                               "annak minden alfolderét. Megkeresi a megfelelõ<br>" +
                                "filetípust(például algoritmikából a \".cpp\") és<br>" +
                                "az appbe beírt infóknak megfelelõen kimásolja az<br>" +
                                "összes filet, majd megfelelõen átnevezi,<br>" +
                                "bekommenteli és (egyes tantárgyakból)<br>" +
                                "a tartalmát is ellenõrzi.</p>" +
                            "</html>");

                    infoScreen.add(text);
                    infoScreen.setVisible(true);
                } else {
                    infoScreen.dispose();
                    infoScreen = null;
                }
            }
        );
    }
}

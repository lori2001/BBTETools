package HomeworkGatherer;

import Common.logging.LogPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;

public class AppInfoButton extends JPanel{
    private final JButton button;
    private JFrame infoScreen = null;

    public AppInfoButton(Point pos, Point size) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        String iconLoc = "./assets/info.png";
        ImageIcon icon = new ImageIcon(iconLoc);

        if(icon.getIconWidth() != -1) {
            icon.setImage(icon.getImage().getScaledInstance((int)(size.x*0.85), (int)(size.x*0.85), Image.SCALE_DEFAULT));
            button = new JButton(icon);
        } else {
            button = new JButton("info");
            LogPanel.logln("MEGJEGYZÉS: \"" + iconLoc + "\" nem talált! Írással lesz helyettesítve", HWG_LOG_INSTANCE);
        }

        button.setPreferredSize(new Dimension(size.x, size.y));
        add(button);
    }

    public void toggleInfoFrameOnClick(JFrame parentFrame) {
        button.addActionListener(e -> {
                if(infoScreen == null) {
                    infoScreen = new JFrame("info");

                    int yOffset = 10;
                    infoScreen.setBounds(
                            parentFrame.getX() + parentFrame.getWidth()  / 4,
                            parentFrame.getY() + yOffset,
                            parentFrame.getWidth() / 2,
                            parentFrame.getHeight() - yOffset * 2);
                    infoScreen.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    infoScreen.setIconImage(parentFrame.getIconImage());

                    JLabel text = new JLabel(
                            "<html><center><h1>App Információ</h1></center>" +
                            "<p>Ez az app a házi begyüjtésének unalmas folyamatát<br>" +
                               "automatizálja. Kézzel elnevezni és bekomentelni<br>" +
                               "minden házit időigényes és könnyen elrontható,<br>" +
                               "s elnevezési hiba esetén néhány tantárgyból a <br>" +
                               "diák a teljes pontszámát elveszítheti.</p>" +
                            "<center><h2>Hogyan használható?</h2></center>" +
                                "<p>Készítsd el az összes algoritmika házid egy<br>" +
                                "folderbe vagy annak bármely alfolderébe és nevezd<br>" +
                                "el \"alpont.cpp\"-nek. Például 1.cpp,2.cpp stb.<br>" +
                                "Töltsd ki az appet az adataiddal illetve a pirossal<br>" +
                                "megjelölt mezőre írd be hogy hányas labort akarsz<br>" +
                                "generálni. Végül pedig állítsd be bemenetnek a<br>" +
                                "házis foldert, illetve kimenetnek bármely elérhető<br>" +
                                "mappát, és kattints a \"Begyüjtés\" gombra</p>" +
                            "<center><h2>Hogyan működik?</h2></center>" +
                            "<p>A program bejárja a bemenetként adott foldert és<br>" +
                               "annak minden alfolderét. Megkeresi a megfelelő<br>" +
                                "filetípust(például algoritmikából a \".cpp\") és<br>" +
                                "az appbe beírt infóknak megfelelően kimásolja az<br>" +
                                "összes filet, majd megfelelően átnevezi,<br>" +
                                "bekommenteli és (egyes tantárgyakból)<br>" +
                                "a tartalmát is ellenőrzi.</p>" +
                            "</html>");

                    // save Common.settings when closing window
                    infoScreen.addWindowListener(new WindowAdapter() {
                              @Override
                              public void windowClosing(WindowEvent e) {
                                  super.windowClosing(e);
                                  infoScreen.dispose();
                                  infoScreen = null;
                              }
                          }
                    );

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

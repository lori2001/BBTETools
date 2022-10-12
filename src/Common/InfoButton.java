package Common;

import Common.logging.LogPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;

public class InfoButton extends JPanel{
    private JFrame infoScreen = null;

    public InfoButton(Point pos, Point size, JFrame parentFrame, String htmlCode) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        String iconLoc = "./assets/info.png";
        ImageIcon icon = new ImageIcon(iconLoc);

        JButton button;
        if(icon.getIconWidth() != -1) {
            icon.setImage(icon.getImage().getScaledInstance((int)(size.x*0.85), (int)(size.x*0.85), Image.SCALE_DEFAULT));
            button = new JButton(icon);
        } else {
            button = new JButton("Infó");
            LogPanel.logln("MEGJEGYZÉS: \"" + iconLoc + "\" nem talált! Írással lesz helyettesítve", HWG_LOG_INSTANCE);
        }

        button.setPreferredSize(new Dimension(size.x, size.y));
        add(button);

        button.addActionListener(e -> {
                    if(infoScreen == null) {
                        infoScreen = new JFrame("Infó");

                        int yOffset = 80;
                        infoScreen.setBounds(
                                parentFrame.getX() + parentFrame.getWidth()  / 4,
                                parentFrame.getY() + yOffset,
                                parentFrame.getWidth() / 2,
                                parentFrame.getHeight() - yOffset * 2);
                        infoScreen.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                        infoScreen.setIconImage(parentFrame.getIconImage());

                        JLabel text = new JLabel(htmlCode);

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

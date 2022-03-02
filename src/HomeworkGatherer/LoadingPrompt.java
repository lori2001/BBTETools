package HomeworkGatherer;

import Common.logging.LogPanel;

import javax.swing.*;
import java.awt.*;

import static HomeworkGatherer.HWGMainPanel.HWG_LOG_INSTANCE;

public class LoadingPrompt extends JPanel {
    private final JLabel label;

    public LoadingPrompt(Point pos, Point size) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        String iconLoc = "./assets/loading.gif";
        ImageIcon icon = new ImageIcon(iconLoc);

        if(icon.getIconWidth() != -1) {
            icon.setImage(icon.getImage().getScaledInstance(size.x, size.y, Image.SCALE_DEFAULT));
            label = new JLabel(icon);
        } else {
            label = new JLabel("Loading...");
            LogPanel.logln("\""+iconLoc+"\" nem talált! Írással lesz helyettesítve.", HWG_LOG_INSTANCE);
        }

        isLoading(false);
        label.setPreferredSize(new Dimension(size.x, size.y));
        add(label);
    }

    public void  isLoading(boolean b) {
        label.setVisible(b);
    }

}

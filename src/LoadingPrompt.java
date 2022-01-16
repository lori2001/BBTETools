import models.Vec;

import javax.swing.*;
import java.awt.*;

public class LoadingPrompt extends JPanel {
    private final JLabel label;

    public LoadingPrompt(Vec pos, Vec size) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        String iconLoc = "./assets/loading.gif";
        ImageIcon icon = new ImageIcon(iconLoc);

        if(icon.getIconWidth() != -1) {
            icon.setImage(icon.getImage().getScaledInstance(size.x, size.y, Image.SCALE_DEFAULT));
            label = new JLabel(icon);
        } else {
            label = new JLabel("Loading...");
            System.out.println("\""+iconLoc+"\" not found! Replacing with text");
        }

        isLoading(false);
        label.setPreferredSize(size.toDim());
        add(label);
    }

    public void  isLoading(boolean b) {
        label.setVisible(b);
    }

}

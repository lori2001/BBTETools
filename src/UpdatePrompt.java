import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UpdatePrompt extends JFrame {

    UpdatePrompt(JFrame parentFrame, String newestVer) {
       super("Frissités!");

       int width = 350;
       int height = 180;
        setBounds(
                parentFrame.getX() + parentFrame.getWidth() / 2  -  width / 2,
                parentFrame.getY() + parentFrame.getHeight() / 2  - height / 2,
                width, height);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        setIconImage(parentFrame.getIconImage());

        JLabel text = new JLabel(
                "<html><center><h1>Frissítés szükséges!</h1></center>" +
                        "<p>A letöltött app verziója: <b>"  + AppFrame.VERSION + "</b><br>" +
                        "A legújabb verzió: <b>"  + newestVer + "</b></p>" +
                        "</html>");
        add(text);

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
        add(link);

        setVisible(true);
    }
}

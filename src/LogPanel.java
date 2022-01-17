import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LogPanel {

    private final JTextPane textPane = new JTextPane();
    private final JScrollPane scroll = new JScrollPane(textPane);

    LogPanel() {
        MutableAttributeSet globalAttr = new SimpleAttributeSet(textPane.getInputAttributes());
        // StyleConstants.setFontSize(globalAttr, 12);
        StyleConstants.setLineSpacing(globalAttr, 0.2f);

        textPane.setBackground(new Color(30, 30, 30));
        textPane.setForeground(new Color(212, 212, 212));
        textPane.setParagraphAttributes(globalAttr, false);
        textPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    public JScrollPane getScrollableTextArea() {
        textPane.setEditable(false); // set textArea non-editable

        return scroll;
    }

    //The following codes set where the text get redirected. In this case, jTextArea1
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AttributeSet attributes = ColorSeverity(text);
                try {
                    Document doc = textPane.getDocument();
                    doc.insertString(doc.getLength(), text, attributes);
                } catch (BadLocationException ignored) {
                    // printing anything here would create an endless loop
                }
            }
        });
    }

    public void clearLogs() {
        textPane.setText("");
    }

    private AttributeSet ColorSeverity(String text)
    {
        MutableAttributeSet attributes = new SimpleAttributeSet(textPane.getInputAttributes());

        if(text.startsWith("HIBA")) { // highlight error
           StyleConstants.setForeground(attributes, new Color(196, 33, 33));
        } else if(text.startsWith("VIGYÁZAT")) {
           StyleConstants.setForeground(attributes, new Color(255, 138, 17)); // 204, 144, 119
        } else if(text.startsWith("MEGJEGYZÉS")) {
           StyleConstants.setForeground(attributes, new Color(76, 195, 161));
        } else if (text.startsWith("A GENERÁLÁS KÉSZEN VAN!")) {
            StyleConstants.setBackground(attributes, new Color(15, 117, 21));
            StyleConstants.setFontSize(attributes, 14);
            StyleConstants.setBold(attributes, true);
        }

        return attributes;
    }

}

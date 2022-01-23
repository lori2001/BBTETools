package logging;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LogPanel {

    private static final JTextPane textPane = new JTextPane();
    private static final MutableAttributeSet defaultTextAttributes = new SimpleAttributeSet(textPane.getInputAttributes());
    private static final JScrollPane scroll = new JScrollPane(textPane);

    private static final String NO_LOGS = "Nincs üzenet. Begyüjtéshez kattincs a \"Házi Begyüjtése\" gombra!";
    private static boolean noLogs = true;

    private static final HashMap<String, Color> SEVERITIES = new HashMap<>() {{
        put("HIBA:", new Color(196, 33, 33));
        put("VIGYÁZAT:", new Color(232, 87, 17));
        put("MEGJEGYZÉS:", new Color(76, 195, 161));
    }};

    public static void init() {
        Color fG = new Color(230, 230, 230);
        Color bG = new Color(30, 30, 30);

        StyleConstants.setLineSpacing(defaultTextAttributes, 0.2f);
        StyleConstants.setBackground(defaultTextAttributes, bG);
        StyleConstants.setForeground(defaultTextAttributes, fG);

        textPane.setBackground(bG);
        textPane.setForeground(fG);
        textPane.setCharacterAttributes(defaultTextAttributes, false);
        textPane.setParagraphAttributes(defaultTextAttributes, false);

        textPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        /*OutputStream out = new OutputStream() {
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
        System.setErr(new PrintStream(out, true));*/
        clearLogs(); // text: No logs
    }

    public static JScrollPane getScrollableTextArea() {
        textPane.setEditable(false); // set textArea non-editable

        return scroll;
    }

    public static MutableAttributeSet getDefaultAttributes() {
        return defaultTextAttributes;
    }

    /*public static void log(String text, Color foreground) {
        MutableAttributeSet attributes = new SimpleAttributeSet(textPane.getInputAttributes());
        StyleConstants.setForeground(attributes, foreground);
        log(text, attributes);
    }*/

    public static void log(String text) {

        boolean severityFound = false;
        for (Map.Entry<String, Color> e : SEVERITIES.entrySet()) {
            if (text.startsWith(e.getKey())) {
                severityFound = true;
                MutableAttributeSet attr = new SimpleAttributeSet(textPane.getInputAttributes());

                // style the matched part
                StyleConstants.setBackground(attr, e.getValue());
                StyleConstants.setBold(attr, true);
                log(text.substring(0, e.getKey().length()), attr); // write with the style

                // unstyle the non-matched part
                text = text.substring(e.getKey().length()); // substract the matched part from the original text
                log(text, getDefaultAttributes());
            }
        }

        if(!severityFound) {
            log(text, getDefaultAttributes());
        }
    }

    public static void logln(String text) {
        log(text + '\n');
    }

    public static void logln(String text, AttributeSet attributeSet) {
        log(text + '\n', attributeSet);
    }

    public static void log(final String text, AttributeSet attributeSet) {
        SwingUtilities.invokeLater(() -> {
            // clear "no logs" message
            if(noLogs) {
                textPane.setText("");
                noLogs = false;
            }

            try {
                Document doc = textPane.getDocument();
                doc.insertString(doc.getLength(), text, attributeSet);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void clearLogs() {
        textPane.setText(NO_LOGS);
        noLogs = true;
    }
}

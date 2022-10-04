package Common.logging;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogPanel {
    private static final ArrayList<JTextPane> textPaneInstances = new ArrayList<>();
    private static final ArrayList<JScrollPane> scrollInstances = new ArrayList<>();
    private static final ArrayList<ArrayList<LogsListener>> listenersArr = new ArrayList<>();
    private static final ArrayList<Boolean> noLogs = new ArrayList<>();
    private static final ArrayList<String> noLogsMsgs = new ArrayList<>();

    private static MutableAttributeSet defaultTextAttributes;
    private static final HashMap<String, Color> SEVERITIES = new HashMap<>() {{
        put("HIBA:", new Color(196, 33, 33));
        put("VIGYÁZAT:", new Color(232, 87, 17));
        put("MEGJEGYZÉS:", new Color(76, 195, 161));
    }};

    public static int createNewInstance(String noLogsMsg) {
        JTextPane newPane = new JTextPane();
        Color fG = new Color(230, 230, 230);
        Color bG = new Color(30, 30, 30);

        defaultTextAttributes = new SimpleAttributeSet(newPane.getInputAttributes());
        StyleConstants.setLineSpacing(defaultTextAttributes, 0.2f);
        StyleConstants.setBackground(defaultTextAttributes, bG);
        StyleConstants.setForeground(defaultTextAttributes, fG);

        newPane.setBackground(bG);
        newPane.setForeground(fG);
        newPane.setCharacterAttributes(defaultTextAttributes, false);
        newPane.setParagraphAttributes(defaultTextAttributes, false);
        newPane.setEditable(false);

        newPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane newScroll = new JScrollPane(newPane);
        newScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        ArrayList<LogsListener> newListeners = new ArrayList<>();

        textPaneInstances.add(newPane);
        scrollInstances.add(newScroll);
        listenersArr.add(newListeners);
        noLogs.add(false);
        noLogsMsgs.add(noLogsMsg);

        int instance = textPaneInstances.size() - 1;
        clearLogs(instance); // text: No logs
        return instance;
    }

    public static JScrollPane getScrollableTextArea(int instance) {
        try {
            return scrollInstances.get(instance);
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("CODE ERROR: Failed to access log scroll instance " + instance);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static MutableAttributeSet getDefaultAttributes() {
        return defaultTextAttributes;
    }

    public static void log(String text, int instance) {
        try {
            boolean severityFound = false;
            for (Map.Entry<String, Color> e : SEVERITIES.entrySet()) {
                if (text.startsWith(e.getKey())) {
                    severityFound = true;
                    MutableAttributeSet attr = new SimpleAttributeSet(textPaneInstances.get(instance).getInputAttributes());

                    // style the matched part
                    StyleConstants.setBackground(attr, e.getValue());
                    StyleConstants.setBold(attr, true);
                    log(text.substring(0, e.getKey().length()), attr, instance); // write with the style

                    // unstyle the non-matched part
                    text = text.substring(e.getKey().length()); // substract the matched part from the original text
                    log(text, getDefaultAttributes(), instance);
                }
            }

            if(!severityFound) {
                log(text, getDefaultAttributes(), instance);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("CODE ERROR: Failed to access log instance " + instance);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void logln(String text, int instance) {
        log(text + '\n', instance);
    }
    public static void logln(String text, AttributeSet attributeSet, int instance) {
        log(text + '\n', attributeSet, instance);
    }
    public static void log(final String text, AttributeSet attributeSet, int instance) {
        SwingUtilities.invokeLater(() -> {
            try {
                // clear "no logs" message
                if(noLogs.get(instance)) {
                    textPaneInstances.get(instance).setText("");
                    noLogs.set(instance, false);

                    for (LogsListener hl : listenersArr.get(instance))
                        hl.logsCleared();
                }

                try {
                    Document doc = textPaneInstances.get(instance).getDocument();
                    doc.insertString(doc.getLength(), text, attributeSet);

                    for (LogsListener hl : listenersArr.get(instance))
                        hl.logsWritten();

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

            } catch (IndexOutOfBoundsException e) {
                System.out.println("CODE ERROR: Failed to write to log instance " + instance);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
    public static void addListener(LogsListener toAdd, int instance) {
        try {
            listenersArr.get(instance).add(toAdd);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("CODE ERROR: Failed to access log listener instance " + instance);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void clearLogs(int instance) {
        try {
            textPaneInstances.get(instance).setText(noLogsMsgs.get(instance));
            noLogs.set(instance, true);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("CODE ERROR: Failed to clear log instance " + instance);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void logAll(String text) {
        for(int i =0; i < textPaneInstances.size(); i++) {
            log(text, i);
        }
    }
    public static void logAll(String text, AttributeSet attributeSet) {
        for(int i =0; i < textPaneInstances.size(); i++) {
            log(text, attributeSet, i);
        }
    }
    public static void loglnAll(String text) {
        for(int i =0; i < textPaneInstances.size(); i++) {
            logln(text, i);
        }
    }
    public static void loglnAll(String text, AttributeSet attributeSet) {
        for(int i =0; i < textPaneInstances.size(); i++) {
            logln(text, attributeSet, i);
        }
    }
    public static void addListenerToAll(LogsListener toAdd) {
        for(int i =0; i < listenersArr.size(); i++) {
            addListener(toAdd, i);
        }
    }
    public static void clearLogsInAll() {
        for(int i =0; i < textPaneInstances.size(); i++) {
            clearLogs(i);
        }
    }
}

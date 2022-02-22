package HomeworkGatherer.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChanged implements DocumentListener {
    @Override
    public void changedUpdate(DocumentEvent e){}

    @Override
    public void insertUpdate(DocumentEvent e) {
        onContentChange(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onContentChange(e);
    }

    abstract protected void onContentChange(DocumentEvent e);
}

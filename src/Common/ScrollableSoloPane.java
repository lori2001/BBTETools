package Common;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.GridLayout;

public class ScrollableSoloPane extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public ScrollableSoloPane() {
        super(new GridLayout(1, 1));

        add(tabbedPane);

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public void addTab(JComponent tab, String tabTitle, String tip) {
        tabbedPane.addTab(tabTitle, null, tab, tip);
    }

    public void setSelectedIndex(int index){
        tabbedPane.setSelectedIndex(index);
    }
}
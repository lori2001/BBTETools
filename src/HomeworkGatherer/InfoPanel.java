package HomeworkGatherer;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.GridLayout;

public class InfoPanel extends JPanel {
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public InfoPanel() {
        super(new GridLayout(1, 1));

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public void addTab(JComponent tab, String tabTitle, String tip) {
        tabbedPane.addTab(tabTitle, null, tab, tip);
    }

    public void setSelectedIndex(int index){
        tabbedPane.setSelectedIndex(index);
    }
}
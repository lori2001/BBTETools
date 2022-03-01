package HomeworkGatherer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.*;

public class FileInput extends JPanel {
    private final JLabel currFolderLoc = new JLabel();
    private final JButton changeFolder = new JButton();
    private final JFileChooser chooser;

    private Path path;

    public FileInput(String text, Point pos, Point size, String pathStr) {
        setBounds(pos.x, pos.y, size.x, size.y);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        path = Paths.get(pathStr);
        chooser = new JFileChooser(pathStr);

        JLabel textLabel = new JLabel();
        textLabel.setText(text);
        add(textLabel);

        currFolderLoc.setText(path.toString());
        currFolderLoc.setPreferredSize(new Dimension((int) (size.x * 0.6), size.y));
        add(currFolderLoc);

        changeFolder.setText("Cserélj");
        changeFolder.setPreferredSize(new Dimension((int) (size.x * 0.2), size.y));
        add(changeFolder);

        setVisible(true);

        chooser.setCurrentDirectory(new File(currFolderLoc.getText()));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        changeFolder.addActionListener(e -> {
            // choose directory
            chooser.showOpenDialog(changeFolder);
            File selectedDir = chooser.getSelectedFile();

            if(selectedDir != null) {
                // refresh on-screen location display
                currFolderLoc.setText(selectedDir.toString());

                path = Path.of(selectedDir.getPath());

                // start next chooser from new location
                chooser.setCurrentDirectory(selectedDir);
            }
        });
    }

    public Path getPath() {
        return path;
    }
}

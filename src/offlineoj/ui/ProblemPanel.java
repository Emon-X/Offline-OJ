package offlineoj.ui;

import javax.swing.*;
import java.awt.*;

public class ProblemPanel extends JPanel {
    private JTextArea descriptionArea;
    private JLabel titleLabel;

    public ProblemPanel(CustomInputPanel customInputPanel) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Problem Viewer"));

        titleLabel = new JLabel("Select a Problem", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JSplitPane contentSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentSplit.setTopComponent(new JScrollPane(descriptionArea));
        contentSplit.setBottomComponent(customInputPanel);
        contentSplit.setDividerLocation(450);
        contentSplit.setResizeWeight(0.7);

        add(contentSplit, BorderLayout.CENTER);
    }

    public void setProblem(String title, String description) {
        titleLabel.setText(title);
        descriptionArea.setText(description);
    }
}

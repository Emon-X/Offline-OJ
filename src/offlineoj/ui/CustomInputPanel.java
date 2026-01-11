package offlineoj.ui;

import javax.swing.*;
import java.awt.*;

public class CustomInputPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;

    public CustomInputPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Custom Input & Output"));

        // Split Input and Output
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input"));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, outputScroll);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);
    }

    public String getInput() {
        return inputArea.getText();
    }

    public void setOutput(String output) {
        outputArea.setText(output);
    }

    public void clearOutput() {
        outputArea.setText("");
    }
}

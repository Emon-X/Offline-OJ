package offlineoj.ui;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {
    private JTextArea outputArea;
    private JLabel verdictLabel;

    public ResultPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Result / Output"));
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        verdictLabel = new JLabel("Status: Ready");
        verdictLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusPanel.add(verdictLabel);
        
        add(statusPanel, BorderLayout.NORTH);
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }
    
    public void setVerdict(String verdict, Color color) {
        verdictLabel.setText("Status: " + verdict);
        verdictLabel.setForeground(color);
    }
    
    public void appendOutput(String text) {
        outputArea.append(text + "\n");
    }
    
    public void clearOutput() {
        outputArea.setText("");
    }
}

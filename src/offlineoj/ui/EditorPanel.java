package offlineoj.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EditorPanel extends JPanel {
    private JTextArea codeArea;
    private JComboBox<String> languageBox;
    private JButton runButton;
    private JButton submitButton;

    public EditorPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Code Editor"));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languageBox = new JComboBox<>(new String[] { "C", "C++", "Java" });
        runButton = new JButton("Run Sample");
        submitButton = new JButton("Submit");

        toolbar.add(new JLabel("Language:"));
        toolbar.add(languageBox);
        toolbar.add(runButton);
        toolbar.add(submitButton);

        add(toolbar, BorderLayout.NORTH);

        // Code Area
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(codeArea), BorderLayout.CENTER);

        languageBox.addActionListener(e -> updateTemplate());
        updateTemplate(); // Set initial
    }

    private void updateTemplate() {
        String lang = (String) languageBox.getSelectedItem();
        if (lang == null)
            return;

        String template = "";
        switch (lang) {
            case "C":
                template = "#include <stdio.h>\n\nint main() {\n    // Your code here\n    return 0;\n}";
                break;
            case "C++":
                template = "#include <iostream>\nusing namespace std;\n\nint main() {\n    // Your code here\n    return 0;\n}";
                break;
            case "Java":
                template = "import java.util.Scanner;\n\npublic class Solution {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        // Your code here\n    }\n}";
                break;
        }
        codeArea.setText(template);
    }

    public String getCode() {
        return codeArea.getText();
    }

    public String getLanguage() {
        return (String) languageBox.getSelectedItem();
    }

    public void addRunListener(ActionListener listener) {
        runButton.addActionListener(listener);
    }

    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }
}

package offlineoj.ui;

import javax.swing.*;
import java.awt.*;
import offlineoj.core.JudgeService;
import offlineoj.core.VerdictResult;
import java.nio.file.*;

public class MainFrame extends JFrame {
    private ProblemPanel problemPanel;
    private EditorPanel editorPanel;
    private ResultPanel resultPanel;
    private CustomInputPanel customInputPanel;
    private JComboBox<String> problemSelector;
    private String currentProblemId = "A";

    public MainFrame() {
        setTitle("Offline Online Judge Simulator");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        checkSystemRequirements();
    }

    private void checkSystemRequirements() {
        java.util.List<String> missing = JudgeService.checkEnvironment();
        if (!missing.isEmpty()) {
            StringBuilder msg = new StringBuilder("The following usage tools seem missing from your PATH:\n");
            for (String s : missing)
                msg.append("- ").append(s).append("\n");
            msg.append("\nSome submissions may fail.");
            JOptionPane.showMessageDialog(this, msg.toString(), "Environment Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        // Main Layout
        setLayout(new BorderLayout());

        // Toolbar for Problem Selection
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Current Problem: "));
        problemSelector = new JComboBox<>(new String[] { "A", "B" });
        problemSelector.addActionListener(e -> {
            currentProblemId = (String) problemSelector.getSelectedItem();
            loadProblem(currentProblemId);
        });
        topBar.add(problemSelector);
        add(topBar, BorderLayout.NORTH);

        // Panels
        resultPanel = new ResultPanel();
        customInputPanel = new CustomInputPanel();
        problemPanel = new ProblemPanel(customInputPanel);
        editorPanel = new EditorPanel();

        // Setup initial dummy data
        loadProblem("A");

        // Split Pane Program Description vs Editor
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, problemPanel, createRightPanel());
        mainSplit.setDividerLocation(400);

        add(mainSplit, BorderLayout.CENTER);

        // Run Sample Action
        JudgeService judgeService = new JudgeService();
        editorPanel.addRunListener(e -> {
            runCustomInput(judgeService);
        });

        // Submit Action
        editorPanel.addSubmitListener(e -> {
            runJudge(judgeService, true);
        });
    }

    private void runJudge(JudgeService judgeService, boolean includeRandom) {
        resultPanel.clearOutput();
        resultPanel.setVerdict("Running...", Color.BLUE);
        String code = editorPanel.getCode();
        String lang = editorPanel.getLanguage();
        String problemId = currentProblemId;

        new SwingWorker<VerdictResult, Void>() {
            @Override
            protected VerdictResult doInBackground() throws Exception {
                return judgeService.judge(code, lang, problemId, includeRandom);
            }

            @Override
            protected void done() {
                try {
                    VerdictResult result = get();
                    Color c = Color.BLACK;
                    switch (result.verdict) {
                        case ACCEPTED:
                            c = new Color(0, 128, 0);
                            break;
                        case WRONG_ANSWER:
                            c = Color.RED;
                            break;
                        case COMPILATION_ERROR:
                            c = Color.ORANGE;
                            break;
                        case RUNTIME_ERROR:
                            c = Color.MAGENTA;
                            break;
                        case TIME_LIMIT_EXCEEDED:
                            c = Color.GRAY;
                            break;
                        default:
                            c = Color.BLACK;
                    }
                    resultPanel.setVerdict(result.verdict.toString(), c);
                } catch (Exception ex) {
                    resultPanel.setVerdict("Error", Color.RED);
                    resultPanel.appendOutput(ex.getMessage());
                }
            }
        }.execute();
    }

    private void runCustomInput(JudgeService judgeService) {
        customInputPanel.clearOutput();
        String code = editorPanel.getCode();
        String lang = editorPanel.getLanguage();
        String input = customInputPanel.getInput();

        new SwingWorker<VerdictResult, Void>() {
            @Override
            protected VerdictResult doInBackground() throws Exception {
                return judgeService.runCustomInput(code, lang, input);
            }

            @Override
            protected void done() {
                try {
                    VerdictResult result = get();
                    customInputPanel.setOutput(result.actualOutput);
                    if (result.verdict == offlineoj.core.Verdict.RUNTIME_ERROR
                            || result.verdict == offlineoj.core.Verdict.COMPILATION_ERROR) {
                        customInputPanel.setOutput(result.message + "\n" + result.actualOutput);
                    }
                } catch (Exception ex) {
                    customInputPanel.setOutput(ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadProblem(String problemId) {
        try {
            String desc = Files.readString(Path.of("problems", problemId, "description.txt"));
            problemPanel.setProblem("Problem " + problemId, desc);
            problemPanel.revalidate();
            problemPanel.repaint();
        } catch (Exception e) {
            problemPanel.setProblem("Error loading problem", e.getMessage());
            problemPanel.revalidate();
            problemPanel.repaint();
        }
    }

    private JComponent createRightPanel() {
        JSplitPane editorSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPanel, resultPanel);
        editorSplit.setDividerLocation(500);
        return editorSplit;
    }

    public EditorPanel getEditorPanel() {
        return editorPanel;
    }

    public ResultPanel getResultPanel() {
        return resultPanel;
    }

    public ProblemPanel getProblemPanel() {
        return problemPanel;
    }
}

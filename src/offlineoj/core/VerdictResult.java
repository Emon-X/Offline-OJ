package offlineoj.core;

public class VerdictResult {
    public Verdict verdict;
    public String message;
    public String actualOutput;
    public String inputUsed;
    public String expectedOutput;

    public VerdictResult(Verdict verdict, String message) {
        this(verdict, message, "", "", "");
    }

    public VerdictResult(Verdict verdict, String message, String actualOutput, String inputUsed,
            String expectedOutput) {
        this.verdict = verdict;
        this.message = message;
        this.actualOutput = actualOutput;
        this.inputUsed = inputUsed;
        this.expectedOutput = expectedOutput;
    }

    @Override
    public String toString() {
        return verdict + ": " + message;
    }
}

package offlineoj;

import offlineoj.core.JudgeService;
import offlineoj.core.VerdictResult;

public class TestRunner {
    public static void main(String[] args) {
        JudgeService judge = new JudgeService();
        String problemId = "A";

        System.out.println("Testing Correct Solution (C)...");
        String correctC = "#include <stdio.h>\nint main() { int a, b; scanf(\"%d %d\", &a, &b); printf(\"%d\\n\", a+b); return 0; }";
        VerdictResult res = judge.judge(correctC, "C", problemId);
        System.out.println(res);

        System.out.println("\nTesting Wrong Answer (C)...");
        String wrongC = "#include <stdio.h>\nint main() { int a, b; scanf(\"%d %d\", &a, &b); printf(\"%d\\n\", a-b); return 0; }";
        res = judge.judge(wrongC, "C", problemId);
        System.out.println(res);

        System.out.println("\nTesting C++ Solution...");
        String cppCode = "#include <iostream>\nusing namespace std;\nint main() { int a, b; cin >> a >> b; cout << a + b << endl; return 0; }";
        res = judge.judge(cppCode, "C++", problemId);
        System.out.println(res);

        System.out.println("\nTesting Compile Error...");
        String ceCode = "int main() { return 0; ";
        res = judge.judge(ceCode, "C", problemId);
        System.out.println(res);
    }
}

package offlineoj.core;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.*;

public class JudgeService {
    private static final String SUBMISSIONS_DIR = "submissions";
    private static final String BIN_DIR = "bin_exec";
    private static final long TIME_LIMIT_MS = 2000;

    public JudgeService() {
        new File(SUBMISSIONS_DIR).mkdirs();
        new File(BIN_DIR).mkdirs();
    }

    public static List<String> checkEnvironment() {
        List<String> missing = new ArrayList<>();
        if (!checkCommand("gcc"))
            missing.add("GCC (C Compiler)");
        if (!checkCommand("g++"))
            missing.add("G++ (C++ Compiler)");
        if (!checkCommand("javac"))
            missing.add("JDK (Java Compiler)");
        return missing;
    }

    private static boolean checkCommand(String cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd, "--version");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.getInputStream().readAllBytes();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public VerdictResult runCustomInput(String code, String language, String input) {
        // 1. Save Code
        String fileName = "Solution";
        String extension = getExtension(language);
        File sourceFile = new File(SUBMISSIONS_DIR, fileName + extension);
        try {
            Files.write(sourceFile.toPath(), code.getBytes());
        } catch (IOException e) {
            return new VerdictResult(Verdict.INTERNAL_ERROR, "Failed to write submit file: " + e.getMessage());
        }

        // 2. Compile User Code
        CompileResult compileResult = compile(sourceFile, language, "Solution");
        if (!compileResult.success) {
            return new VerdictResult(Verdict.COMPILATION_ERROR, compileResult.output);
        }

        // 3. Create temporary input file
        File tempInputFile = null;
        try {
            tempInputFile = File.createTempFile("custom_input", ".in");
            Files.writeString(tempInputFile.toPath(), input);
        } catch (IOException e) {
            return new VerdictResult(Verdict.INTERNAL_ERROR, "Failed to create input file: " + e.getMessage());
        }

        // 4. Run User Code
        ExecutionResult execResult = execute(compileResult.executablePath, language, tempInputFile, "Solution");

        // Cleanup
        tempInputFile.delete();

        if (execResult.timedOut) {
            return new VerdictResult(Verdict.TIME_LIMIT_EXCEEDED, "Time limit exceeded", execResult.output, input, "");
        }

        if (execResult.exitCode != 0) {
            return new VerdictResult(Verdict.RUNTIME_ERROR, "Runtime Error\nError:\n" + execResult.error,
                    execResult.output, input, "");
        }

        return new VerdictResult(Verdict.ACCEPTED, "Executed successfully", execResult.output.trim(), input, "");
    }

    public VerdictResult judge(String code, String language, String problemId, boolean includeRandomTests) {
        // 1. Save Code
        String fileName = "Solution";
        String extension = getExtension(language);
        File sourceFile = new File(SUBMISSIONS_DIR, fileName + extension);
        try {
            Files.write(sourceFile.toPath(), code.getBytes());
        } catch (IOException e) {
            return new VerdictResult(Verdict.INTERNAL_ERROR, "Failed to write submit file: " + e.getMessage());
        }

        // 2. Compile User Code
        CompileResult compileResult = compile(sourceFile, language, "Solution");
        if (!compileResult.success) {
            return new VerdictResult(Verdict.COMPILATION_ERROR, compileResult.output);
        }

        // 3. Run Static Test Cases
        File problemDir = new File("problems", problemId);
        File inputDir = new File(problemDir, "input");
        File outputDir = new File(problemDir, "output");

        if (!inputDir.exists() || !outputDir.exists()) {
            return new VerdictResult(Verdict.INTERNAL_ERROR, "Problem data not found for ID: " + problemId);
        }

        File[] inputs = inputDir.listFiles();
        VerdictResult lastResult = null;
        if (inputs != null) {
            Arrays.sort(inputs);
            for (File input : inputs) {
                String testCaseName = input.getName();
                String expectedOutputName = testCaseName.replace(".in", ".out");
                File expectedOutputObj = new File(outputDir, expectedOutputName);

                if (!expectedOutputObj.exists())
                    continue;

                lastResult = runTestCase(compileResult.executablePath, language, "Solution", input, expectedOutputObj,
                        testCaseName);
                if (lastResult.verdict != Verdict.ACCEPTED)
                    return lastResult;
            }
        }

        // 4. Run Random Test Cases
        if (includeRandomTests) {
            File generatorFile = new File(problemDir, "generator/Generator.java");
            File modelFile = new File(problemDir, "model/ModelSolution.java");

            if (generatorFile.exists() && modelFile.exists()) {
                CompileResult genCompile = compile(generatorFile, "Java", "Generator");
                if (!genCompile.success)
                    return new VerdictResult(Verdict.INTERNAL_ERROR, "Generator Compilation Failed");

                CompileResult modelCompile = compile(modelFile, "Java", "ModelSolution");
                if (!modelCompile.success)
                    return new VerdictResult(Verdict.INTERNAL_ERROR, "Model Solution Compilation Failed");

                for (int i = 1; i <= 5; i++) {
                    try {
                        // Generate Input
                        File tempInput = File.createTempFile("random", ".in");
                        ExecutionResult genResult = execute(genCompile.executablePath, "Java", null, "Generator");
                        if (genResult.exitCode != 0)
                            return new VerdictResult(Verdict.INTERNAL_ERROR, "Generator Runtime Error");
                        Files.writeString(tempInput.toPath(), genResult.output);

                        // Generate Expected Output
                        File tempOutput = File.createTempFile("random", ".out");
                        ExecutionResult modelResult = execute(modelCompile.executablePath, "Java", tempInput,
                                "ModelSolution");
                        if (modelResult.exitCode != 0)
                            return new VerdictResult(Verdict.INTERNAL_ERROR, "Model Solution Runtime Error");
                        Files.writeString(tempOutput.toPath(), modelResult.output);

                        // Run User Code
                        lastResult = runTestCase(compileResult.executablePath, language, "Solution", tempInput,
                                tempOutput, "Random Case #" + i);

                        // Cleanup
                        tempInput.delete();
                        tempOutput.delete();

                        if (lastResult.verdict != Verdict.ACCEPTED)
                            return lastResult;

                    } catch (IOException e) {
                        return new VerdictResult(Verdict.INTERNAL_ERROR, "Random Testing Error: " + e.getMessage(), "",
                                "", "");
                    }
                }
            }
        }

        return new VerdictResult(Verdict.ACCEPTED, "All test cases passed!",
                lastResult != null ? lastResult.actualOutput : "",
                lastResult != null ? lastResult.inputUsed : "",
                lastResult != null ? lastResult.expectedOutput : "");
    }

    private VerdictResult runTestCase(String execPath, String language, String mainClass, File input,
            File expectedOutputObj, String caseName) {
        String inputContent = "";
        String expectedContent = "";
        try {
            inputContent = Files.readString(input.toPath());
            expectedContent = Files.readString(expectedOutputObj.toPath()).trim();
        } catch (IOException e) {
            inputContent = "Error reading files";
        }

        ExecutionResult execResult = execute(execPath, language, input, mainClass);

        if (execResult.timedOut) {
            return new VerdictResult(Verdict.TIME_LIMIT_EXCEEDED, "Time limit exceeded on " + caseName,
                    execResult.output, inputContent, expectedContent);
        }

        if (execResult.exitCode != 0) {
            return new VerdictResult(Verdict.RUNTIME_ERROR,
                    "Runtime Error on " + caseName + "\nError:\n" + execResult.error, execResult.output, inputContent,
                    expectedContent);
        }

        String actual = execResult.output.trim();

        // Normalize line endings
        String normalizedExpected = expectedContent.replace("\r\n", "\n").replace("\r", "\n");
        String normalizedActual = actual.replace("\r\n", "\n").replace("\r", "\n");

        if (!normalizedExpected.equals(normalizedActual)) {
            return new VerdictResult(Verdict.WRONG_ANSWER, "Failed on " + caseName + "\nExpected:\n" + expectedContent,
                    actual, inputContent, expectedContent);
        }
        return new VerdictResult(Verdict.ACCEPTED, "Passed " + caseName, actual, inputContent, expectedContent);
    }

    private CompileResult compile(File sourceFile, String language, String binaryName) {
        ProcessBuilder pb;
        String absSource = sourceFile.getAbsolutePath();
        String absBin = new File(BIN_DIR).getAbsolutePath();

        if (language.equals("C")) {
            pb = new ProcessBuilder("gcc", "-o", absBin + "/" + binaryName, absSource);
        } else if (language.equals("C++")) {
            pb = new ProcessBuilder("g++", "-o", absBin + "/" + binaryName, absSource);
        } else if (language.equals("Java")) {
            pb = new ProcessBuilder("javac", "-d", absBin, absSource);
        } else {
            return new CompileResult(false, "Unsupported language", "");
        }

        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes());
            boolean success = p.waitFor(5, TimeUnit.SECONDS) && p.exitValue() == 0;
            String executablePath = absBin + "/" + binaryName;
            return new CompileResult(success, output, executablePath);
        } catch (Exception e) {
            return new CompileResult(false, e.getMessage(), "");
        }
    }

    private ExecutionResult execute(String executablePath, String language, File inputFile, String mainClass) {
        ProcessBuilder pb;
        String absBin = new File(BIN_DIR).getAbsolutePath();

        if (language.equals("Java")) {
            pb = new ProcessBuilder("java", "-cp", absBin, mainClass);
        } else {
            pb = new ProcessBuilder(executablePath);
        }

        if (inputFile != null) {
            pb.redirectInput(inputFile);
        }

        try {
            Process p = pb.start();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            Thread outThread = new Thread(() -> {
                try {
                    p.getInputStream().transferTo(outputStream);
                } catch (IOException e) {
                }
            });
            Thread errThread = new Thread(() -> {
                try {
                    p.getErrorStream().transferTo(errorStream);
                } catch (IOException e) {
                }
            });

            outThread.start();
            errThread.start();

            boolean finished = p.waitFor(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);

            if (!finished) {
                p.destroyForcibly();
                return new ExecutionResult(true, 0, "", "");
            }

            outThread.join();
            errThread.join();

            return new ExecutionResult(false, p.exitValue(), outputStream.toString(), errorStream.toString());

        } catch (Exception e) {
            return new ExecutionResult(false, -1, "", e.getMessage());
        }
    }

    private String getExtension(String lang) {
        switch (lang) {
            case "C":
                return ".c";
            case "C++":
                return ".cpp";
            case "Java":
                return ".java";
            default:
                return ".txt";
        }
    }

    private static class CompileResult {
        boolean success;
        String output;
        String executablePath;

        CompileResult(boolean success, String output, String executablePath) {
            this.success = success;
            this.output = output;
            this.executablePath = executablePath;
        }
    }

    private static class ExecutionResult {
        boolean timedOut;
        int exitCode;
        String output;
        String error;

        ExecutionResult(boolean timedOut, int exitCode, String output, String error) {
            this.timedOut = timedOut;
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }
    }
}

package execution;

import IDE.mainWindow;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;

public class Java {
    void runJavaFile(final File sourceFile, final String content) throws IOException, InterruptedException {
        final String className = sourceFile.getName().replace(".java", "");
        File tempDirLocal = sourceFile.getParentFile();

        if (tempDirLocal == null || !tempDirLocal.isDirectory()) {
            tempDirLocal = Files.createTempDirectory("chax_java_run_").toFile();
            tempDirLocal.deleteOnExit();
        }

        final File tempDir = tempDirLocal;

        if (sourceFile.getName().startsWith("temp_run_")) {
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(content);
            }
        }

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                compilePb.directory(tempDir);
                Process compileProcess = compilePb.start();

                readProcessOutput(compileProcess, "[JAVA-COMPILER-ERROR]");
                int compileExitCode = compileProcess.waitFor();

                if (compileExitCode == 0) {
                    publish("[JAVA] Compilation successful.\n");
                    publish("[JAVA] Running " + className + "...\n");

                    ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempDir.getAbsolutePath(), className);
                    runPb.directory(tempDir);
                    Process runProcess = runPb.start();
                    readProcessOutput(runProcess, "[JAVA-RUNTIME-ERROR]");
                    return runProcess.waitFor();
                } else {
                    return compileExitCode;
                }
            }

            private void readProcessOutput(Process process, String errorPrefix) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line + "\n");
                }

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    publish(errorPrefix + " " + line + "\n");
                }
            }
        }.execute();
    }
}

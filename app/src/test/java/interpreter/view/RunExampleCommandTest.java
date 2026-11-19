package interpreter.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import interpreter.controller.Controller;
import interpreter.model.PrgState;
import interpreter.repository.Repository;

class RunExampleCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void executesControllerOnlyOnce() throws Exception {
        Path logFile = tempDir.resolve("example.log");
        Controller controller = new Controller(
                new Repository(PrgState.of(Examples.EX1), logFile.toString()), true);
        RunExampleCommand command = new RunExampleCommand("1", "example", controller);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        String logAfterFirstExecution;

        try (PrintStream capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            command.execute();
            logAfterFirstExecution = Files.readString(logFile);
            command.execute();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("This example has already been executed."));
        assertEquals(logAfterFirstExecution, Files.readString(logFile));
    }
}

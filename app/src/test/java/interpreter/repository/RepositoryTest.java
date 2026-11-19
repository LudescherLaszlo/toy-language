package interpreter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.view.Examples;

class RepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void loggingAppendsProgramStates() throws Exception {
        Path logFile = tempDir.resolve("log.txt");
        Repository repository = new Repository(PrgState.of(Examples.EX1), logFile.toString());

        repository.logPrgStateExec();
        repository.logPrgStateExec();

        String log = Files.readString(logFile);
        assertEquals(2, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void loggingToDirectoryThrowsRepositoryException() {
        Repository repository = new Repository(PrgState.of(Examples.EX1), tempDir.toString());

        assertThrows(RepositoryException.class, repository::logPrgStateExec);
    }
}

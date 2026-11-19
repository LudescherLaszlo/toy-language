package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;

    @BeforeEach
    void setUp() {
        state = PrgState.of(new NopStmt());
    }

    @AfterEach
    void closeOpenFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values()) {
            reader.close();
        }
    }

    @Test
    void openingFileAddsItToFileTable() throws IOException, MyException {
        Path file = dir.resolve("input.txt");
        Files.writeString(file, "input");
        var fileName = new StringValue(file.toString());

        PrgState result = new OpenRFileStmt(new ValueExp(fileName)).execute(state);

        assertSame(state, result);
        assertTrue(state.getFileTable().isDefined(fileName));
        assertNotNull(state.getFileTable().lookup(fileName));
    }

    @Test
    void openingFileWithNonStringExpressionThrowsException() {
        var stmt = new OpenRFileStmt(new ValueExp(new IntValue(1)));

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void openingSameFileTwiceThrowsException() throws IOException, MyException {
        Path file = dir.resolve("input.txt");
        Files.writeString(file, "input");
        var stmt = new OpenRFileStmt(new ValueExp(new StringValue(file.toString())));
        stmt.execute(state);

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void openingFileThatDoesNotExistThrowsException() {
        Path file = dir.resolve("missing.txt");
        var stmt = new OpenRFileStmt(new ValueExp(new StringValue(file.toString())));

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }
}

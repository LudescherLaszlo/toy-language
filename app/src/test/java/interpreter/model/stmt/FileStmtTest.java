package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void readFileStoresLinesAsIntegersAndStoresZeroAtEndOfFile() throws IOException, MyException {
        Path file = dir.resolve("numbers.txt");
        Files.writeString(file, "7\n8\n");
        var fileName = new StringValue(file.toString());
        new OpenRFileStmt(new ValueExp(fileName)).execute(state);
        state.getSymTable().put("n", new IntValue(0));
        var stmt = new ReadFileStmt(new ValueExp(fileName), "n");

        stmt.execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("n"));
        stmt.execute(state);
        assertEquals(new IntValue(8), state.getSymTable().lookup("n"));
        stmt.execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("n"));
    }

    @Test
    void readFileWithUndeclaredVariableThrowsException() {
        var stmt = new ReadFileStmt(new ValueExp(new StringValue("input.txt")), "n");

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void readFileWithNonIntegerVariableThrowsException() {
        state.getSymTable().put("text", new StringValue(""));
        var stmt = new ReadFileStmt(new ValueExp(new StringValue("input.txt")), "text");

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void readFileWithNonStringExpressionThrowsException() {
        state.getSymTable().put("n", new IntValue(0));
        var stmt = new ReadFileStmt(new ValueExp(new IntValue(1)), "n");

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void readFileWithFileNotOpenThrowsException() {
        state.getSymTable().put("n", new IntValue(0));
        var stmt = new ReadFileStmt(new ValueExp(new StringValue("input.txt")), "n");

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void readFileWithNonIntegerLineThrowsException() throws IOException, MyException {
        Path file = dir.resolve("invalid.txt");
        Files.writeString(file, "abc\n");
        var fileName = new StringValue(file.toString());
        new OpenRFileStmt(new ValueExp(fileName)).execute(state);
        state.getSymTable().put("n", new IntValue(0));
        var stmt = new ReadFileStmt(new ValueExp(fileName), "n");

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void closeRFileClosesReaderAndRemovesFileFromTable() throws IOException, MyException {
        Path file = dir.resolve("input.txt");
        Files.writeString(file, "input");
        var fileName = new StringValue(file.toString());
        new OpenRFileStmt(new ValueExp(fileName)).execute(state);
        BufferedReader reader = state.getFileTable().lookup(fileName);

        new CloseRFileStmt(new ValueExp(fileName)).execute(state);

        assertFalse(state.getFileTable().isDefined(fileName));
        assertThrows(IOException.class, reader::readLine);
    }

    @Test
    void closeRFileWithFileNotOpenThrowsException() {
        var stmt = new CloseRFileStmt(new ValueExp(new StringValue("input.txt")));

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void closeRFileWithNonStringExpressionThrowsException() {
        var stmt = new CloseRFileStmt(new ValueExp(new IntValue(1)));

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void fileStatementsHaveExpectedStringRepresentations() {
        var exp = new ValueExp(new StringValue("input.txt"));

        assertEquals("readFile(\"input.txt\", n)", new ReadFileStmt(exp, "n").toString());
        assertEquals("closeRFile(\"input.txt\")", new CloseRFileStmt(exp).toString());
    }
}

package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.OpenRFileStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;

class ControllerTest {
    @TempDir
    Path dir;

    private PrgState state;

    private PrgState run(IStmt program) throws MyException {
        state = PrgState.of(program);
        new Controller(new Repository(state, dir.resolve("log.txt").toString()), false).allStep();
        return state;
    }

    @AfterEach
    void closeOpenFiles() throws IOException {
        if (state != null) {
            for (BufferedReader reader : state.getFileTable().toMap().values()) {
                reader.close();
            }
        }
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(18), state.getOut().get(0)); 
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void allStepLogsEveryStateInSectionOrder() throws MyException, IOException {
        PrgState state = run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        String[] entries = log.split("(?=ExeStack:)");

        assertEquals(6, entries.length);
        assertEquals(6, log.split("ExeStack:", -1).length - 1);
        for (String entry : entries) {
            assertTrue(entry.indexOf("ExeStack:") < entry.indexOf("SymTable:"));
            assertTrue(entry.indexOf("SymTable:") < entry.indexOf("Out:"));
            assertTrue(entry.indexOf("Out:") < entry.indexOf("FileTable:"));
        }
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void oneStepOnEmptyStackThrowsException() throws ADTException {
        PrgState state = PrgState.of(Examples.EX1);
        Controller controller = new Controller(new Repository(state, dir.resolve("log.txt").toString()), false);
        
        // Manually empty the stack to trigger the exception
        state.getExeStack().pop();
        
        // Controller test: oneStep on an empty stack throws
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void fileExamplePrintsTwoLinesAndClosesFile() throws Exception {
        Path input = dir.resolve("numbers.txt");
        Files.writeString(input, "15\n50\n");

        PrgState state = run(Examples.fileExample(input.toString()));

        assertEquals(List.of(new IntValue(15), new IntValue(50)), state.getOut().toList());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExamplePrintsZeroTwiceForEmptyFileAndClosesFile() throws Exception {
        Path input = dir.resolve("empty.txt");
        Files.createFile(input);

        PrgState state = run(Examples.fileExample(input.toString()));

        assertEquals(List.of(new IntValue(0), new IntValue(0)), state.getOut().toList());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState state = run(Examples.EX5);

        assertEquals(List.of(new IntValue(5)), state.getOut().toList());
    }

    @Test
    void openingMissingFileThrowsStatementException() {
        IStmt program = new OpenRFileStmt(
                new ValueExp(new StringValue(dir.resolve("missing.txt").toString())));

        assertThrows(StatementException.class, () -> run(program));
    }
}
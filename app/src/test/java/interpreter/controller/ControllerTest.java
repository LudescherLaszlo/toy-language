package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;

class ControllerTest {
    @TempDir
    Path tempDir;

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, tempDir.resolve("log.txt").toString()), false).allStep(); // display off
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.ex1);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.ex2);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(18), state.getOut().get(0)); 
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.ex3);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void allStepLogsEveryStateInSectionOrder() throws MyException, IOException {
        PrgState state = run(Examples.ex1);
        String log = Files.readString(tempDir.resolve("log.txt"));
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
        PrgState state = PrgState.of(Examples.ex1);
        Controller controller = new Controller(new Repository(state, tempDir.resolve("log.txt").toString()), false);
        
        // Manually empty the stack to trigger the exception
        state.getExeStack().pop();
        
        // Controller test: oneStep on an empty stack throws
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }
}
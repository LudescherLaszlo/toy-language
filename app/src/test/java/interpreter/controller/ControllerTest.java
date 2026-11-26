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
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.ReadHeapExp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.AssignStmt;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.OpenRFileStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.stmt.WhileStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
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

        assertEquals(11, entries.length);
        assertEquals(11, log.split("ExeStack:", -1).length - 1);
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
    void whileRunsThroughControllerAndRepository() throws MyException, IOException {
        PrgState state = run(countdownProgram(3));

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(List.of(new IntValue(3), new IntValue(2), new IntValue(1)), state.getOut().toList());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
        assertFalse(Files.readString(dir.resolve("log.txt")).isEmpty());
    }

    @Test
    void whileRunsExactlyOnceWhenInitiallyOne() throws MyException {
        PrgState state = run(countdownProgram(1));

        assertEquals(List.of(new IntValue(1)), state.getOut().toList());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void safeGarbageCollectionPreservesNestedReferences() throws MyException {
        PrgState state = run(nestedReferenceProgram());

        assertEquals(List.of(new IntValue(20)), state.getOut().toList());
    }

    @Test
    void unsafeGarbageCollectionDropsNestedReferenceTarget() throws MyException {
        PrgState state = PrgState.of(nestedReferenceProgram());
        Controller controller = new Controller(
                new Repository(state, dir.resolve("unsafe-log.txt").toString()), false);

        for (int step = 0; step < 10; step++) {
            controller.oneStep(state);
            state.getHeap().setContent(GarbageCollector.unsafeGarbageCollector(
                    GarbageCollector.getAddrFromSymTable(state.getSymTable().toMap().values()),
                    state.getHeap().getContent()));
        }

        ExpressionException exception = assertThrows(ExpressionException.class,
                () -> controller.oneStep(state));
        assertEquals("rH: address 1 is not in the heap", exception.getMessage());
    }

    @Test
    void openingMissingFileThrowsStatementException() {
        IStmt program = new OpenRFileStmt(
                new ValueExp(new StringValue(dir.resolve("missing.txt").toString())));

        assertThrows(StatementException.class, () -> run(program));
    }

    private IStmt countdownProgram(int start) {
        IStmt body = new CompStmt(
                new PrintStmt(new VarExp("v")),
                new AssignStmt("v",
                        new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1)))));
        IStmt loop = new WhileStmt(
                new RelationalExp(RelOp.GT, new VarExp("v"), new ValueExp(new IntValue(0))),
                body);
        return new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new IntValue(start))),
                        loop));
    }

    private IStmt nestedReferenceProgram() {
        return new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new NewStmt("a", new VarExp("v")),
                                        new CompStmt(
                                                new NewStmt("v",
                                                        new ValueExp(new IntValue(30))),
                                                new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VarExp("a")))))))));
    }
}

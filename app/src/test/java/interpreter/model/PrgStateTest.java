package interpreter.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.IValue;
import interpreter.model.value.StringValue;

class PrgStateTest {
    @Test
    void ofCreatesEmptyStructuresAndPushesProgram() {
        NopStmt program = new NopStmt();

        PrgState state = PrgState.of(program);

        assertEquals(program, state.getExeStack().toList().get(0));
        assertEquals(1, state.getExeStack().toList().size());
        assertEquals(0, state.getSymTable().toMap().size());
        assertEquals(0, state.getOut().size());
        assertEquals(0, state.getFileTable().toMap().size());
        assertEquals(0, state.getHeap().getContent().size());
    }

    @Test
    void toLogStringListsEachSectionAndItsContentsInOrder() {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().push(new PrintStmt(new ValueExp(new IntValue(1))));
        state.getExeStack().push(new PrintStmt(new ValueExp(new IntValue(2))));
        state.getSymTable().put("x", new IntValue(5));
        state.getOut().add(new IntValue(6));
        state.getOut().add(new IntValue(7));
        state.getFileTable().put(new StringValue("input.txt"), new BufferedReader(new StringReader("")));

        String expected = "ExeStack:\n" +
                "print(2)\n" +
                "print(1)\n" +
                "nop\n" +
                "SymTable:\n" +
                "x --> 5\n" +
                "Out:\n" +
                "6\n" +
                "7\n" +
                "FileTable:\n" +
                "input.txt\n" +
                "Heap:\n";

        assertEquals(expected, state.toLogString());
        String display = state.toString();
        assertTrue(display.contains("File Table:\ninput.txt\n"));
        assertTrue(display.contains("Heap:\n{}"));
        assertFalse(display.contains("BufferedReader"));
    }

    @Test
    void toLogStringSortsHeapEntriesByAddress() {
        PrgState state = PrgState.of(new NopStmt());
        Map<Integer, IValue> content = new LinkedHashMap<>();
        content.put(3, new IntValue(30));
        content.put(1, new IntValue(10));
        content.put(2, new IntValue(20));
        state.getHeap().setContent(content);

        String log = state.toLogString();

        assertTrue(log.contains("Heap:\n1 --> 10\n2 --> 20\n3 --> 30\n"));
        assertTrue(log.indexOf("ExeStack:") < log.indexOf("SymTable:"));
        assertTrue(log.indexOf("SymTable:") < log.indexOf("Out:"));
        assertTrue(log.indexOf("Out:") < log.indexOf("FileTable:"));
        assertTrue(log.indexOf("FileTable:") < log.indexOf("Heap:"));
    }
}

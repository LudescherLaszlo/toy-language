package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;

class HeapStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
    }

    @Test
    void newAllocatesIncreasingAddresses() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(10))).execute(state);

        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(10), state.getHeap().get(1));

        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(2));
    }

    @Test
    void newRejectsUndeclaredNonReferenceAndWrongType() throws MyException {
        assertThrows(StatementException.class,
                () -> new NewStmt("missing", new ValueExp(new IntValue(1))).execute(state));

        new VarDeclStmt("i", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new NewStmt("i", new ValueExp(new IntValue(1))).execute(state));

        assertThrows(StatementException.class,
                () -> new NewStmt("v", new ValueExp(new RefValue(0, new IntType()))).execute(state));
    }

    @Test
    void newCanAllocateAReferenceInsideAReference() throws MyException {
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(42))).execute(state);

        new NewStmt("a", new VarExp("v")).execute(state);

        assertEquals(new RefValue(2, new RefType(new IntType())), state.getSymTable().lookup("a"));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().get(2));
    }

    @Test
    void writeHeapUpdatesTheReferencedLocation() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(10))).execute(state);

        new WriteHeapStmt("v", new ValueExp(new IntValue(20))).execute(state);

        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void writeHeapRejectsNonReferenceInvalidAddressAndWrongType() throws MyException {
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("missing", new ValueExp(new IntValue(1))).execute(state));

        new VarDeclStmt("i", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("i", new ValueExp(new IntValue(1))).execute(state));
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("v", new ValueExp(new IntValue(1))).execute(state));

        new NewStmt("v", new ValueExp(new IntValue(10))).execute(state);
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("v", new ValueExp(new RefValue(0, new IntType()))).execute(state));
    }

    @Test
    void heapStatementsHaveExpectedStringForms() {
        var value = new ValueExp(new IntValue(1));

        assertEquals("new(v, 1)", new NewStmt("v", value).toString());
        assertEquals("wH(v, 1)", new WriteHeapStmt("v", value).toString());
    }
}

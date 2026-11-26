package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;

class ReadHeapExpTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = PrgState.of(new NopStmt());
    }

    @Test
    void readsTheStoredValue() throws MyException {
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(42))).execute(state);

        assertEquals(new IntValue(42), new ReadHeapExp(new VarExp("v"))
                .eval(state.getSymTable(), state.getHeap()));
    }

    @Test
    void nestedReadsFollowReferencesThroughTwoHeapLocations() throws MyException {
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(42))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);

        IExp nested = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));

        assertEquals(new IntValue(42), nested.eval(state.getSymTable(), state.getHeap()));
    }

    @Test
    void rejectsNonReferenceAndUndefinedAddress() throws MyException {
        new VarDeclStmt("i", new IntType()).execute(state);
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("i")).eval(state.getSymTable(), state.getHeap()));

        IExp nullAddress = new ValueExp(new RefValue(0, new IntType()));
        ExpressionException exception = assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(nullAddress).eval(state.getSymTable(), state.getHeap()));
        assertEquals("rH: address 0 is not in the heap", exception.getMessage());
    }

    @Test
    void hasExpectedStringForm() {
        assertEquals("rH(v)", new ReadHeapExp(new VarExp("v")).toString());
    }
}

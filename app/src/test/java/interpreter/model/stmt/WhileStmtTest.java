package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;

class WhileStmtTest {
    @Test
    void falseConditionPushesNothing() throws MyException {
        IStmt stmt = new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt());
        PrgState state = PrgState.of(stmt);
        state.getExeStack().pop();

        stmt.execute(state);

        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void trueConditionPushesBodyAboveWhileStatement() throws MyException {
        IStmt body = new NopStmt();
        IStmt stmt = new WhileStmt(new ValueExp(new BoolValue(true)), body);
        PrgState state = PrgState.of(stmt);
        state.getExeStack().pop();

        stmt.execute(state);

        assertEquals(body, state.getExeStack().pop());
        assertEquals(stmt, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void nonBooleanConditionThrowsStatementException() throws MyException {
        IStmt stmt = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        PrgState state = PrgState.of(stmt);
        state.getExeStack().pop();

        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void hasExpectedStringForm() {
        IStmt stmt = new WhileStmt(new ValueExp(new BoolValue(true)), new NopStmt());

        assertEquals("(while (true) nop)", stmt.toString());
    }
}

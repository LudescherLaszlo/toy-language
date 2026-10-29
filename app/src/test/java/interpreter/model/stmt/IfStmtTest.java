package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;

class IfStmtTest {
    private PrgState state;
    private IStmt thenS;
    private IStmt elseS;

    @BeforeEach
    void setUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        thenS = new NopStmt();
        elseS = new NopStmt();
    }

    @Test
    void pushesThenStatementWhenConditionIsTrue() throws MyException {
        IStmt stmt = new IfStmt(new ValueExp(new BoolValue(true)), thenS, elseS);
        stmt.execute(state);
        
        // Statements test: rule S5[cite: 11]
        assertEquals(thenS, state.getExeStack().pop());
    }

    @Test
    void pushesElseStatementWhenConditionIsFalse() throws MyException {
        IStmt stmt = new IfStmt(new ValueExp(new BoolValue(false)), thenS, elseS);
        stmt.execute(state);
        
        // Statements test: rule S5[cite: 11]
        assertEquals(elseS, state.getExeStack().pop());
    }

    @Test
    void nonBooleanConditionThrowsException() {
        IStmt stmt = new IfStmt(new ValueExp(new IntValue(1)), thenS, elseS);
        
        // Statements test: non-boolean if condition error[cite: 11]
        var ex = assertThrows(StatementException.class, () -> stmt.execute(state));
        assertEquals("Condition of if statement is not a boolean.", ex.getMessage());
    }
}
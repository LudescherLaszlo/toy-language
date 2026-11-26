package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.stmt.IfStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;

class RelationalExpTest {
    private final MyDictionary<String, IValue> symTable = new MyDictionary<>();
    private final MyHeap heap = new MyHeap();

    @Test
    void lessThanEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.LT, 1, 2).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.LT, 2, 1).eval(symTable, heap));
    }

    @Test
    void lessThanOrEqualEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.LE, 2, 2).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.LE, 3, 2).eval(symTable, heap));
    }

    @Test
    void equalEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.EQ, 2, 2).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.EQ, 2, 3).eval(symTable, heap));
    }

    @Test
    void notEqualEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.NE, 2, 3).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.NE, 2, 2).eval(symTable, heap));
    }

    @Test
    void greaterThanEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.GT, 3, 2).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.GT, 2, 3).eval(symTable, heap));
    }

    @Test
    void greaterThanOrEqualEvaluatesTrueAndFalse() throws MyException {
        assertEquals(new BoolValue(true), exp(RelOp.GE, 2, 2).eval(symTable, heap));
        assertEquals(new BoolValue(false), exp(RelOp.GE, 1, 2).eval(symTable, heap));
    }

    @Test
    void nonIntegerFirstOperandThrowsExpressionException() {
        var expression = new RelationalExp(RelOp.LT, new ValueExp(new BoolValue(true)), new ValueExp(new IntValue(1)));

        var exception = assertThrows(ExpressionException.class, () -> expression.eval(symTable, heap));
        assertEquals("first operand is not an integer", exception.getMessage());
    }

    @Test
    void nonIntegerSecondOperandThrowsExpressionException() {
        var expression = new RelationalExp(RelOp.LT, new ValueExp(new IntValue(1)), new ValueExp(new BoolValue(true)));

        var exception = assertThrows(ExpressionException.class, () -> expression.eval(symTable, heap));
        assertEquals("second operand is not an integer", exception.getMessage());
    }

    @Test
    void toStringIncludesOperandsAndOperator() {
        var expression = new RelationalExp(RelOp.LT, new VarExp("a"), new ValueExp(new IntValue(7)));

        assertEquals("(a < 7)", expression.toString());
    }

    @Test
    void falseRelationalConditionPushesElseBranch() throws MyException {
        IStmt thenS = new NopStmt();
        IStmt elseS = new NopStmt();
        var state = PrgState.of(new NopStmt());
        var condition = exp(RelOp.LT, 2, 1);
        var statement = new IfStmt(condition, thenS, elseS);

        statement.execute(state);

        assertEquals(elseS, state.getExeStack().pop());
    }

    private RelationalExp exp(RelOp op, int left, int right) {
        return new RelationalExp(op, new ValueExp(new IntValue(left)), new ValueExp(new IntValue(right)));
    }
}

package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.IValue;

class ValueExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();

    @Test
    void evalReturnsCorrectIntValue() throws MyException {
        IValue expectedValue = new IntValue(42);
        IExp exp = new ValueExp(expectedValue);
        
        assertEquals(expectedValue, exp.eval(symTable));
    }

    @Test
    void evalReturnsCorrectBoolValue() throws MyException {
        IValue expectedValue = new BoolValue(true);
        IExp exp = new ValueExp(expectedValue);
        
        assertEquals(expectedValue, exp.eval(symTable));
    }
}

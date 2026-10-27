package interpreter.model.exp;

import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;

public class ValueExp {
    private IValue value;

    public ValueExp(IValue value) {
        this.value = value;
    }

    public IValue getValue() {
        return value;
    }

    public void setValue(IValue value) {
        this.value = value;
    }

    public IValue eval(IDictionary<String, IValue> symTable) {
        return value;
    }
}

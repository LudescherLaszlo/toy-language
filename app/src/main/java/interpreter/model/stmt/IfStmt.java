package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record IfStmt(IExp cond, IStmt thenS, IStmt elseS) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        var exeStack = state.getExeStack();
        
        IValue condVal = cond.eval(symTable);
        
        if (!(condVal instanceof BoolValue(boolean isTrue))) {
            throw new StatementException("Condition of if statement is not a boolean.");
        }
        
        if (isTrue) {
            exeStack.push(thenS);
        } else {
            exeStack.push(elseS);
        }
        
        return state;
    }

    @Override
    public String toString() {
        return "if " + cond.toString() + " then " + thenS.toString() + " else " + elseS.toString();
    }
}
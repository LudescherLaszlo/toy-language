package interpreter.model.stmt;

import java.io.BufferedReader;
import java.io.IOException;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.StringValue;

public record CloseRFileStmt(IExp exp) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var value = exp.eval(state.getSymTable());
        if (!(value instanceof StringValue fileName)) {
            throw new StatementException("closeRFile: " + exp + " is not a string");
        }
        if (!state.getFileTable().isDefined(fileName)) {
            throw new StatementException("closeRFile: " + fileName + " is not open");
        }

        BufferedReader reader = state.getFileTable().lookup(fileName);
        try {
            reader.close();
        } catch (IOException e) {
            throw new StatementException("closeRFile: cannot close " + fileName + ": " + e.getMessage());
        }
        state.getFileTable().remove(fileName);
        return state;
    }

    @Override
    public String toString() {
        return "closeRFile(" + exp + ")";
    }
}

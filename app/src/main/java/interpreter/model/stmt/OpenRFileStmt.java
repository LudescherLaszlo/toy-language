package interpreter.model.stmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.StringValue;

public record OpenRFileStmt(IExp exp) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var value = exp.eval(state.getSymTable());
        if (!(value instanceof StringValue fileName)) {
            throw new StatementException("openRFile: " + exp + " is not a string");
        }

        if (state.getFileTable().isDefined(fileName)) {
            throw new StatementException("openRFile: " + fileName + " is already open");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName.val()));
            state.getFileTable().put(fileName, reader);
        } catch (IOException e) {
            throw new StatementException("openRFile: cannot open " + fileName + ": " + e.getMessage());
        }
        return state;
    }

    @Override
    public String toString() {
        return "openRFile(" + exp + ")";
    }
}

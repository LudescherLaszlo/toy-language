package interpreter.model.stmt;

import java.io.BufferedReader;
import java.io.IOException;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

public record ReadFileStmt(IExp exp, String varName) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        if (!symTable.isDefined(varName)) {
            throw new StatementException("readFile: variable " + varName + " was not declared");
        }
        if (!symTable.lookup(varName).getType().equals(new IntType())) {
            throw new StatementException("readFile: variable " + varName + " is not an int");
        }

        var value = exp.eval(symTable);
        if (!(value instanceof StringValue fileName)) {
            throw new StatementException("readFile: " + exp + " is not a string");
        }
        if (!state.getFileTable().isDefined(fileName)) {
            throw new StatementException("readFile: " + fileName + " is not open");
        }

        BufferedReader reader = state.getFileTable().lookup(fileName);
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new StatementException("readFile: cannot read from " + fileName + ": " + e.getMessage());
        }

        int number;
        try {
            number = line == null ? 0 : Integer.parseInt(line.strip());
        } catch (NumberFormatException e) {
            throw new StatementException("readFile: line is not an integer: " + line);
        }
        symTable.update(varName, new IntValue(number));
        return state;
    }

    @Override
    public String toString() {
        return "readFile(" + exp + ", " + varName + ")";
    }
}

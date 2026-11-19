package interpreter.model;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IList;
import interpreter.model.adt.IStack;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IValue;
import interpreter.model.value.StringValue;

public class PrgState {
    private IStack<IStmt> exeStack;
    private IDictionary<String, IValue> symTable;
    private IList<IValue> out;
    private IDictionary<StringValue, BufferedReader> fileTable;
    private final IStmt originalProgram;

    public PrgState(IStack<IStmt> exeStack, IDictionary<String, IValue> symTable,
            IList<IValue> out, IDictionary<StringValue, BufferedReader> fileTable, IStmt program) {
        this.exeStack = exeStack;
        this.symTable = symTable;
        this.out = out;
        this.fileTable = fileTable;
        this.originalProgram = program;
        exeStack.push(program);
    }

    public static PrgState of(IStmt program) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new MyDictionary<>(), program);
    }

    public IStack<IStmt> getExeStack() {
        return exeStack;
    }

    public IDictionary<String, IValue> getSymTable() {
        return symTable;
    }

    public IList<IValue> getOut() {
        return out;
    }

    public IDictionary<StringValue, BufferedReader> getFileTable() {
        return fileTable;
    }

    public IStmt getOriginalProgram() {
        return originalProgram;
    }

    public void setExeStack(IStack<IStmt> exeStack) {
        this.exeStack = exeStack;
    }

    public void setSymTable(IDictionary<String, IValue> symTable) {
        this.symTable = symTable;
    }

    public void setOut(IList<IValue> out) {
        this.out = out;
    }

    public void setFileTable(IDictionary<StringValue, BufferedReader> fileTable) {
        this.fileTable = fileTable;
    }

    public String toLogString() {
        StringBuilder log = new StringBuilder("ExeStack:\n");
        for (IStmt stmt : exeStack.toList()) {
            log.append(stmt).append('\n');
        }

        log.append("SymTable:\n");
        for (Map.Entry<String, IValue> entry : symTable.toMap().entrySet()) {
            log.append(entry.getKey()).append(" --> ").append(entry.getValue()).append('\n');
        }

        log.append("Out:\n");
        for (IValue value : out.toList()) {
            log.append(value).append('\n');
        }

        log.append("FileTable:\n");
        for (StringValue fileName : fileTable.toMap().keySet()) {
            log.append(fileName.val()).append('\n');
        }
        return log.toString();
    }

    @Override
    public String toString() {
        String fileNames = fileTable.toMap().keySet().stream()
                .map(StringValue::val)
                .collect(Collectors.joining("\n"));
        return "------------------------------------------------------\n" +
                "Execution Stack:\n" + exeStack.toString() + "\n" +
                "Symbol Table:\n" + symTable.toString() + "\n" +
                "Output Console:\n" + out.toString() + "\n" +
                "File Table:\n" + fileNames + "\n" +
                "------------------------------------------------------\n";
    }
}

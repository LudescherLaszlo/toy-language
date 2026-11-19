package interpreter;

import java.util.List;

import interpreter.controller.Controller;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import interpreter.view.ExitCommand;
import interpreter.view.RunExampleCommand;
import interpreter.view.TextMenu;

public class Main {
    public static void main(String[] args) {
        List<IStmt> programs = List.of(Examples.EX1, Examples.EX2, Examples.EX3, Examples.EX4, Examples.EX5);
        TextMenu menu = new TextMenu();
        menu.addCommand(new ExitCommand("0", "exit"));

        for (int i = 0; i < programs.size(); i++) {
            IStmt program = programs.get(i);
            PrgState state = PrgState.of(program);
            Repository repository = new Repository(state, "log" + (i + 1) + ".txt");
            Controller controller = new Controller(repository, true);
            menu.addCommand(new RunExampleCommand(String.valueOf(i + 1), program.toString(), controller));
        }

        menu.show();
    }
}

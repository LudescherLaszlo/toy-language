package interpreter.view;

import java.util.Scanner;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import interpreter.repository.Repository;

public class View {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n--- Toy Language Interpreter ---");
            System.out.println("1. Run Example 1 (v=2)");
            System.out.println("2. Run Example 2 (b=18)");
            System.out.println("3. Run Example 3 (if condition, v=2)");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            
            String option = scanner.nextLine();
            if (option.equals("0")) {
                System.out.println("Exiting...");
                break;
            }
            
            IStmt selectedProgram = null;
            switch (option) {
                case "1" -> selectedProgram = Examples.ex1;
                case "2" -> selectedProgram = Examples.ex2;
                case "3" -> selectedProgram = Examples.ex3;
                default -> {
                    System.out.println("Invalid option. Please try again.");
                    continue;
                }
            }
            
            try {
                PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), selectedProgram);
                
                IRepository repo = new Repository(state);
                Controller controller = new Controller(repo, true); 

                System.out.println("\n--- Execution Trace ---");
                controller.allStep();
                
            } catch (MyException e) {
                System.out.println("\n[INTERPRETER ERROR]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[SYSTEM ERROR]: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}
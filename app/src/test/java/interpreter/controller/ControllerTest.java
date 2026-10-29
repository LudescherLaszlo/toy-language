package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;

class ControllerTest {
    private PrgState run(IStmt program) throws MyException {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), program);
        new Controller(new Repository(state), false).allStep(); // display off[cite: 12]
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.ex1);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out[cite: 11]
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.ex2);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out[cite: 11]
        assertEquals(new IntValue(18), state.getOut().get(0)); //[cite: 12]
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.ex3);
        assertTrue(state.getExeStack().isEmpty());
        
        // Controller test: complete execution gives the expected Out[cite: 11]
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void oneStepOnEmptyStackThrowsException() throws ADTException {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), Examples.ex1);
        Controller controller = new Controller(new Repository(state), false);
        
        // Manually empty the stack to trigger the exception
        state.getExeStack().pop();
        
        // Controller test: oneStep on an empty stack throws[cite: 11]
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }
}
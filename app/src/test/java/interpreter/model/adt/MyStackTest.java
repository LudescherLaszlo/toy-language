package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.exceptions.ADTException;
import java.util.List;

class MyStackTest {
    private IStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
    }

    @Test
    void lifoOrderIsRespected() throws ADTException {
        stack.push(1);
        stack.push(2);
        
        // ADTs test: stack LIFO order
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void popOnEmptyStackThrowsException() {
        // ADTs test: pop on an empty stack throws
        var ex = assertThrows(ADTException.class, () -> stack.pop());
        assertEquals("pop from an empty stack", ex.getMessage());
    }

    @Test
    void toListIsTopFirstAndDoesNotPop() throws ADTException {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(List.of(3, 2, 1), stack.toList());
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }
}
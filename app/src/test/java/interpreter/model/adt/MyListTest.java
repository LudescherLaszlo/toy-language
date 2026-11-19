package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void toListKeepsInsertionOrder() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(List.of(1, 2, 3), list.toList());
    }
}

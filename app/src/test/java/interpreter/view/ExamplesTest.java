package interpreter.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExamplesTest {
    @Test
    void examplesHaveExpectedStringRepresentations() {
        assertEquals("(int v; (v = 2; print(v)))", Examples.EX1.toString());
        assertEquals("(int a; (int b; (a = (2 + (3 * 5)); (b = (a + 1); print(b)))))",
                Examples.EX2.toString());
        assertEquals("(bool a; (int v; (a = true; ((if (a) then (v = 2) else (v = 3)); print(v)))))",
                Examples.EX3.toString());
    }
}

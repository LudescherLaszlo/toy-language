package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.exceptions.ADTException;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putAndLookupWorkCorrectly() throws ADTException {
        dict.put("a", 10);
        assertTrue(dict.isDefined("a"));
        assertEquals(10, dict.lookup("a")); // ADTs test: dictionary put/lookup/update[cite: 11]
    }

    @Test
    void lookupOfMissingKeyThrowsException() {
        // ADTs test: lookup of a missing key throws[cite: 11]
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }
}
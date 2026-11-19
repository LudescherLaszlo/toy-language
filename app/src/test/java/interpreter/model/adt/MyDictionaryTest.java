package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.exceptions.ADTException;
import java.util.Map;

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
        assertEquals(10, dict.lookup("a")); // ADTs test: dictionary put/lookup/update
    }

    @Test
    void lookupOfMissingKeyThrowsException() {
        // ADTs test: lookup of a missing key throws
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }

    @Test
    void removeOfExistingKeyMakesItUndefined() throws ADTException {
        dict.put("a", 10);

        dict.remove("a");

        assertFalse(dict.isDefined("a"));
    }

    @Test
    void removeOfMissingKeyThrowsException() {
        var ex = assertThrows(ADTException.class, () -> dict.remove("missing"));
        assertEquals("Key 'missing' is not defined. Cannot remove.", ex.getMessage());
    }

    @Test
    void toMapContainsEntriesAndIsReadOnlyCopy() {
        dict.put("a", 10);
        dict.put("b", 20);

        Map<String, Integer> copy = dict.toMap();

        assertEquals(Map.of("a", 10, "b", 20), copy);
        assertThrows(UnsupportedOperationException.class, () -> copy.put("c", 30));

        dict.put("c", 30);
        assertFalse(copy.containsKey("c"));
    }
}
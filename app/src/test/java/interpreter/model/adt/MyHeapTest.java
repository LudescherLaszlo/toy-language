package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;

class MyHeapTest {
    private MyHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeap();
    }

    @Test
    void allocateReturnsIncreasingAddressesAndGetReturnsStoredValues() throws ADTException {
        IntValue first = new IntValue(10);
        IntValue second = new IntValue(20);
        IntValue third = new IntValue(30);

        assertEquals(1, heap.allocate(first));
        assertEquals(2, heap.allocate(second));
        assertEquals(3, heap.allocate(third));
        assertEquals(first, heap.get(1));
        assertEquals(second, heap.get(2));
        assertEquals(third, heap.get(3));
    }

    @Test
    void missingAddressesCannotBeReadOrUpdated() {
        assertThrows(ADTException.class, () -> heap.get(1));
        assertThrows(ADTException.class, () -> heap.update(1, new IntValue(10)));
    }

    @Test
    void updateAndIsDefinedWork() throws ADTException {
        int address = heap.allocate(new IntValue(10));

        assertTrue(heap.isDefined(address));
        heap.update(address, new IntValue(20));
        assertEquals(new IntValue(20), heap.get(address));
        assertFalse(heap.isDefined(address + 1));
    }

    @Test
    void getContentIsAReadOnlySnapshot() {
        heap.allocate(new IntValue(10));

        Map<Integer, IValue> copy = heap.getContent();

        assertThrows(UnsupportedOperationException.class, () -> copy.put(2, new IntValue(20)));
        heap.allocate(new IntValue(30));
        assertEquals(Map.of(1, new IntValue(10)), copy);
    }

    @Test
    void setContentReplacesEntriesWithoutReusingFreedAddresses() {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));

        heap.setContent(Map.of(8, new IntValue(80)));

        assertEquals(Map.of(8, new IntValue(80)), heap.getContent());
        assertEquals(3, heap.allocate(new IntValue(30)));
        assertEquals(Map.of(8, new IntValue(80), 3, new IntValue(30)), heap.getContent());
    }

    @Test
    void toStringPrintsHeapContent() {
        heap.allocate(new IntValue(10));

        assertEquals("{1=10}", heap.toString());
    }
}

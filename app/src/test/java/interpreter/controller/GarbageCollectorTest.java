package interpreter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;

class GarbageCollectorTest {
    @Test
    void safeCollectorKeepsHeapLocationsReachableThroughReferences() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(Set.of(
                new RefValue(3, new IntType()),
                new RefValue(2, new RefType(new IntType()))));
        Map<Integer, IValue> heap = Map.of(
                1, new IntValue(20),
                2, new RefValue(1, new IntType()),
                3, new IntValue(30),
                4, new IntValue(7));

        assertEquals(Set.of(3, 2), roots);
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, new IntType()),
                3, new IntValue(30)), GarbageCollector.safeGarbageCollector(roots, heap));
        assertEquals(Map.of(2, new RefValue(1, new IntType()), 3, new IntValue(30)),
                GarbageCollector.unsafeGarbageCollector(roots, heap));
    }

    @Test
    void safeCollectorFollowsOnlyTheReferencesReachableFromRoots() {
        Map<Integer, IValue> heap = Map.of(
                1, new IntValue(7),
                2, new IntValue(8),
                3, new RefValue(2, new IntType()),
                4, new RefValue(1, new IntType()),
                5, new RefValue(4, new RefType(new IntType())));
        Set<Integer> roots = Set.of(3, 0);

        assertEquals(Set.of(3, 2), GarbageCollector.reachableAddresses(roots, heap));
        assertEquals(Map.of(3, new RefValue(2, new IntType())),
                GarbageCollector.unsafeGarbageCollector(roots, heap));
        assertEquals(Map.of(2, new IntValue(8), 3, new RefValue(2, new IntType())),
                GarbageCollector.safeGarbageCollector(roots, heap));
    }

    @Test
    void safeCollectorKeepsAFullReferenceChainAndRemovesUnreachableLocations() {
        Map<Integer, IValue> heap = Map.of(
                1, new RefValue(2, new RefType(new IntType())),
                2, new RefValue(3, new IntType()),
                3, new IntValue(42),
                4, new IntValue(99));

        assertEquals(Set.of(1, 2, 3), GarbageCollector.reachableAddresses(Set.of(1), heap));
        assertEquals(Map.of(
                1, new RefValue(2, new RefType(new IntType())),
                2, new RefValue(3, new IntType()),
                3, new IntValue(42)), GarbageCollector.safeGarbageCollector(Set.of(1), heap));
    }

    @Test
    void emptyRootsAndMissingRootAddressesKeepNoHeapEntries() {
        Map<Integer, IValue> heap = Map.of(1, new IntValue(10));

        assertEquals(Set.of(), GarbageCollector.reachableAddresses(Set.of(), heap));
        assertEquals(Map.of(), GarbageCollector.safeGarbageCollector(Set.of(), heap));
        assertEquals(Set.of(), GarbageCollector.reachableAddresses(Set.of(0), heap));
        assertEquals(Map.of(), GarbageCollector.safeGarbageCollector(Set.of(0), heap));
    }

    @Test
    void reachableAddressesTerminatesForAReferenceCycle() {
        Map<Integer, IValue> heap = Map.of(
                1, new RefValue(2, new IntType()),
                2, new RefValue(1, new IntType()));

        assertEquals(Set.of(1, 2), GarbageCollector.reachableAddresses(Set.of(1), heap));
    }
}

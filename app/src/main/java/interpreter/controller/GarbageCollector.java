package interpreter.controller;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public final class GarbageCollector {
    private GarbageCollector() {
    }

    public static Set<Integer> getAddrFromSymTable(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(value -> value instanceof RefValue)
                .map(value -> ((RefValue) value).address())
                .collect(Collectors.toSet());
    }

    // This collector is intentionally unsafe: it ignores references stored in the heap.
    public static Map<Integer, IValue> unsafeGarbageCollector(Set<Integer> symTableAddr, Map<Integer, IValue> heap) {
        return heap.entrySet().stream()
                .filter(entry -> symTableAddr.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Set<Integer> reachableAddresses(Set<Integer> roots, Map<Integer, IValue> heap) {
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> workList = new ArrayDeque<>(roots);

        while (!workList.isEmpty()) {
            int address = workList.removeFirst();
            if (!heap.containsKey(address) || !visited.add(address)) {
                continue;
            }

            IValue value = heap.get(address);
            if (value instanceof RefValue refValue) {
                workList.addLast(refValue.address());
            }
        }

        return visited;
    }

    public static Map<Integer, IValue> safeGarbageCollector(Set<Integer> symTableAddr, Map<Integer, IValue> heap) {
        Set<Integer> reachable = reachableAddresses(symTableAddr, heap);
        return heap.entrySet().stream()
                .filter(entry -> reachable.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

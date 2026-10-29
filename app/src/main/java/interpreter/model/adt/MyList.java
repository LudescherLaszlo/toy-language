package interpreter.model.adt;

import java.util.ArrayList;
import java.util.List;
import interpreter.exceptions.ADTException;

public class MyList<T> implements IList<T> {
    private final List<T> list = new ArrayList<>();

    @Override
    public void add(T v) {
        list.add(v);
    }

    @Override
    public T get(int index) throws ADTException {
        if (index < 0 || index >= list.size()) {
            throw new ADTException("Index " + index + " is out of bounds.");
        }
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
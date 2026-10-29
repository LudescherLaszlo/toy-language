package interpreter.model.adt;

import interpreter.model.value.IValue;

public interface IDictionary<K,V> {

    boolean isDefined(String id);

    IValue lookup(String id);

}

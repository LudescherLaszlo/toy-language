package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;

class RefValueTest {
    @Test
    void equalValuesAreEqualAndHaveEqualHashCodes() {
        var first = new RefValue(3, new IntType());
        var second = new RefValue(3, new IntType());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void typeIsReferenceToItsLocationType() {
        var value = new RefValue(3, new IntType());

        assertEquals(new RefType(new IntType()), value.getType());
    }

    @Test
    void valuesHaveExpectedStringRepresentation() {
        assertEquals("(3, int)", new RefValue(3, new IntType()).toString());
        assertEquals("(3, Ref(int))", new RefValue(3, new RefType(new IntType())).toString());
    }
}

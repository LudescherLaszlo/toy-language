package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import interpreter.model.value.StringValue;

class StringTypeTest {
    @Test
    void defaultValueIsAnEmptyStringValue() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
    }

    @Test
    void equalStringValuesHaveEqualHashCodes() {
        var first = new StringValue("same string");
        var second = new StringValue("same string");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}

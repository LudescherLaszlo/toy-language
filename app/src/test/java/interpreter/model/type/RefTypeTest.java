package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.value.RefValue;

class RefTypeTest {
    @Test
    void declarationGetsNullReferenceDefaultValue() throws MyException {
        var state = PrgState.of(new NopStmt());
        var inner = new IntType();

        new VarDeclStmt("v", new RefType(inner)).execute(state);

        assertEquals(new RefValue(0, inner), state.getSymTable().lookup("v"));
    }

    @Test
    void nestedReferenceDeclarationGetsNullReferenceToReferenceDefaultValue() throws MyException {
        var state = PrgState.of(new NopStmt());
        var inner = new RefType(new IntType());

        new VarDeclStmt("v", new RefType(inner)).execute(state);

        assertEquals(new RefValue(0, inner), state.getSymTable().lookup("v"));
    }

    @Test
    void equalNestedTypesAreEqualAndHaveEqualHashCodes() {
        var first = new RefType(new RefType(new IntType()));
        var second = new RefType(new RefType(new IntType()));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentTypesAndNestingAreNotEqual() {
        assertNotEquals(new RefType(new IntType()), new RefType(new RefType(new IntType())));
        assertNotEquals(new RefType(new IntType()), new RefType(new BoolType()));
    }

    @Test
    void typesHaveExpectedStringRepresentations() {
        assertEquals("Ref(int)", new RefType(new IntType()).toString());
        assertEquals("Ref(Ref(int))", new RefType(new RefType(new IntType())).toString());
    }
}

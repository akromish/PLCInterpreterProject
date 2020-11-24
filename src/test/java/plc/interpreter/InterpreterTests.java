package plc.interpreter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

final class InterpreterTests {

    @Test
    void testTerm() {
        test(new Ast.Term("print", Arrays.asList()), Interpreter.VOID, Collections.emptyMap());
    }

    @Test
    void testIdentifier() {
        test(new Ast.Identifier("num"), 10, Collections.singletonMap("num", 10));
    }

    @Test
    void testNumber() {
        test(new Ast.NumberLiteral(BigDecimal.ONE), BigDecimal.ONE, Collections.emptyMap());
    }

    @Test
    void testString() {
        test(new Ast.StringLiteral("string"), "string", Collections.emptyMap());
    }

    @ParameterizedTest
    @MethodSource
    void testAddition(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testAddition() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("+", Arrays.asList()), BigDecimal.valueOf(0)),
                Arguments.of("Multiple Arguments", new Ast.Term("+", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), BigDecimal.valueOf(6)),
                Arguments.of("Different type Arguments", new Ast.Term("+", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.StringLiteral("howdy"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), null),
                Arguments.of("Single Argument", new Ast.Term("+", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(7))
                )), BigDecimal.valueOf(7))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testSubtraction(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testSubtraction() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("-", Arrays.asList()), null),
                Arguments.of("Single Argument", new Ast.Term("-", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE)
                )), BigDecimal.valueOf(-1)),
                Arguments.of("Multiple Arguments", new Ast.Term("-", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), BigDecimal.valueOf(-4)),
                Arguments.of("Different type Arguments", new Ast.Term("-", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.StringLiteral("howdy"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testMultiplication(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testMultiplication() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("*", Arrays.asList()), BigDecimal.valueOf(1)),
                Arguments.of("Multiple Arguments", new Ast.Term("*", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), BigDecimal.valueOf(6)),
                Arguments.of("Different type Arguments", new Ast.Term("*", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.StringLiteral("howdy"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), null),
                Arguments.of("Single Argument", new Ast.Term("*", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(4))
                )), BigDecimal.valueOf(4))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDivision(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testDivision() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("/", Arrays.asList()), null),
                Arguments.of("Multiple Arguments", new Ast.Term("/", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(100)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(5)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2))
                )), BigDecimal.valueOf(10)),
                Arguments.of("One Argument", new Ast.Term("/", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(0.5))
                )), BigDecimal.valueOf(2)),
                Arguments.of("Different type Arguments", new Ast.Term("/", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.StringLiteral("howdy"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), null),
                Arguments.of("Multiple Arguments", new Ast.Term("/", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(9)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2))
                )), BigDecimal.valueOf(4)),
                Arguments.of("Multiple Arguments", new Ast.Term("/", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(11)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2))
                )), BigDecimal.valueOf(6))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testEquals(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testEquals() {
        return Stream.of(
//                Arguments.of("Variable Arguments", new Ast.Term("equals?", Arrays.asList(
//                        new Ast.StringLiteral("x"),
//                        new Ast.StringLiteral("y")
//                )), false),
//                Arguments.of("Number Arguments", new Ast.Term("equals?", Arrays.asList(
//                        new Ast.NumberLiteral(BigDecimal.valueOf(10)),
//                        new Ast.NumberLiteral(BigDecimal.valueOf(10))
//                )), true),
                Arguments.of("Different type Arguments", new Ast.Term("equals?", Arrays.asList(
                        new Ast.StringLiteral("true"),
                        new Ast.StringLiteral("false"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                        )), false),
                Arguments.of("Zero Arguments", new Ast.Term("equals?", Arrays.asList()), null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testNot(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testNot() {
        return Stream.of(
                Arguments.of("True Arguments", new Ast.Term("not", Arrays.asList(
                        new Ast.StringLiteral("true")
                )), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testAnd(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testAnd() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("and", Arrays.asList()), null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testRange(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testRange() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("range", Arrays.asList()), null),
                Arguments.of("Two Arguments Equal", new Ast.Term("range", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(1))
                )), BigDecimal.valueOf(0)),
                Arguments.of("Multiple Arguments", new Ast.Term("range", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(20))
                )), BigDecimal.valueOf(1))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testList(String test, Ast ast, BigDecimal expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testList() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("list", Arrays.asList()), BigDecimal.valueOf(1)),
                Arguments.of("Multiple Arguments", new Ast.Term("list", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.ONE),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), BigDecimal.valueOf(1)),
                Arguments.of("strings Arguments", new Ast.Term("list", Arrays.asList(
                        new Ast.StringLiteral("\"a\""),
                        new Ast.StringLiteral("\"b\""),
                        new Ast.StringLiteral("\"c\"")
                )), BigDecimal.valueOf(1))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testLessThan(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testLessThan() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("<", Arrays.asList()), true),
                Arguments.of("String Arguments", new Ast.Term("<", Arrays.asList(
                        new Ast.StringLiteral("a"),
                        new Ast.StringLiteral("b"),
                        new Ast.StringLiteral("c")
                )), true),
                Arguments.of("Number Arguments", new Ast.Term("<", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), true),
                Arguments.of("Not strictly increasing", new Ast.Term("<", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(4)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2))
                )), false),
                Arguments.of("Incomparable Arguments", new Ast.Term("<", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.StringLiteral("x"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testLessThanOrEqual(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testLessThanOrEqual() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term("<=", Arrays.asList()), true),
                Arguments.of("String Arguments", new Ast.Term("<=", Arrays.asList(
                        new Ast.StringLiteral("a"),
                        new Ast.StringLiteral("a"),
                        new Ast.StringLiteral("b"),
                        new Ast.StringLiteral("c")
                )), true),
                Arguments.of("Number Arguments", new Ast.Term("<=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), true),
                Arguments.of("Not strictly increasing", new Ast.Term("<=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(4)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(4)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2))
                )), false),
                Arguments.of("Incomparable Arguments", new Ast.Term("<=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.StringLiteral("x"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGreaterThan(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testGreaterThan() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term(">", Arrays.asList()), true),
                Arguments.of("String Arguments", new Ast.Term(">", Arrays.asList(
                        new Ast.StringLiteral("c"),
                        new Ast.StringLiteral("b"),
                        new Ast.StringLiteral("a")
                )), true),
                Arguments.of("Number Arguments", new Ast.Term(">", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(1))
                )), true),
                Arguments.of("Not strictly increasing", new Ast.Term(">", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(4))
                )), false),
                Arguments.of("Incomparable Arguments", new Ast.Term(">", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.StringLiteral("x"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGreaterThanOrEqual(String test, Ast ast, boolean expected) {
        test(ast, expected, Collections.emptyMap());
    }

    private static Stream<Arguments> testGreaterThanOrEqual() {
        return Stream.of(
                Arguments.of("Zero Arguments", new Ast.Term(">=", Arrays.asList()), true),
                Arguments.of("String Arguments", new Ast.Term(">=", Arrays.asList(
                        new Ast.StringLiteral("c"),
                        new Ast.StringLiteral("c"),
                        new Ast.StringLiteral("b"),
                        new Ast.StringLiteral("a")
                )), true),
                Arguments.of("Number Arguments", new Ast.Term(">=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(1))
                )), true),
                Arguments.of("Not strictly increasing", new Ast.Term(">=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(2)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3)),
                        new Ast.NumberLiteral(BigDecimal.valueOf(4))
                )), false),
                Arguments.of("Incomparable Arguments", new Ast.Term(">=", Arrays.asList(
                        new Ast.NumberLiteral(BigDecimal.valueOf(1)),
                        new Ast.StringLiteral("x"),
                        new Ast.NumberLiteral(BigDecimal.valueOf(3))
                )), false)
        );
    }

    private static void test(Ast ast, Object expected, Map<String, Object> map) {
        Scope scope = new Scope(null);
        map.forEach(scope::define);
        Interpreter interpreter = new Interpreter(new PrintWriter(System.out), scope);
        if (expected != null) {
            Assertions.assertEquals(expected, interpreter.eval(ast));
        } else {
            Assertions.assertThrows(EvalException.class, () -> interpreter.eval(ast));
        }
    }

}

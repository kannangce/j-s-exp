package in.kannangce.j_s_exp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OperatorsTest {

    Evaluator evaluatorInstance = new Evaluator(null,
            Map.of("always", Operators.FN_ALWAYS,
                    "true?", Operators.FN_IS_TRUE,
                    "identity", Operators.FN_IDENTITY,
                    "matches", Operators.FN_IS_MATCHES),
            Map.of("if-else", Operators.MC_IF_ELSE));

    @Test
    public  void testAlways() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"always\", \"str param\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Always expected to return true always");
    }

    @Test
    public  void testAlwaysNoParam() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"always\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Always expected to return true always");
    }


    @Test
    public  void testAlwaysNullParam() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"always\", null]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Always expected to return true always");
    }


    @Test
    public  void testAlwaysFalseParam() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"always\", false]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Always expected to return true always");
    }


    @Test
    public  void testAlwaysNestedFunctionCall() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"always\", [\"identity\", false]]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Always expected to return true always");
    }

    @Test
    public  void testIsTrueNonTrueString() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"true?\", \"str param\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), false,
                () -> "true? expected to return false when the string is not boolean equivalent of true.");
    }

    @Test
    public  void testIsTrueTrueString() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"true?\", \"trUe\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Boolean equivalent of true is expected to return true.");
    }

    @Test
    public  void testIsTrueTrueBoolean() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"true?\", true]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Boolean equivalent of true is expected to return true.");
    }


    @Test
    public  void testIsTrueFalseBoolean() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"true?\", false]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), false,
                () -> "Boolean equivalent of false is expected to return false.");
    }

    @Test
    public  void testIdentityString() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"identity\", \"Some String\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "Some String",
                () -> "identity is expected to return the string passed");
    }

    @Test
    public  void testIdentityFirstParam() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"identity\", \"Some String\", \"second param\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "Some String",
                () -> "identity is expected to return the string passed");
    }

    @Test
    public  void testIdentityBoolean() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"identity\", true]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "identity is expected to return the boolean passed");
    }


    @Test
    public  void testIdentityFloat() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"identity\", 17.29]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), 17.29,
                () -> "identity is expected to return the boolean passed");
    }

    @Test
    public  void testMatchesString() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"matches\", \"abc\", \"abc\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Matches expected to match same string");
    }


    @Test
    public  void testMatchesRegex() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"matches\", \"abc\", \"a.*c\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), true,
                () -> "Matches expected to match regex");
    }

    @Test
    public  void testMatchesNoPattern() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"matches\", \"abc\"]");

        assertThrows(IllegalArgumentException.class, ()->evaluatorInstance.evaluate(parsedExpression),
                () -> "Matches expected to fail when insufficient params passed");
    }

    @Test
    public  void testIfElseBoolean() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\", true, \"true-path\", \"false-path\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "true-path",
                () -> "Expected to return true path");
    }

    @Test
    public  void testIfElseTrueEquivalentString() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\", \"truE\", \"true-path\", \"false-path\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "true-path",
                () -> "Expected to return true path");
    }

    @Test
    public  void testIfElseNoElsePath() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\", \"truE\", \"true-path\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "true-path",
                () -> "Expected to return true path.");
    }

    @Test
    public  void testIfElseConditionFromFunction() throws Exception {
        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\", [\"identity\", \"truE\"], \"true-path\"]");

        assertEquals(evaluatorInstance.evaluate(parsedExpression), "true-path",
                () -> "Expected to return true path.");
    }

    @Test
    public  void testIfElseConditionOnlyTruePathToBeEvaluated() throws Exception {
        List<String> ctxt = new ArrayList<>();

        Evaluator.CustomFunction updateContext = (context, args) -> { ((List)context).add(args[0]); return args[0]; };

        Evaluator evaluatorInstance = new Evaluator(ctxt,
                Map.of("always", Operators.FN_ALWAYS,
                        "true?", Operators.FN_IS_TRUE,
                        "identity", Operators.FN_IDENTITY,
                        "matches", Operators.FN_IS_MATCHES,
                        "updateContext", updateContext),
                Map.of("if-else", Operators.MC_IF_ELSE));


        assertEquals(ctxt.size(), 0 , "Context is expected to be empty before evaulation");

        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\"," +
                                                                            "[\"identity\", \"truE\"], " +
                                                                            "[\"updateContext\" , \"true-path\"], " +
                                                                            "[\"updateContext\" , \"false-path\"]]");

        String expectedResult = "true-path";
        assertEquals(evaluatorInstance.evaluate(parsedExpression), expectedResult,
                () -> "Expected to return "+expectedResult);

        assertEquals(ctxt.size(), 1,
                () -> "Expected to contain values only from the evaulated path");

        assertEquals(ctxt.contains(expectedResult), true,
                () -> "Expected to contain values only from the evaulated path");


        assertEquals(ctxt.contains("false-path"), false,
                () -> "Expected to contain values only from the evaulated path");
    }

    @Test
    public  void testIfElseConditionOnlyFalsePathToBeEvaluated() throws Exception {
        List<String> ctxt = new ArrayList<>();

        Evaluator.CustomFunction updateContext = (context, args) -> { ((List)context).add(args[0]); return args[0]; };

        Evaluator evaluatorInstance = new Evaluator(ctxt,
                Map.of("always", Operators.FN_ALWAYS,
                        "true?", Operators.FN_IS_TRUE,
                        "identity", Operators.FN_IDENTITY,
                        "matches", Operators.FN_IS_MATCHES,
                        "updateContext", updateContext),
                Map.of("if-else", Operators.MC_IF_ELSE));


        assertEquals(ctxt.size(), 0 , "Context is expected to be empty before evaulation");

        List<Object> parsedExpression = EvaluatorTest.parseExpression("[\"if-else\"," +
                "[\"identity\", \"false\"], " +
                "[\"updateContext\" , \"true-path\"], " +
                "[\"updateContext\" , \"false-path\"]]");

        String expectedResult = "false-path";
        assertEquals(evaluatorInstance.evaluate(parsedExpression), expectedResult,
                () -> "Expected to return "+expectedResult);

        assertEquals(ctxt.size(), 1,
                () -> "Expected to contain values only from the evaulated path");

        assertEquals(ctxt.contains(expectedResult), true,
                () -> "Expected to contain values only from the evaulated path");


        assertEquals(ctxt.contains("true-path"), false,
                () -> "Expected to contain values only from the evaulated path");
    }
}

package in.kannangce.j_s_exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.kannangce.exception.UnsupportedOperatorException;

public class EvaluatorTest {

	private Object ctx = null;

	private static ObjectMapper mapper = new ObjectMapper();

	Object a = new Object[] { "", 2 };

	@Test
	public void testAllowedFunctions() throws Exception {

		Evaluator evaluatorInstance = new Evaluator(null, Map.of("identity", Operators.FN_IDENTITY), null);

		List<Object> identityOperator = parseExpression("[\"identity\", \"result\"]");

		assertEquals(evaluatorInstance.evaluate(identityOperator), "result",
				() -> "Identity function to return the first parameter as is");
	}

	@Test
	public void testNestedFunctions() throws Exception {
		Evaluator evaluatorInstance = new Evaluator(null,
				Map.of("identity", Operators.FN_IDENTITY, "matches", Operators.FN_IS_MATCHES), null);

		List<Object> nestedOperators = parseExpression("[\"matches\" , [\"identity\", \"result\"], \"^r.*t$\"]");

		assertEquals(evaluatorInstance.evaluate(nestedOperators), true,
				() -> "matches wrapping identity to return true");
	}

	@Test
	public void testUnallowedFunctions() throws Exception {
		Evaluator evaluatorInstance = new Evaluator(null, Map.of("identity", Operators.FN_IDENTITY), null);

		List<Object> nestedOperators = parseExpression("[\"matches\" , [\"identity\", \"result\"], \"^r.*t$\"]");

		assertThrows(UnsupportedOperatorException.class, () -> evaluatorInstance.evaluate(nestedOperators),
				() -> "Expression expected to throw exception when not added to allowed functions");
	}

	@Test
	public void testMacro() throws Exception {
		Evaluator evaluatorInstance = new Evaluator(null,
				Map.of("true?", Operators.FN_IS_TRUE, "identity", Operators.FN_IDENTITY), // If uses those 2 operators.
																							// It's caller
																							// responsibility to ensure
																							// the dependencies
				Map.of("if-else", Operators.MC_IF_ELSE));

		List<Object> expressionWithMacro = parseExpression("[\"if-else\" , true , \"true-path\", \"false-path\"]");

		assertEquals(evaluatorInstance.evaluate(expressionWithMacro), "true-path",
				() -> "if with condition true expected to returh true-path");
	}

	private static List<Object> parseExpression(String expression) throws Exception {
		return mapper.readValue(expression, new TypeReference<List<Object>>() {
		});
	}

}

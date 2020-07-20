package in.kannangce.j_s_exp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import in.kannangce.exception.UnsupportedOperatorException;

public class EvaluatorTest {

	private Evaluator<Object> evaluatorInstance;

	private Object ctx = null;
	
	Object a = new Object[] {"", 2};

	@Before
	public void init() throws Exception {
		Evaluator.CustomFunction<Object> printFunction = (Object ctx,
				Object[] params) -> {
			System.out.println(params[0]);
			return null;
		};
		evaluatorInstance = new Evaluator<>(Map.of("println", printFunction),
				null);
	}

	@Test
	public void testIf() throws Exception {
		evaluatorInstance.evaluate(null,
				Arrays.asList("if", true,
						Arrays.asList("println", "true-case"),
						Arrays.asList("println", "false-case")));
	}

	@Test
	public void testMatchesTrue() throws Exception {
		Object matchesResult = evaluatorInstance.evaluate(ctx,
				Arrays.asList("matches", "someValue", "s.*e"));
		assertTrue(Boolean.valueOf(String.valueOf(matchesResult)));
	}

	@Test
	public void testMatchesFalse() throws Exception {
		Object matchesResult = evaluatorInstance.evaluate(ctx,
				Arrays.asList("matches", "someValue", "sameValue"));
		assertFalse(Boolean.valueOf(String.valueOf(matchesResult)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMatchesNotEnoughParams() throws Exception {
		evaluatorInstance.evaluate(ctx, Arrays.asList("matches", "someValue"));
	}

	@Test
	public void testAlways() throws Exception {
		Object evalResult = evaluatorInstance.evaluate(ctx,
				Arrays.asList("always"));
		assertTrue(Boolean.valueOf(String.valueOf(evalResult)));
	}

	@Test(expected = UnsupportedOperatorException.class)
	public void testNotAllowedOperators() throws Exception {
		evaluatorInstance.evaluate(ctx, Arrays.asList("invalid-op"));
	}

	@Test(expected = UnsupportedOperatorException.class)
	public void testNotAllowedOperatorsNested() throws Exception {
		evaluatorInstance.evaluate(ctx,
				Arrays.asList("matches", "someval", Arrays.asList("invalid-op")));
	}
}

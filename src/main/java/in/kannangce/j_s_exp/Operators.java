package in.kannangce.j_s_exp;

import static in.kannangce.j_s_exp.utils.Utils.emptyForNull;
import static in.kannangce.j_s_exp.utils.Utils.nullIfUnavailable;

import java.util.Arrays;

import in.kannangce.j_s_exp.Evaluator.CustomFunction;
import in.kannangce.j_s_exp.Evaluator.CustomMacro;

/**
 * Meant to contain the standard operators that can be typically used.
 * 
 * @author kannanr
 *
 */
public class Operators {

	/**
	 * Operator that always returns true.
	 */
	public static CustomFunction FN_ALWAYS = (Object contenxt, Object... objs) -> {
		return true;
	};

	/**
	 * Operator returns the first argument of the function as is.
	 */
	public static CustomFunction FN_IDENTITY = (Object contenxt, Object... objs) -> {
		if (objs == null || objs.length == 0) {
			return null;
		}
		return objs[0];
	};

	/**
	 * Operator that checks if the first parameter is true as defined by
	 * {@link Boolean#valueOf(String)}. The remaining parameters will be ignored.
	 */
	public static CustomFunction FN_IS_TRUE = (Object contenxt, Object... objs) -> {
		if (objs == null || objs.length == 0 || objs[0] == null) {
			return false;
		}
		return Boolean.valueOf(objs[0].toString());
	};

	/**
	 * Operator that checks if the first parameter matches with the pattern provided
	 * in the second argument.
	 */
	public static CustomFunction FN_IS_MATCHES = (Object contenxt, Object... objs) -> {

		if (objs.length != 2) {
			throw new IllegalArgumentException("Not sufficient arguments for matches");
		}

		String val = emptyForNull(String.valueOf(objs[0]));
		String pattern = emptyForNull(String.valueOf(objs[1]));
		return val.toLowerCase().matches(pattern.toLowerCase());
	};

	/**
	 * if-else expression, works as below,
	 * <ol>
	 * <li>Evaluates first parameter.</li>
	 * <li>If the above evaluation is true, return the second parameter for
	 * evaluation</li>
	 * <li>If the above evaluation is false, return the third parameter for
	 * evaluation</li>
	 * </ol>
	 */
	public static CustomMacro MC_IF_ELSE = (Evaluator evaluator, Object... objs) -> {

		Object condition = nullIfUnavailable(objs, 0);

		Object trueExpr = nullIfUnavailable(objs, 1);

		Object falseExpr = nullIfUnavailable(objs, 2);

		// Evaluates the condition
		if (Boolean.valueOf(evaluator.evaluate(Arrays.asList("true?", condition)).toString())) {
			// Returns the expression of true path.
			return Arrays.asList("identity", trueExpr);
		} else {
			// Returns the expression of false path.
			return Arrays.asList("identity", falseExpr);
		}
	};
}

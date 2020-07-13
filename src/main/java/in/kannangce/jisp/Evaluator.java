package in.kannangce.jisp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.kannangce.exception.UnsupportedOperatorException;
import static in.kannangce.jisp.utils.Utils.*;

/**
 * Evaluates conditionals and return the result.
 * 
 * @author kannan.r
 *
 */
public class Evaluator<T> {

	/**
	 * Operator that always returns true.
	 */
	private CustomFunction<T> always = (T contenxt,
			Object... objs) -> {
		return true;
	};

	/**
	 * Operator returns the first argument of the function as is.
	 */
	private CustomFunction<T> identity = (T contenxt,
			Object... objs) -> {
		if (objs == null || objs.length == 0) {
			return null;
		}
		return objs[0];
	};

	/**
	 * Operator that checks if the first parameter is true as defined by
	 * {@link Boolean#valueOf(String)}. The remaining parameters will be
	 * ignored.
	 */
	private CustomFunction<T> isTrue = (T contenxt,
			Object... objs) -> {
		if (objs == null || objs.length == 0 || objs[0] == null) {
			return false;
		}
		return Boolean.valueOf(objs[0].toString());
	};

	/**
	 * Operator that checks if the first parameter matches with the pattern
	 * provided in the second argument.
	 */
	private CustomFunction<T> matches = (T contenxt,
			Object... objs) -> {

		if (objs.length != 2) {
			throw new IllegalArgumentException(
					"Not sufficient arguments for matches");
		}

		String val = emptyForNull(String.valueOf(objs[0]));
		String pattern = emptyForNull(String.valueOf(objs[1]));
		return val.toLowerCase()
				.matches(pattern.toLowerCase());
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
	private CustomMacro<T> if_else = (T context, Object... objs) -> {

		Object condition = nullIfUnavailable(objs, 0);

		Object trueExpr = nullIfUnavailable(objs, 1);

		Object falseExpr = nullIfUnavailable(objs, 2);

		// Evaluates the condition
		if (Boolean.valueOf(evaluate(context, Arrays.asList("true?", condition))
				.toString())) {
			// Returns the expression of true path.
			return Arrays.asList("identity", trueExpr);
		} else {
			// Returns the expression of false path.
			return Arrays.asList("identity", falseExpr);
		}
	};

	private final Map<String, CustomFunction<T>> allowedFns = new HashMap<>(Map
			.of("matches", matches, "always", always, "true?", isTrue,
					"identity", identity));

	private final Map<String, CustomMacro<T>> allowedMacros = new HashMap<>(Map
			.of("if", if_else));

	/**
	 * Creates an evaluator instance
	 * 
	 * @param allowedFns
	 *            The list of allowed operators, where they key is operator and
	 *            value is respective {@link CustomFunction} implementation.
	 */
	public Evaluator(Map<String, CustomFunction<T>> allowedFns,
			Map<String, CustomMacro<T>> allowedMacros) {
		if (allowedFns != null) {
			this.allowedFns.putAll(allowedFns);
		}
		if (allowedMacros != null) {
			this.allowedMacros.putAll(allowedMacros);
		}
	}

	/**
	 * Custom function represents an operator(function) that will be evaluated
	 * by the evaluator.
	 * 
	 */
	public static interface CustomFunction<T> {
		Object apply(T contenxt, Object... objects);
	}

	/**
	 * Represents an operator(macro) that will be expanded and then evaluated by
	 * the evaluator.
	 * 
	 */
	public static interface CustomMacro<T> {
		List<Object> apply(T contenxt, Object... objects);
	}

	/**
	 * Evaluates the given s-expression in the form of List. The operators in
	 * the s-expressions must be one of those in the
	 * {@link Evaluator#allowedFns}
	 * 
	 * @param context
	 *            The context variable to be made available for all the
	 *            operators.
	 * @param tree
	 *            The s-expression to be evaluated.
	 * @return The return value of the evaluated expression.
	 * @throws UnsupportedOperatorException
	 *             If the given expression doesn't adhere to the form or uses
	 *             the function that is not allowed
	 */
	public Object evaluate(T context, List<Object> tree) {
		String operator = (String) tree.get(0);

		if (!allowedOperator(operator)) {
			throw new UnsupportedOperatorException(String.format(
					"The operator %s is not allowed to evaluate", operator));
		}

		if (isMacro(operator)) {
			// For macro operator, expand the macro operator.
			// And evaluate the return list of the macro recursively.
			return evaluate(context,
					allowedMacros.get(operator).apply(context,
							// We'll not be evaluating the parameters for a
							// macro
							getParams(context, tree, false)));
		}

		// Apply the function call
		return allowedFns.get(operator)
				// The params of functions should be evaluated recursively
				.apply(context, getParams(context, tree, true));
	}

	/**
	 * Gets the parameters of a given expression.
	 * 
	 * @param context
	 *            The context of the s-expression.
	 * @param tree
	 *            The s-expression, whose parameters to be returned.
	 * @param evaluate
	 *            Flag that says whether the nested expresssions to be
	 *            evaluated.
	 * @return The parameters in the expressions of the given tree.
	 */
	private Object[] getParams(T context, List<Object> tree, boolean evaluate) {
		Object[] params = new Object[tree.size() - 1];
		for (int i = tree.size() - 1; i > 0; i--) {
			Object currParam = tree.get(i);
			if (evaluate && currParam instanceof List) {
				// If the currParam is list, evaluate is recursively
				params[i - 1] = evaluate(context, (List<Object>) currParam);
			} else {
				// If not pass the param as is
				params[i - 1] = currParam;
			}
		}
		return params;
	}

	/**
	 * Tells if the given operator is allowed operator as per the evaluator.
	 * 
	 * @param operator
	 *            The operator to be tested for.
	 * @return true if the given operator is a valid function or macro. false
	 *         otherwise.
	 */
	private boolean allowedOperator(String operator) {
		return allowedMacros.containsKey(operator)
				|| allowedFns.containsKey(operator);
	}

	/**
	 * Tells if the given operator is allowed macro.
	 * 
	 * @param operator
	 *            The operator to be tested for.
	 * @return true if the given operator is a valid macro. false otherwise.
	 */
	private boolean isMacro(String operator) {
		return allowedMacros.containsKey(operator);
	}

	/**
	 * Gets the set of the operators/functions allowed by the evaluator.
	 * 
	 * @return The set of operators allowed
	 */
	public Set<String> allowedOperators() {
		return allowedFns.keySet();
	}
}

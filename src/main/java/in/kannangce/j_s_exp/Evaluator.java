package in.kannangce.j_s_exp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.kannangce.exception.UnsupportedOperatorException;

/**
 * Evaluates conditionals and return the result.
 *
 * @author kannan.r
 */
public class Evaluator {

    private Object context;

    private final Map<String, CustomFunction> allowedFns = new HashMap<>();

    private final Map<String, CustomMacro> allowedMacros = new HashMap<>();

    /**
     * Creates an evaluator instance
     *
     * @param context       The context in which the evaluation will be done.
     * @param allowedFns    The list of allowed operators, where they key is operator
     *                      and value is respective {@link CustomFunction}
     *                      implementation.
     * @param allowedMacros The list of allowed operators, where they key is operator
     *                      and value is respective {@link CustomMacro}
     *                      implementation.
     */
    public Evaluator(Object context, Map<String, CustomFunction> allowedFns, Map<String, CustomMacro> allowedMacros) {
        this.context = context;
        if (allowedFns != null) {
            this.allowedFns.putAll(allowedFns);
        }
        if (allowedMacros != null) {
            this.allowedMacros.putAll(allowedMacros);
        }
    }

    /**
     * Custom function represents an operator(function) that will be evaluated by
     * the evaluator.
     */
    public static interface CustomFunction {
        Object apply(Object context, Object... objects);
    }

    /**
     * Represents an operator(macro) that will be expanded and then evaluated by the
     * evaluator.
     */
    public static interface CustomMacro {
        List<Object> apply(Evaluator contenxt, Object... objects);
    }

    /**
     * Evaluates the given s-expression in the form of List. The operators in the
     * s-expressions must be one of those in the {@link Evaluator#allowedFns}

     * @param tree    The s-expression to be evaluated.
     * @return The return value of the evaluated expression.
     * @throws UnsupportedOperatorException If the given expression doesn't adhere
     *                                      to the form or uses the function that is
     *                                      not allowed
     */
    public Object evaluate(List<Object> tree) {
        String operator = (String) tree.get(0);

        if (!allowedOperator(operator)) {
            throw new UnsupportedOperatorException(
                    String.format("The operator %s is not allowed to evaluate", operator));
        }

        if (isMacro(operator)) {
            // For macro operator, expand the macro operator.
            // And evaluate the return list of the macro recursively.
            return evaluate(allowedMacros.get(operator).apply(this,
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
     * @param context  The context of the s-expression.
     * @param tree     The s-expression, whose parameters to be returned.
     * @param evaluate Flag that says whether the nested expresssions to be
     *                 evaluated.
     * @return The parameters in the expressions of the given tree.
     */
    private Object[] getParams(Object context, List<Object> tree, boolean evaluate) {
        Object[] params = new Object[tree.size() - 1];
        for (int i = tree.size() - 1; i > 0; i--) {
            Object currParam = tree.get(i);
            if (evaluate && currParam instanceof List) {
                // If the currParam is list, evaluate is recursively
                params[i - 1] = evaluate((List<Object>) currParam);
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
     * @param operator The operator to be tested for.
     * @return true if the given operator is a valid function or macro. false
     * otherwise.
     */
    private boolean allowedOperator(String operator) {
        return allowedMacros.containsKey(operator) || allowedFns.containsKey(operator);
    }

    /**
     * Tells if the given operator is allowed macro.
     *
     * @param operator The operator to be tested for.
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

package in.kannangce.exception;

/**
 * Represents the state of invalid operator to be evaluated.
 * 
 * @author kannan.r
 *
 */
public class UnsupportedOperatorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 514642987114481643L;

	public UnsupportedOperatorException(String string) {
		super(string);
	}

}

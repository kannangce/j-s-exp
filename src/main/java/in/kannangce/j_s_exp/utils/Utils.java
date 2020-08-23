package in.kannangce.j_s_exp.utils;

/**
 * Class containing the utilities
 *
 * @author kannan.r
 */
public class Utils {

    /**
     * Checks if the given string is null or the trimmed version is empty
     *
     * @param createdByValue String to test
     * @return true if the string is null/empty/just whitespace
     */
    public static boolean isEmpty(Object createdByValue) {
        return createdByValue == null || createdByValue.toString()
                .strip()
                .equals("");
    }

    /**
     * Returns empty string if the given string is null
     *
     * @param str String that need to checked and value returned.
     * @return empty string if the given string is null, the given string
     * otherwise.
     */
    public static String emptyForNull(String str) {
        return str == null ? "" : str;
    }

    /**
     * Gets the element from the given array at the given index, if available.
     *
     * @param <T>        The type of array.
     * @param arr        The array from which the element to be fetched.
     * @param indexToGet The index from which the element to be fetched.
     * @return The element at the given index, if the index is valid. null
     * otherwise.
     */
    public static <T> T nullIfUnavailable(T[] arr, int indexToGet) {
        if (arr != null && arr.length > indexToGet) {
            return arr[indexToGet];
        }
        return null;
    }
}

package org.oceanops.api.id;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;


/**
 * Utility class
 */
public class Utils {    
    /**
     * Check if string represents an integer
     * @param str the string to test
     * @return true if is an integer, false otherwise
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method returning the request Cayenne context. 
     * @return the context
     */
	public static ObjectContext getCayenneContext() {
		return BaseContext.getThreadObjectContext();
	}
}
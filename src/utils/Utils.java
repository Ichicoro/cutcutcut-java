package utils;

import actors.*;

/**
 * A bunch of utility functions
 */
public class Utils {
	
	/**
	 * A function that returns true if a {@link String} is a number
	 * @param str The input {@link String}
	 * @return {@code true} if the input is a number, {@code false} otherwise
	 */
	public static boolean isNumeric(final String str) {
        // null or empty
        if (str == null || str.length() == 0)
            return false;
        
        for (char c : str.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return true;
    }
	
	/**
	 * A function that returns a UI-usable {@link String} representation of the
	 * type of an {@link Action}
	 * @param a The input {@link Action}
	 * @return
	 */
	public static String getActionTypeText(Action a) {
		System.out.println(a.getClass());
		if (a instanceof FileSplitter) {
			if (a instanceof FileSplitterByPartSize)
				return "By size";
			else if (a instanceof FileSplitterWithEncryption)
				return "Encrypted";
			else if (a instanceof FileSplitterByPartCount)
				return "By count";
		} else if (a instanceof FileMerger) {
			if (a instanceof EncryptedFileMerger)
				return "Encrypted";
			else if (a instanceof DefaultFileMerger)
				return "By size/count";
		}
		System.out.println("is a a merger? " + (a instanceof DefaultFileMerger));// || a instanceof EncryptedFileMerger));
		return "???";
	}
	
	/**
	 * A function that capitalizes the input {@link String}
	 * @param str The input {@link String}
	 * @return 
	 */
	public static String capitalizeString(String str) {
		if (str.length() == 0)
			return "";
		else if (str.length() == 1)
			return str.toUpperCase();
		else
			return str.toUpperCase().charAt(0) + str.toLowerCase().substring(1);
	}
}

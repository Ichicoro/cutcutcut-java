package utils;

import actors.*;

public class Utils {
	public static boolean isNumeric(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }
	
	public static String getActionTypeText(Action a) {
		if (a instanceof FileSplitter) {
			if (a instanceof FileSplitterByPartSize) {
				return "By size";
			} else if (a instanceof FileSplitterByPartCount) {
				return "By count";
			}
		} else {
			return "owo";
		}
		return null;
	}
}

package com.polaris.core.log;

public class LoggerNameUtil {
    public static final char DOT = '.';
    public static final char TAB = '\t';
    public static final char DOLLAR = '$';
    
	public static int getSeparatorIndexOf(String name, int fromIndex) {
        int dotIndex = name.indexOf(DOT, fromIndex);
        int dollarIndex = name.indexOf(DOLLAR, fromIndex);

        if (dotIndex == -1 && dollarIndex == -1)
            return -1;
        if (dotIndex == -1)
            return dollarIndex;
        if (dollarIndex == -1)
            return dotIndex;

        return dotIndex < dollarIndex ? dotIndex : dollarIndex;
    }
}

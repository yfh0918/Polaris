package com.polaris.comm.filter;

import com.polaris.comm.util.StringUtil;

public class CodeFilter {
	static String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
	public static String escapeFromHtml(String s) {  
        if (StringUtil.isEmpty(s)) {
        	return s;
        }
        s = Replace(s, "&", "&amp;");  
        s = Replace(s, "<", "&lt;");  
        s = Replace(s, ">", "&gt;");  
        s = Replace(s, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");  
        s = Replace(s, "\r\n", "\n");  
        s = Replace(s, "\n", "<br>");  
        s = Replace(s, "  ", "&nbsp;&nbsp;");  
        s = Replace(s, "'", "&#39;");  
        s = Replace(s, "\\", "&#92;");
        for (String key : fbsArr) {  
            if (s.contains(key)) {  
                s = s.replace(key, "\\" + key);  
            }  
        } 
        return s.trim();  
    }
	
	public static String escapeFromDB(String s) {  
        if (StringUtil.isEmpty(s)) {
        	return s;
        }
        s = Replace(s, "&amp;", "&");  
	    s = Replace(s, "&nbsp;", " ");  
	    s = Replace(s, "&#39;", "'");          
	    s = Replace(s, "&#92;", "\\"); 
	    s = Replace(s, "&lt;", "<");
	    s = Replace(s, "&gt;", ">");  
	    s = Replace(s, "<br>", "\n");  
        return s.trim();  
    }
	
	public static String Replace(String s, String s1, String s2)  
    {  
        if(s == null) {  
            return null;  
        }  
        StringBuffer stringbuffer = new StringBuffer();  
        int i = s.length();  
        int j = s1.length();  
        int k;  
        int l;  
        for(k = 0; (l = s.indexOf(s1, k)) >= 0; k = l + j) {  
            stringbuffer.append(s.substring(k, l));  
            stringbuffer.append(s2);  
        }  

        if(k < i)   {  
            stringbuffer.append(s.substring(k));  
        }  
        return stringbuffer.toString();  
    }  
}

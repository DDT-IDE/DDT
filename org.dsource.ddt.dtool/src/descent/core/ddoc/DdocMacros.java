package descent.core.ddoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to do ddoc macro replacing.
 */
public class DdocMacros {
	
	private final static Map<String, String> defaultMacros;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("B", "<b>$0</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("I", "<i>$0</i>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("U", "<u>$0</u>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("P", "<p>$0</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DL", "<dl>$0</dl>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DT", "<dt>$0</dt>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DD", "<dd>$0</dd>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("TABLE", "<table border=\"1\" cellpadding=\"4\">$0</table>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("TR", "<tr>$0</tr>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("TH", "<th>$0</th>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("TD", "<td>$0</td>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("OL", "<ol>$0</ol>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("UL", "<ul>$0</ul>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("LI", "<li>$0</li>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("BIG", "<big>$0</big>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("SMALL", "<small>$0</small>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("BR", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("LINK", "<a href=\"$0\" target=\"_blank\">$0</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("LINK2", "<a href=\"$1\" target=\"_blank\">$+</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		map.put("RED", "<font color=red>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("BLUE", "<font color=blue>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("GREEN", "<font color=green>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("YELLOW", "<font color=yellow>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("BLACK", "<font color=black>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("WHITE", "<font color=white>$0</font>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		map.put("D_CODE", "<span class=\"code\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// TODO ddoc macro provider
		map.put("D_COMMENT", "<span class=\"java_single_line_comment\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		map.put("D_STRING", "<span class=\"java_string\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		map.put("D_KEYWORD", "<span class=\"java_keyword\">$0</span>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		map.put("D_PSYMBOL", "$(U $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("D_PARAM", "$(I $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		
		map.put("DDOC", "<html><head> <META http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"><title>$(TITLE)</title></head><body><h1>$(TITLE)</h1>$(BODY)</body></html>"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_COMMENT", "<!-- $0 -->"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DECL", "$(DT $(BIG $0))"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DECL_DD", "$(DD $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DITTO", "$(BR) $0"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_SECTIONS", "$0"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_SUMMARY", "$0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DESCRIPTION", "$0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_AUTHORS", "$(B Authors:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_BUGS", "$(RED BUGS:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_COPYRIGHT", "$(B Copyright:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DATE", "$(B Date:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_DEPRECATED", "$(RED Deprecated:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_EXAMPLES", "$(B Examples:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_HISTORY", "$(B History:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_LICENSE", "$(B License:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_RETURNS", "$(B Returns:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_SEE_ALSO", "$(B See Also:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_STANDARDS", "$(B Standards:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_THROWS", "$(B Throws:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_VERSION", "$(B Version:)$(BR) $0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_SECTION_H", "$(B $0)$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_SECTION", "$0$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_MEMBERS", "$(DL $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_MODULE_MEMBERS", "$(DDOC_MEMBERS $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_CLASS_MEMBERS", "$(DDOC_MEMBERS $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_STRUCT_MEMBERS", "$(DDOC_MEMBERS $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_ENUM_MEMBERS", "$(DDOC_MEMBERS $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_TEMPLATE_MEMBERS", "$(DDOC_MEMBERS $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_PARAMS", "$(B Params:)$(BR)\n$(TABLE $0)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_PARAM_ROW", "$(TR $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_PARAM_ID", "$(TD $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_PARAM_DESC", "$(TD $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_BLANKLINE", "$(BR)$(BR)"); //$NON-NLS-1$ //$NON-NLS-2$
		
		map.put("DDOC_PSYMBOL", "$(U $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_KEYWORD", "$(B $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		map.put("DDOC_PARAM", "$(I $0)"); //$NON-NLS-1$ //$NON-NLS-2$
		
		defaultMacros = Collections.unmodifiableMap(map);
	}
	
	/**
	 * Returns a map of the default macros. The key is the macro name,
	 * the value is the replacement. This map in unmodifiable.
	 */
	public static Map<String, String> getDefaultMacros() {
		return defaultMacros;
	}
	
	/**
	 * Replaces the macros found in the given string with the given macros
	 * map.
	 * @param string the string to replace
	 * @param macros the macros map
	 * @return the replaced string
	 */
	public static String replaceMacros(String string, Map<String, String> macros) {
		TreeSet<String> usedMacros = new TreeSet<String>();
		return replaceMacros(string, new int[] { 0 }, macros, usedMacros, false /* not nested */);
	}
	
	private static String replaceMacros(String string, int[] from, Map<String, String> macros, Set<String> usedMacros, boolean isNested) {
		// Total string
		StringBuilder sb = new StringBuilder();
		
		// In case a macro is started but not finished
		StringBuilder temp = new StringBuilder();
		
		// The current argument in the macro
		StringBuilder currentArgument = new StringBuilder();
		
		// Argument $0
		StringBuilder $0 = new StringBuilder();
		
		// Argument $+
		StringBuilder $plus = new StringBuilder();
		
		List<String> arguments = new ArrayList<String>(); 
		
		int length = string.length();
		
		loop: 
		for(; from[0] < length; from[0]++) {
			char c = string.charAt(from[0]);
			if (c != '$') {
				sb.append(c);
				continue;
			}
			
			temp.setLength(0);
			
			temp.append(c);			
			from[0]++;
			if (from[0] == length || string.charAt(from[0]) != '(') {
				sb.append(temp);
				if (from[0] != length) {
					sb.append(string.charAt(from[0]));
				}
				continue;
			}
			c = string.charAt(from[0]);
			temp.append(c);
			
			from[0]++;
			if (from[0] == length) {
				sb.append(temp);
				continue;
			}
			c = string.charAt(from[0]);
			
			currentArgument.setLength(0);
			$0.setLength(0);
			$plus.setLength(0);
			arguments.clear();
			
			boolean foundSpace = false;
			boolean foundComma = false;
			
			int parenCount = 0;
			for(; from[0] < length; from[0]++) {
				c = string.charAt(from[0]);
				if (c == '$' && from[0] < length - 1 && string.charAt(from[0] + 1) == '(') {
					String result = replaceMacros(string, from, macros, usedMacros, true /* nested */);
					currentArgument.append(result);				
					temp.append(result);
					if (foundSpace) {
						$0.append(result);
					}
					if (foundComma) {
						$plus.append(result);
					}
					continue;
				} else if (c == ' ' && !foundSpace) {
					foundSpace = true;
					arguments.add(currentArgument.toString());
					currentArgument.setLength(0);
					temp.append(c);
					continue;
				} else if (c == ')' && parenCount > 0) {
					parenCount--;
				} else if (c == ')' && parenCount == 0) {
					arguments.add(currentArgument.toString());
					
					String macroName = arguments.get(0);
					String replacement = macros.get(macroName);
					if (replacement != null) {
						// Recursive step: replace macros in replacement
						if (!usedMacros.contains(macroName)) {
							usedMacros.add(macroName);
							replacement = replaceMacros(replacement, new int[] { 0 }, macros, usedMacros, false /* not nested */);
							usedMacros.remove(macroName);
							
							replacement = replaceParameters(replacement, arguments, $0.toString(), $plus.toString());
							sb.append(replacement);
						}
					}
					if (isNested) {
						return sb.toString();
					} else {
						continue loop;
					}
				} else if (c == ',') {
					if (foundComma) {
						$plus.append(c);
					}
					foundComma = true;
					arguments.add(currentArgument.toString());
					currentArgument.setLength(0);
					$0.append(c);
					continue;
				}
				
				if (c == '(') {
					parenCount++;
				}
				
				currentArgument.append(c);				
				temp.append(c);
				if (foundSpace) {
					$0.append(c);
				}
				if (foundComma) {
					$plus.append(c);
				}
			}
			
			sb.append(temp);
		}
		
		return sb.toString();
	}
	
	private static String replaceParameters(String string, List<String> arguments, String $0, String $plus) {
		StringBuilder sb = new StringBuilder();
		
		int length = string.length();
		for(int i = 0; i < length; i++) {
			char c = string.charAt(i);
			if (c == '$' && i < length - 1) {
				i++;
				c = string.charAt(i);
				if ('0' <= c && c <= '9') {
					int index = c - '0';
					if (index == 0) {
						sb.append($0);
					} else if (1 <= index && index < arguments.size()) {
						sb.append(arguments.get(index));
					} else {
						// Default behaviour of DMD
						sb.append($0);
					}
				} else if (c == '+') {
					sb.append($plus);
				} else {
					sb.append('$');
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}

}

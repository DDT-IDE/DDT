package mmrnmhrm.core.build;

public class DeeBuilderUtils {

	public static String escapeQuotes(String str) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			switch(ch) {
			case '"':
				sb.append('\\');
				sb.append('"');
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
	
}

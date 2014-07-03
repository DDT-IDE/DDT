package mmrnmhrm.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

// See: http://www.digitalmars.com/d/2.0/lex.html
public class DeePartitionScanner extends RuleBasedPartitionScanner {
	// BM Note: we should be careful with having different rules return the same token
	// Such behavior is not properly supported by RuleBasedPartitionScanner
	
	private static final char NO_ESCAPE_CHAR = (char) -1;
	
	/**Creates the partitioner and sets up the appropriate rules. */
	public DeePartitionScanner() {
		IToken tkString = new Token(DeePartitions.DEE_STRING);
		IToken tkRawString = new Token(DeePartitions.DEE_RAW_STRING);
		IToken tkRawString2 = new Token(DeePartitions.DEE_RAW_STRING2);
		IToken tkDelimString = new Token(DeePartitions.DEE_DELIM_STRING);
		IToken tkCharacter = new Token(DeePartitions.DEE_CHARACTER);
		IToken tkSingleComment = new Token(DeePartitions.DEE_SINGLE_COMMENT);
		IToken tkSingleDocComment = new Token(DeePartitions.DEE_SINGLE_DOCCOMMENT);
		IToken tkMultiComment = new Token(DeePartitions.DEE_MULTI_COMMENT);
		IToken tkMultiDocComment = new Token(DeePartitions.DEE_MULTI_DOCCOMMENT);
		IToken tkNestedComment = new Token(DeePartitions.DEE_NESTED_COMMENT);
		IToken tkNestedDocComment = new Token(DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		
		rules.add(new PatternRule_Fixed("`", "`", tkRawString, NO_ESCAPE_CHAR, false, true));
		rules.add(new PatternRule_Fixed("r\"", "\"", tkRawString2, NO_ESCAPE_CHAR, false, true));
		// TODO: this rule is not accurate, need to use something like HereDocEnabledPartitioner to make it work
		rules.add(new PatternRule_Fixed("q\"", "\"", tkDelimString, NO_ESCAPE_CHAR, false, true)); 
		rules.add(new PatternRule_Fixed("\"", "\"", tkString, '\\', false, true));
		rules.add(new PatternRule_Fixed("'", "'", tkCharacter, '\\', true, true));
		
		
		rules.add(new PatternRule_Fixed("///", null, tkSingleDocComment, NO_ESCAPE_CHAR, true, true)); 
		rules.add(new PatternRule_Fixed("//", null, tkSingleComment, NO_ESCAPE_CHAR, true, true));
		
		rules.add(new NestedDelimiterRule("/++", "/+", "+/", tkNestedDocComment, NO_ESCAPE_CHAR, true));
		rules.add(new NestedDelimiterRule("/+", "/+", "+/", tkNestedComment, NO_ESCAPE_CHAR, true));
		rules.add(new PatternRule_Fixed("/**", "*/", tkMultiDocComment, NO_ESCAPE_CHAR, false, true));
		rules.add(new PatternRule_Fixed("/*", "*/", tkMultiComment, NO_ESCAPE_CHAR, false, true));
		
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
	
}
package mmrnmhrm.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

// See: http://www.digitalmars.com/d/2.0/lex.html
public class DeePartitionScanner extends RuleBasedPartitionScanner {
	
	private static final char NO_ESCAPE_CHAR = (char) -1;
	
	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public DeePartitionScanner() {
		IToken tkString = new Token(DeePartitions.DEE_STRING);
		IToken tkRawString = new Token(DeePartitions.DEE_RAW_STRING);
		IToken tkDelimString = new Token(DeePartitions.DEE_DELIM_STRING);
		IToken tkCharacter = new Token(DeePartitions.DEE_CHARACTER);
		IToken tkSingleComment = new Token(DeePartitions.DEE_SINGLE_COMMENT);
		IToken tkSingleDocComment = new Token(DeePartitions.DEE_SINGLE_DOCCOMMENT);
		IToken tkMultiComment = new Token(DeePartitions.DEE_MULTI_COMMENT);
		IToken tkMultiDocComment = new Token(DeePartitions.DEE_MULTI_DOCCOMMENT);
		IToken tkNestedComment = new Token(DeePartitions.DEE_NESTED_COMMENT);
		IToken tkNestedDocComment = new Token(DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		
		rules.add(new MultiLineRule("`", "`", tkRawString, NO_ESCAPE_CHAR, true));
		rules.add(new MultiLineRule("r\"", "\"", tkRawString, NO_ESCAPE_CHAR, true));
		rules.add(new MultiLineRule("q\"", "\"", tkDelimString, NO_ESCAPE_CHAR, true)); // TODO: this rule is not accurate
		rules.add(new MultiLineRule("\"", "\"", tkString, '\\', true));
		rules.add(new SingleLineRule("'", "'", tkCharacter, '\\', true));
		
		
		rules.add(new EndOfLineRule("///", tkSingleDocComment, NO_ESCAPE_CHAR)); 
		rules.add(new EndOfLineRule("//", tkSingleComment, NO_ESCAPE_CHAR));
		
		rules.add(new NestedDelimiterRule("/++", "/+", "+/", tkNestedDocComment, NO_ESCAPE_CHAR, true));
		rules.add(new NestedDelimiterRule("/+", "/+", "+/", tkNestedComment, NO_ESCAPE_CHAR, true));
		rules.add(new MultiLineRule("/**", "*/", tkMultiDocComment, NO_ESCAPE_CHAR, true));
		rules.add(new MultiLineRule("/*", "*/", tkMultiComment, NO_ESCAPE_CHAR, true));
		
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
	
}
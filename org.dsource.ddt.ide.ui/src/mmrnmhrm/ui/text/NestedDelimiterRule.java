package mmrnmhrm.ui.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class NestedDelimiterRule extends PatternRule_Fixed {
	
	protected final char[] anotherStart;
	protected final char[][] startSequences;
	protected char[][] innerStartSequences;
	protected int fOpenDelims = 0;
	
	public NestedDelimiterRule(String start, String innerStart, String end, IToken token, char escapeCharacter, 
		boolean breaksOnEOF) {
		super(start, end, token, escapeCharacter, false, breaksOnEOF);
		this.anotherStart = innerStart.toCharArray();
		startSequences = new char[][] { fStartSequence };
		innerStartSequences = new char[][] { fStartSequence, anotherStart };
	}
	
	
	@Override
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		if(resume) {
			return Token.UNDEFINED; // We can't evaluate this rule mid-way, need to restart
		}
		
		if (anySequenceMatched(scanner, startSequences)) {
			if (anyEndSequenceMatched(scanner))
				return fToken;
			
		}
		return Token.UNDEFINED;
	}
	
	protected boolean anySequenceMatched(ICharacterScanner scanner, char[][] possibleSequences) {
		int ch = scanner.read();
		for (int i = 0; i < possibleSequences.length; i++) {
			char[] sequence = possibleSequences[i];
			if(ch == sequence[0] && sequenceDetected(scanner, sequence, false))
				return true;
		}
		scanner.unread();
		return false;
	}
	
	protected boolean anyEndSequenceMatched(ICharacterScanner scanner) {
		fOpenDelims = 1;
		int ch;
		int readCount = 1;
		while((ch = scanner.read()) != ICharacterScanner.EOF) {
			if (ch == fEndSequence[0] && sequenceDetected(scanner, fEndSequence, false)) {
				if(--fOpenDelims == 0) 
					return true;
				
				readCount += fEndSequence.length - 1;
				continue;
			}
			if (anySequenceMatched(scanner, innerStartSequences)) {
				fOpenDelims++;
			}
			readCount++;
		}
		
		if (fBreaksOnEOF)
			return true;
		
		do {
			scanner.unread();
		} while (--readCount > 0);
		
		return false;
	}
	
}
package org.eclipse.dltk.ruby.internal.ui.text;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class RubyHeuristicScanner extends ScriptHeuristicScanner implements
		IRubySymbols {
	private static final int[] BLOCK_BEGINNING_KEYWORDS = { TokenIF, TokenFOR,
			TokenDEF, TokenCASE, TokenCATCH, TokenCLASS, TokenWHILE,
			TokenBEGIN, TokenUNTIL, TokenUNLESS, TokenMODULE, TokenDO };

	private static final int[] BLOCK_BEGINNING_SYMBOLS = { TokenLBRACE,
			TokenLBRACKET };

	private static final int[] BLOCK_MIDDLES = { TokenELSE, TokenELSIF,
			TokenENSURE, TokenRESCUE, TokenWHEN };

	private static final int[] BLOCK_ENDINGS = { TokenEND, TokenRBRACE,
			TokenRBRACKET };

	static {
		Arrays.sort(BLOCK_BEGINNING_KEYWORDS);
		Arrays.sort(BLOCK_BEGINNING_SYMBOLS);
		Arrays.sort(BLOCK_MIDDLES);
		Arrays.sort(BLOCK_ENDINGS);
	}

	/**
	 * Calls
	 * <code>super(document, IRubyPartitions.RUBY_PARTITIONING, IDocument.DEFAULT_CONTENT_TYPE)</code>
	 * .
	 * 
	 * @param document
	 *            the document to scan.
	 */
	public RubyHeuristicScanner(IDocument document) {
		super(document, IRubyPartitions.RUBY_PARTITIONING,
				IDocument.DEFAULT_CONTENT_TYPE);
	}

	public int getToken(int pos, String s) {
		Assert.isNotNull(s);

		switch (s.length()) {
		case 2:
			if ("if".equals(s)) //$NON-NLS-1$
				return TokenIF;
			if ("do".equals(s)) //$NON-NLS-1$
				return TokenDO;
			if ("in".equals(s)) //$NON-NLS-1$
				return TokenIN;
			if ("or".equals(s)) //$NON-NLS-1$
				return TokenOR;
			break;
		case 3:
			if ("for".equals(s)) //$NON-NLS-1$
				return TokenFOR;
			if ("and".equals(s)) //$NON-NLS-1$
				return TokenAND;
			if ("def".equals(s)) //$NON-NLS-1$
				return TokenDEF;
			if ("end".equals(s)) { //$NON-NLS-1$
				if (pos > 0 && getChar(pos - 1) == '=') {
					return TokenRDOCEND;
				}
				return TokenEND;
			}
			if ("END".equals(s)) //$NON-NLS-1$
				return TokenEND;
			if ("nil".equals(s)) //$NON-NLS-1$
				return TokenNIL;
			if ("not".equals(s)) //$NON-NLS-1$
				return TokenNOT;
			break;
		case 4:
			if ("case".equals(s)) //$NON-NLS-1$
				return TokenCASE;
			if ("else".equals(s)) //$NON-NLS-1$
				return TokenELSE;
			if ("when".equals(s)) //$NON-NLS-1$
				return TokenWHEN;
			if ("then".equals(s)) //$NON-NLS-1$
				return TokenTHEN;
			if ("next".equals(s)) //$NON-NLS-1$
				return TokenNEXT;
			if ("redo".equals(s)) //$NON-NLS-1$
				return TokenREDO;
			break;
		case 5:
			if ("break".equals(s)) //$NON-NLS-1$
				return TokenBREAK;
			if ("catch".equals(s)) //$NON-NLS-1$
				return TokenCATCH;
			if ("class".equals(s)) //$NON-NLS-1$
				return TokenCLASS;
			if ("while".equals(s)) //$NON-NLS-1$
				return TokenWHILE;
			if ("alias".equals(s)) //$NON-NLS-1$
				return TokenALIAS;
			if ("BEGIN".equals(s)) //$NON-NLS-1$
				return TokenBEGIN;
			if ("begin".equals(s)) { //$NON-NLS-1$
				if (pos > 0 && getChar(pos - 1) == '=') {
					return TokenRDOCBEGIN;
				}
				return TokenBEGIN;
			}
			if ("elsif".equals(s)) //$NON-NLS-1$
				return TokenELSIF;
			if ("retry".equals(s)) //$NON-NLS-1$
				return TokenRETRY;
			if ("undef".equals(s)) //$NON-NLS-1$
				return TokenUNDEF;
			if ("until".equals(s)) //$NON-NLS-1$
				return TokenUNTIL;
			if ("yield".equals(s)) //$NON-NLS-1$
				return TokenYIELD;
			break;
		case 6:
			if ("return".equals(s)) //$NON-NLS-1$
				return TokenRETURN;
			if ("ensure".equals(s)) //$NON-NLS-1$
				return TokenENSURE;
			if ("rescue".equals(s)) //$NON-NLS-1$
				return TokenRESCUE;
			if ("unless".equals(s)) //$NON-NLS-1$
				return TokenUNLESS;
			if ("module".equals(s)) //$NON-NLS-1$
				return TokenMODULE;
			break;
		case 7:
			if ("defined".equals(s)) //$NON-NLS-1$
				return TokenDEFINED;
			break;
		}
		return TokenIDENTIFIER;
	}

	private char getChar(int pos) {
		try {
			return getDocument().getChar(pos);
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
			return 0;
		}
	}

	public IRegion findSurroundingBlock(int offset) {
		int start = findBlockBeginningOffset(offset);
		if (start == NOT_FOUND)
			start = 0;

		int end = findBlockEndingOffset(offset);
		if (end == NOT_FOUND)
			end = getDocument().getLength();

		return new Region(start, end - start);
	}

	public boolean isBlockBeginning(int offset, int bound) {
		int token = previousToken(bound, offset);
		while (token != NOT_FOUND) {
			if (Arrays.binarySearch(BLOCK_BEGINNING_SYMBOLS, token) >= 0)
				return true;

			if (Arrays.binarySearch(BLOCK_BEGINNING_KEYWORDS, token) >= 0) {
				int pos = getPosition();
				int prevToken = token;
				token = previousToken(getPosition(), offset);
				if (token == NOT_FOUND || token == TokenEQUAL
						|| prevToken == TokenDO) {
					setPosition(pos + 1);
					return true;
				}
			}

			token = previousToken(getPosition(), offset);
		}

		return false;
	}

	public boolean isBlockMiddle(int offset, int bound) {
		if (Arrays.binarySearch(BLOCK_MIDDLES, nextToken(offset, bound)) >= 0) {
			// setting the position to start of the block keyword
			findNonIdentifierBackward(offset, UNBOUND);
			setPosition(getPosition() + 1);
			return true;
		} else {
			return false;
		}
	}

	public boolean isBlockEnding(int offset, int bound) {
		int token = nextToken(offset, bound);
		while (token != NOT_FOUND) {
			if (Arrays.binarySearch(BLOCK_ENDINGS, token) >= 0)
				return true;
			token = nextToken(getPosition(), bound);
		}

		return false;
	}
	
	public int findBlockBeginningOffset(int offset) {
		try {
			IDocument d = getDocument();
			int line = d.getLineOfOffset(offset);
			int endingCount = 0;
			while (line >= 0) {
				IRegion info = d.getLineInformation(line);
				int start = info.getOffset();
				int end = Math.min(info.getOffset() + info.getLength(), offset);
				setPosition(start);
				while (getPosition() < end) {
					if (isBlockEnding(getPosition(), end)) {
						endingCount++;
					}
				}

				start = info.getOffset();
				end = Math.min(info.getOffset() + info.getLength(), offset);
				setPosition(end);
				while (getPosition() > start) {
					if (isBlockBeginning(start, getPosition())) {
						if (endingCount > 0) {
							endingCount--;
						} else {
							return getPosition();
						}
					}
				}

				line--;
			}
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
		return NOT_FOUND;
	}

	public int findBlockEndingOffset(int offset) {
		try {
			IDocument d = getDocument();
			int line = d.getLineOfOffset(offset);
			int lineNum = d.getNumberOfLines();
			int beginningCount = 0;
			while (line < lineNum) {
				IRegion info = d.getLineInformation(line);
				int start = Math.max(info.getOffset(), offset);
				int end = info.getOffset() + info.getLength();
				setPosition(end);
				while (getPosition() > start) {
					if (isBlockBeginning(start, getPosition())) {
						beginningCount++;
					}
				}

				start = Math.max(info.getOffset(), offset);
				end = info.getOffset() + info.getLength();
				setPosition(start);
				while (getPosition() < end) {
					if (isBlockEnding(getPosition(), end)) {
						if (beginningCount > 0) {
							beginningCount--;
						} else {
							return getPosition();
						}
					}
				}

				line++;
			}
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
		return NOT_FOUND;
	}

	public int previousTokenAfterInput(int offset, String appended) {
		try {
			if (getPartition(offset).getType() != IDocument.DEFAULT_CONTENT_TYPE)
				return NOT_FOUND;

			if (appended.length() == 1) {
				int token = getGenericToken(appended.charAt(0));
				if (token != TokenOTHER)
					return token;
			}

			IRegion line = getDocument().getLineInformationOfOffset(offset);
			String content = getDocument().get(line.getOffset(),
					offset - line.getOffset())
					+ appended;
			IDocument newDoc = new Document(content);
			RubyHeuristicScanner scanner = new RubyHeuristicScanner(newDoc);
			return scanner.previousToken(content.length(), UNBOUND);
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
		return NOT_FOUND;
	}
}

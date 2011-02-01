package org.eclipse.dltk.ruby.internal.ui.text;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;

public abstract class ScriptHeuristicScanner implements ISymbols {
	/**
	 * Specifies the stop condition, upon which the <code>scanXXX</code>
	 * methods will decide whether to keep scanning or not. This interface may
	 * implemented by clients.
	 */
	protected static abstract class StopCondition {
		/**
		 * Instructs the scanner to return the current position.
		 * 
		 * @param ch
		 *            the char at the current position
		 * @param position
		 *            the current position
		 * @param forward
		 *            the iteration direction
		 * @return <code>true</code> if the stop condition is met.
		 */
		public abstract boolean stop(char ch, int position, boolean forward);

		/**
		 * Asks the condition to return the next position to query. The default
		 * is to return the next/previous position.
		 * 
		 * @return the next position to scan
		 */
		public int nextPosition(int position, boolean forward) {
			return forward ? position + 1 : position - 1;
		}
	}

	/**
	 * Stops upon a non-whitespace (as defined by
	 * {@link Character#isWhitespace(char)}) character.
	 */
	protected static class NonWhitespace extends StopCondition {
		public boolean stop(char ch, int position, boolean forward) {
			return !Character.isWhitespace(ch);
		}
	}

	/**
	 * Stops upon a non-whitespace character in the default partition.
	 * 
	 * @see RubyHeuristicScanner.NonWhitespace
	 */
	protected final class NonWhitespaceDefaultPartition extends NonWhitespace {
		public boolean stop(char ch, int position, boolean forward) {
			return super.stop(ch, position, true)
					&& isDefaultPartition(position);
		}

		public int nextPosition(int position, boolean forward) {
			ITypedRegion partition = getPartition(position);
			if (fPartition.equals(partition.getType()))
				return super.nextPosition(position, forward);

			if (forward) {
				int end = partition.getOffset() + partition.getLength();
				if (position < end)
					return end;
			} else {
				int offset = partition.getOffset();
				if (position > offset)
					return offset - 1;
			}
			return super.nextPosition(position, forward);
		}
	}

	/**
	 * Stops upon a non-java identifier (as defined by
	 * {@link Character#isJavaIdentifierPart(char)}) character.
	 */
	protected class NonIdentifierPart extends StopCondition {
		public boolean stop(char ch, int position, boolean forward) {
			return !isValidIdentifierPart(ch);
		}
	}

	/**
	 * Stops upon a non-java identifier character in the default partition.
	 * 
	 * @see JavaHeuristicScanner.NonIdentifierPart
	 */
	protected final class NonIdentifierPartDefaultPartition extends
			NonIdentifierPart {
		public boolean stop(char ch, int position, boolean forward) {
			return super.stop(ch, position, true)
					|| !isDefaultPartition(position);
		}

		public int nextPosition(int position, boolean forward) {
			ITypedRegion partition = getPartition(position);
			if (fPartition.equals(partition.getType()))
				return super.nextPosition(position, forward);

			if (forward) {
				int end = partition.getOffset() + partition.getLength();
				if (position < end)
					return end;
			} else {
				int offset = partition.getOffset();
				if (position > offset)
					return offset - 1;
			}
			return super.nextPosition(position, forward);
		}
	}

	/**
	 * Stops upon a character in the default partition that matches the given character list.
	 */
	protected final class CharacterMatch extends StopCondition {
		private final char[] fChars;

		/**
		 * Creates a new instance.
		 * @param ch the single character to match
		 */
		public CharacterMatch(char ch) {
			this(new char[] {ch});
		}

		/**
		 * Creates a new instance.
		 * @param chars the chars to match.
		 */
		public CharacterMatch(char[] chars) {
			Assert.isNotNull(chars);
			Assert.isTrue(chars.length > 0);
			fChars= chars;
			Arrays.sort(chars);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.JavaHeuristicScanner.StopCondition#stop(char, int)
		 */
		public boolean stop(char ch, int position, boolean forward) {
			return Arrays.binarySearch(fChars, ch) >= 0 && isDefaultPartition(position);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.JavaHeuristicScanner.StopCondition#nextPosition(int, boolean)
		 */
		public int nextPosition(int position, boolean forward) {
			ITypedRegion partition= getPartition(position);
			if (fPartition.equals(partition.getType()))
				return super.nextPosition(position, forward);

			if (forward) {
				int end= partition.getOffset() + partition.getLength();
				if (position < end)
					return end;
			} else {
				int offset= partition.getOffset();
				if (position > offset)
					return offset - 1;
			}
			return super.nextPosition(position, forward);
		}
	}
	
	/**
	 * Returned by all methods when the requested position could not be found,
	 * or if a {@link BadLocationException} was thrown while scanning.
	 */
	public static final int NOT_FOUND = -1;
	/**
	 * Special bound parameter that means either -1 (backward scanning) or
	 * <code>fDocument.getLength()</code> (forward scanning).
	 */
	public static final int UNBOUND = -2;

	/* character constants */
	private static final char LBRACE = '{';
	private static final char RBRACE = '}';
	private static final char LPAREN = '(';
	private static final char RPAREN = ')';
	private static final char SEMICOLON = ';';
	private static final char COLON = ':';
	private static final char COMMA = ',';
	private static final char LBRACKET = '[';
	private static final char RBRACKET = ']';
	private static final char QUESTIONMARK = '?';
	private static final char EQUAL = '=';
	private static final char LANGLE = '<';
	private static final char RANGLE = '>';
	private static final char BACKSLASH = '\\';
	private static final char SLASH = '/';
	private static final char PLUS = '+';
	private static final char MINUS = '-';
	private static final char STAR = '*';
	
	/* preset stop conditions */
	private final StopCondition fNonWSDefaultPart = new NonWhitespaceDefaultPartition();
	private final static StopCondition fNonWS = new NonWhitespace();
	private final StopCondition fNonIdentifier = new NonIdentifierPartDefaultPartition();

	/** The document being scanned. */
	private final IDocument fDocument;
	/** The partitioning being used for scanning. */
	private final String fPartitioning;
	/** The partition to scan in. */
	private final String fPartition;

	/* internal scan state */

	/** the most recently read character. */
	private char fChar;
	/** the most recently read position. */
	private int fPos;
	/**
	 * The most recently used partition.
	 * 
	 * @since 3.2
	 */
	private ITypedRegion fCachedPartition = new TypedRegion(-1, 0,
			"__no_partition_at_all"); //$NON-NLS-1$

	/**
	 * @param document
	 *            the document to scan
	 * @param partitioning
	 *            the partitioning to use for scanning
	 * @param partition
	 *            the partition to scan in
	 */
	protected ScriptHeuristicScanner(IDocument document, String partitioning,
			String partition) {
		Assert.isLegal(document != null);
		Assert.isLegal(partitioning != null);
		Assert.isLegal(partition != null);
		fDocument = document;
		fPartitioning = partitioning;
		fPartition = partition;
	}

	/**
	 * Returns the most recent internal scan position.
	 * 
	 * @return the most recent internal scan position.
	 */
	public int getPosition() {
		return fPos;
	}

	/**
	 * Sets the most recent internal scan position.
	 * 
	 * @return the most recent internal scan position.
	 */
	protected void setPosition(int pos) {
		fPos = pos;
	}
	
	/**
	 * Returns the scanned document
	 * 
	 * @return the scanned document
	 */
	public IDocument getDocument() {
		return fDocument;
	}

	/**
	 * Returns the partition at <code>position</code>.
	 * 
	 * @param position
	 *            the position to get the partition for
	 * @return the partition at <code>position</code> or a dummy zero-length
	 *         partition if accessing the document fails
	 */
	protected ITypedRegion getPartition(int position) {
		if (!contains(fCachedPartition, position)) {
			Assert.isTrue(position >= 0);
			Assert.isTrue(position <= fDocument.getLength());

			try {
				fCachedPartition = TextUtilities.getPartition(fDocument,
						fPartitioning, position, false);
			} catch (BadLocationException e) {
				fCachedPartition = new TypedRegion(position, 0,
						"__no_partition_at_all"); //$NON-NLS-1$
			}
		}

		return fCachedPartition;
	}

	/**
	 * Returns <code>true</code> if <code>region</code> contains
	 * <code>position</code>.
	 * 
	 * @param region
	 *            a region
	 * @param position
	 *            an offset
	 * @return <code>true</code> if <code>region</code> contains
	 *         <code>position</code>
	 * @since 3.2
	 */
	private boolean contains(IRegion region, int position) {
		int offset = region.getOffset();
		return offset <= position && position < offset + region.getLength();
	}

	/**
	 * Checks whether <code>position</code> resides in a default partition of
	 * <code>fDocument</code>.
	 * 
	 * @param position
	 *            the position to be checked
	 * @return <code>true</code> if <code>position</code> is in the default
	 *         partition of <code>fDocument</code>, <code>false</code>
	 *         otherwise
	 */
	public boolean isDefaultPartition(int position) {
		return fPartition.equals(getPartition(position).getType());
	}

	/**
	 * Finds the lowest position <code>p</code> in <code>fDocument</code>
	 * such that <code>start</code> &lt;= p &lt; <code>bound</code> and
	 * <code>condition.stop(fDocument.getChar(p), p)</code> evaluates to
	 * <code>true</code>.
	 * 
	 * @param start
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &gt; <code>start</code>,
	 *            or <code>UNBOUND</code>
	 * @param condition
	 *            the <code>StopCondition</code> to check
	 * @return the lowest position in [<code>start</code>,
	 *         <code>bound</code>) for which <code>condition</code> holds,
	 *         or <code>NOT_FOUND</code> if none can be found
	 */
	public int scanForward(int start, int bound, StopCondition condition) {
		Assert.isLegal(start >= 0);

		if (bound == UNBOUND)
			bound = fDocument.getLength();

		Assert.isLegal(bound <= fDocument.getLength());

		try {
			fPos = start;
			while (fPos < bound) {

				fChar = fDocument.getChar(fPos);
				if (condition.stop(fChar, fPos, true))
					return fPos;

				fPos = condition.nextPosition(fPos, true);
			}
		} catch (BadLocationException e) {
		}
		return NOT_FOUND;
	}

	/**
	 * Finds the highest position <code>p</code> in <code>fDocument</code>
	 * such that <code>bound</code> &lt; <code>p</code> &lt;=
	 * <code>start</code> and
	 * <code>condition.stop(fDocument.getChar(p), p)</code> evaluates to
	 * <code>true</code>.
	 * 
	 * @param start
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &lt; <code>start</code>,
	 *            or <code>UNBOUND</code>
	 * @param condition
	 *            the <code>StopCondition</code> to check
	 * @return the highest position in (<code>bound</code>,
	 *         <code>start</code> for which <code>condition</code> holds, or
	 *         <code>NOT_FOUND</code> if none can be found
	 */
	public int scanBackward(int start, int bound, StopCondition condition) {
		if (bound == UNBOUND)
			bound = -1;

		Assert.isLegal(bound >= -1);
		Assert.isLegal(start <= fDocument.getLength());

		try {
			fPos = start - 1;
			while (fPos >= bound) {

				fChar = fDocument.getChar(fPos);
				if (condition.stop(fChar, fPos, false))
					return fPos;

				fPos = condition.nextPosition(fPos, false);
			}
		} catch (BadLocationException e) {
		}
		return NOT_FOUND;
	}

	/**
	 * Finds the smallest position in <code>fDocument</code> such that the
	 * position is &gt;= <code>position</code> and &lt; <code>bound</code>
	 * and <code>Character.isWhitespace(fDocument.getChar(pos))</code>
	 * evaluates to <code>false</code> and the position is in the default
	 * partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &gt; <code>position</code>,
	 *            or <code>UNBOUND</code>
	 * @return the smallest position of a non-whitespace character in [<code>position</code>,
	 *         <code>bound</code>) that resides in a Java partition, or
	 *         <code>NOT_FOUND</code> if none can be found
	 */
	public int findNonWhitespaceForward(int position, int bound) {
		return scanForward(position, bound, fNonWSDefaultPart);
	}

	/**
	 * Finds the smallest position in <code>fDocument</code> such that the
	 * position is &gt;= <code>position</code> and &lt; <code>bound</code>
	 * and <code>Character.isWhitespace(fDocument.getChar(pos))</code>
	 * evaluates to <code>false</code>.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &gt; <code>position</code>,
	 *            or <code>UNBOUND</code>
	 * @return the smallest position of a non-whitespace character in [<code>position</code>,
	 *         <code>bound</code>), or <code>NOT_FOUND</code> if none can
	 *         be found
	 */
	public int findNonWhitespaceForwardInAnyPartition(int position, int bound) {
		return scanForward(position, bound, fNonWS);
	}

	/**
	 * Finds the highest position in <code>fDocument</code> such that the
	 * position is &lt;= <code>position</code> and &gt; <code>bound</code>
	 * and <code>Character.isWhitespace(fDocument.getChar(pos))</code>
	 * evaluates to <code>false</code> and the position is in the default
	 * partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &lt; <code>position</code>,
	 *            or <code>UNBOUND</code>
	 * @return the highest position of a non-whitespace character in (<code>bound</code>,
	 *         <code>position</code>] that resides in a Java partition, or
	 *         <code>NOT_FOUND</code> if none can be found
	 */
	public int findNonWhitespaceBackward(int position, int bound) {
		return scanBackward(position, bound, fNonWSDefaultPart);
	}

	/**
	 * Finds the highest position in <code>fDocument</code> such that the
	 * position is &gt;= <code>position</code> and &lt; <code>bound</code>
	 * and <code>Character.isWhitespace(fDocument.getChar(pos))</code>
	 * evaluates to <code>false</code>.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be
	 *            considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider
	 *            any more, with <code>bound</code> &gt; <code>position</code>,
	 *            or <code>UNBOUND</code>
	 * @return the smallest position of a non-whitespace character in [<code>position</code>,
	 *         <code>bound</code>), or <code>NOT_FOUND</code> if none can
	 *         be found
	 */
	public int findNonWhitespaceBackwardInAnyPartition(int position, int bound) {
		return scanBackward(position, bound, fNonWS);
	}

	protected int getGenericToken(char ch) {
		switch (ch) {
		case LBRACE:
			return TokenLBRACE;
		case RBRACE:
			return TokenRBRACE;
		case LBRACKET:
			return TokenLBRACKET;
		case RBRACKET:
			return TokenRBRACKET;
		case LPAREN:
			return TokenLPAREN;
		case RPAREN:
			return TokenRPAREN;
		case SEMICOLON:
			return TokenSEMICOLON;
		case COLON:
			return TokenCOLON;
		case COMMA:
			return TokenCOMMA;
		case QUESTIONMARK:
			return TokenQUESTIONMARK;
		case EQUAL:
			return TokenEQUAL;
		case LANGLE:
			return TokenLESSTHAN;
		case RANGLE:
			return TokenGREATERTHAN;
		case BACKSLASH:
			return TokenBACKSLASH;
		case SLASH:
			return TokenSLASH;
		case PLUS:
			return TokenPLUS;
		case MINUS:
			return TokenMINUS;
		case STAR:
			return TokenSTAR;	
		default:
			return TokenOTHER;
		}
	}

	/**
	 * Returns the next token in forward direction, starting at
	 * <code>start</code>, and not extending further than <code>bound</code>.
	 * The return value is one of the constants defined in {@link Symbols}.
	 * After a call, {@link #getPosition()} will return the position just after
	 * the scanned token (i.e. the next position that will be scanned).
	 * 
	 * @param start
	 *            the first character position in the document to consider
	 * @param bound
	 *            the first position not to consider any more
	 * @return a constant from {@link Symbols} describing the next token
	 */
	public int nextToken(int start, int bound) {
		int pos = scanForward(start, bound, fNonWSDefaultPart);
		if (pos == NOT_FOUND)
			return TokenEOF;

		fPos++;

		int token = getGenericToken(fChar);
		if (token != TokenOTHER)
			return token;

		// else
		if (isValidIdentifierPart(fChar)) {
			// assume an identifier or keyword
			int from = pos, to;
			pos = findNonIdentifierForward(pos, bound);
			if (pos == NOT_FOUND)
				to = bound == UNBOUND ? fDocument.getLength() : bound;
			else
				to = pos;

			return getToken(from, to);
		} else {
			// operators, number literals etc
			return TokenOTHER;
		}
	}

	/**
	 * Returns the next token in backward direction, starting at
	 * <code>start</code>, and not extending further than <code>bound</code>.
	 * The return value is one of the constants defined in {@link Symbols}.
	 * After a call, {@link #getPosition()} will return the position just before
	 * the scanned token starts (i.e. the next position that will be scanned).
	 * 
	 * @param start
	 *            the first character position in the document to consider
	 * @param bound
	 *            the first position not to consider any more
	 * @return a constant from {@link Symbols} describing the previous token
	 */
	public int previousToken(int start, int bound) {
		int pos = scanBackward(start, bound, fNonWSDefaultPart);
		if (pos == NOT_FOUND)
			return TokenEOF;

		fPos--;

		int token = getGenericToken(fChar);
		if (token != TokenOTHER)
			return token;

		// else
		if (isValidIdentifierPart(fChar)) {
			// assume an ident or keyword
			int from, to = pos + 1;
			pos = findNonIdentifierBackward(pos, bound);
			if (pos == NOT_FOUND)
				from = bound == UNBOUND ? 0 : bound;
			else
				from = pos + 1;

			return getToken(from, to);
		} else {
			// operators, number literals etc
			return TokenOTHER;
		}
	}

	private int getToken(int from, int to) {
		String identOrKeyword;
		try {
			identOrKeyword = fDocument.get(from, to - from);
		} catch (BadLocationException e) {
			return TokenEOF;
		}

		return getToken(from, identOrKeyword);
	}

	public int findNonIdentifierForward(int offset, int bound) {
		return scanForward(offset, bound, fNonIdentifier);
	}

	public int findNonIdentifierBackward(int offset, int bound) {
		return scanBackward(offset, bound, fNonIdentifier);
	}

	public int findPrecedingNotEmptyLine(int offset) {
		try {
			int notEmptyPositioin = findNonWhitespaceBackward(offset, UNBOUND);
			if (notEmptyPositioin != NOT_FOUND) {
				return fDocument.getLineInformationOfOffset(notEmptyPositioin)
						.getOffset();
			}
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
		return 0;
	}
	
	/**
	 * Returns the position of the closing peer character (forward search). Any scopes introduced by opening peers
	 * are skipped. All peers accounted for must reside in the default partition.
	 *
	 * <p>Note that <code>start</code> must not point to the opening peer, but to the first
	 * character being searched.</p>
	 *
	 * @param start the start position
	 * @param openingPeer the opening peer character (e.g. '{')
	 * @param closingPeer the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findClosingPeer(int start, final char openingPeer, final char closingPeer) {
		return findClosingPeer(start, UNBOUND, openingPeer, closingPeer);
	}

	/**
	 * Returns the position of the closing peer character (forward search). Any scopes introduced by opening peers
	 * are skipped. All peers accounted for must reside in the default partition.
	 *
	 * <p>Note that <code>start</code> must not point to the opening peer, but to the first
	 * character being searched.</p>
	 *
	 * @param start the start position
	 * @param bound the bound
	 * @param openingPeer the opening peer character (e.g. '{')
	 * @param closingPeer the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findClosingPeer(int start, int bound, final char openingPeer, final char closingPeer) {
		Assert.isLegal(start >= 0);

		try {
			CharacterMatch match= new CharacterMatch(new char[] {openingPeer, closingPeer});
			int depth= 1;
			start -= 1;
			while (true) {
				start= scanForward(start + 1, bound, match);
				if (start == NOT_FOUND)
					return NOT_FOUND;

				if (fDocument.getChar(start) == openingPeer)
					depth++;
				else
					depth--;

				if (depth == 0)
					return start;
			}

		} catch (BadLocationException e) {
			return NOT_FOUND;
		}
	}

	/**
	 * Returns the position of the opening peer character (backward search). Any scopes introduced by closing peers
	 * are skipped. All peers accounted for must reside in the default partition.
	 *
	 * <p>Note that <code>start</code> must not point to the closing peer, but to the first
	 * character being searched.</p>
	 *
	 * @param start the start position
	 * @param openingPeer the opening peer character (e.g. '{')
	 * @param closingPeer the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findOpeningPeer(int start, char openingPeer, char closingPeer) {
		return findOpeningPeer(start, UNBOUND, openingPeer, closingPeer);
	}

	/**
	 * Returns the position of the opening peer character (backward search). Any scopes introduced by closing peers
	 * are skipped. All peers accounted for must reside in the default partition.
	 *
	 * <p>Note that <code>start</code> must not point to the closing peer, but to the first
	 * character being searched.</p>
	 *
	 * @param start the start position
	 * @param bound the bound
	 * @param openingPeer the opening peer character (e.g. '{')
	 * @param closingPeer the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findOpeningPeer(int start, int bound, char openingPeer, char closingPeer) {
		Assert.isLegal(start <= fDocument.getLength());

		try {
			final CharacterMatch match= new CharacterMatch(new char[] {openingPeer, closingPeer});
			int depth= 1;
			start += 1;
			while (true) {
				start= scanBackward(start - 1, bound, match);
				if (start == NOT_FOUND)
					return NOT_FOUND;

				if (fDocument.getChar(start) == closingPeer)
					depth++;
				else
					depth--;

				if (depth == 0)
					return start;
			}

		} catch (BadLocationException e) {
			return NOT_FOUND;
		}
	}

	public IRegion findWordAt(int offset) {
		try {			
			IRegion line = fDocument.getLineInformationOfOffset(offset);
			int start = findNonIdentifierBackward(offset, line.getOffset());
			int end = findNonIdentifierForward(offset, line.getOffset() + line.getLength());
			if (start == NOT_FOUND)
				start = line.getOffset();
			else 
				start += 1;
			
			if (end == NOT_FOUND)
				end = line.getOffset() + line.getLength();
			
			int length = end - start;
			if (length > 0)
				return new Region(start, length);
			
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
		return null;
	}

	/**
	 * Returns one of the keyword constants or <code>TokenIDENT</code> for a
	 * scanned identifier.
	 * 
	 * @param s
	 *            a scanned identifier
	 * @return one of the constants defined in {@link ISymbols}
	 */
	public abstract int getToken(int position, String s);

	/**
	 * @return true if symbol is valid identifier part
	 */
	protected boolean isValidIdentifierPart(char symbol) {
		return Character.isJavaIdentifierPart(symbol);
	}
}

/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.internal.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * A scanner to parse block tokens. 
 * The blocks are specified by pairs of characters (must be one char in length each)
 * The scanning is partition aware, it only parse partitions of a given type.
 */
public class BlockHeuristicsScannner extends LangHeuristicScanner {
	
	public static final class BlockTokenRule {
		public final char open;
		public final char close;
		public BlockTokenRule(char open, char close) {
			this.open = open;
			this.close = close;
		}
	}
	
	protected final BlockTokenRule[] blockRules;

	public BlockHeuristicsScannner(IDocument document, BlockTokenRule... blockRules) {
		super(document, "TODO"); // partitinioning
		this.blockRules = blockRules;
	}

	protected final int readPreviousToken() throws BadLocationException {
		return token = previousToken();
	}
	
	protected final int previousToken() throws BadLocationException {
		if(pos == limitPos) {
			return TOKEN_EOF;
		} else {
			pos--;
			return getSourceChar(pos);
		}
	}
	
	protected final int readNextToken() throws BadLocationException {
		return token = nextToken();
	}
	
	protected final int nextToken() throws BadLocationException {
		if(pos == limitPos) {
			return TOKEN_EOF;
		} else {
			pos++;
			return getSourceChar(pos);
		}
	}
	
	public char getClosingPeer(char openChar) {
		for (int i = 0; i < blockRules.length; i++) {
			BlockTokenRule blockRule = blockRules[i];
			if(blockRule.open == openChar){
				return blockRule.close;
			}
		}
		throw assertFail();
	}
	
	/*-------------------*/
	
	public static class BlockBalanceResult {
		public int unbalancedOpens = 0;
		public int unbalancedCloses = 0;
		public int rightmostUnbalancedBlockCloseOffset = -1;
		public int rightmostUnbalancedBlockOpenOffset = -1;
	}
	
	
	/** Calculate the block balance in given range. */
	protected BlockBalanceResult calculateBlockBalances(int beginPos, int endPos) throws BadLocationException {
		// Calculate backwards
		setPosition(endPos);
		limitPos = beginPos;
		// Ideally we would fully parse the code to figure the delta.
		// But ATM we just estimate using number of blocks
		BlockBalanceResult result = new BlockBalanceResult();
		
		while(readPreviousToken() != TOKEN_EOF) {
			for (int i = 0; i < blockRules.length; i++) {
				BlockTokenRule blockRule = blockRules[i];
				
				if(token == blockRule.close) {

					int blockCloseOffset = getPosition();
					
					int balance = scanToBlockStart(i); // do a subscan
					if(balance < 0) {
						// block start not found
						result.unbalancedCloses = balance;
						result.rightmostUnbalancedBlockCloseOffset = blockCloseOffset;
						return result;
					}
					break;
				} 
				if(token == blockRule.open) {
					result.unbalancedOpens++;
					
					if(result.rightmostUnbalancedBlockOpenOffset == -1) {
						result.rightmostUnbalancedBlockOpenOffset = getPosition();
					}
					break;
				}
			}
		}
		return result;
	}
	
	/** Scans in search of a block open.
	 * Stops on EOF, or when block open is found (balance is 0)
	 * @return 0 if block open token was found (even if created to syntax correct), 
	 * or a count (balance) of how many blocks were left open.
	 */
	protected int scanToBlockStart() throws BadLocationException {
		return scanToBlockStart(0);
	}
	
	protected int scanToBlockStart(int expectedRuleIx) throws BadLocationException {
		while(readPreviousToken() != TOKEN_EOF) {
			for (int i = expectedRuleIx; i < blockRules.length; i++) {
				BlockTokenRule blockRule = blockRules[i];
				
				if(token == blockRule.close) {
					int balance = scanToBlockStart(i);
					if(balance < 0){
						return balance + 1;
					}
					break;
				} 
				if(token == blockRule.open) {
					if(i == expectedRuleIx){
						return 0; 
					} else {
						// syntax error
						if(i > expectedRuleIx) {
							// ignore token
						} else {
							// stronger rule takes precedence, assume syntax correction
						}
					}
					break;
				} 
			}
		}
		return 1; // Balance is -1 if we reached the end without finding peer
	}
	
	protected int scanToBlockEnd() throws BadLocationException {
		return scanToBlockEnd(0);
	}
	
	protected int scanToBlockEnd(int expectedRuleIx) throws BadLocationException {
		while(readNextToken() != TOKEN_EOF) {
			for (int i = 0; i < blockRules.length; i++) {
				BlockTokenRule blockRule = blockRules[i];
				
				if(token == blockRule.open) {
					int balance = scanToBlockEnd(i);
					if(balance < 0){
						return balance - 1;
					}
					break;
				} 
				if(token == blockRule.close) {
					if(i == expectedRuleIx){
						return 0; 
					} else {
						// syntax error
						if(i > expectedRuleIx) {
							// ignore token
						} else {
							// stronger rule takes precedence, assume syntax correction
						}
					}
					break;
				} 
			}
		}
		return -1;
	}
	
	/** Finds the offset where starts the blocks whose end token is at given blockCloseOffset */
	protected int findBlockStart(int blockCloseOffset) throws BadLocationException {
		setPosition(blockCloseOffset);
		limitPos = 0;
		scanToBlockStart(); // XXX BUG here, find token
		return getPosition();
	}
	
	protected boolean isBlockClosed(int blockOpenOffset) throws BadLocationException {
		setPosition(blockOpenOffset);
		limitPos = document.getLength()-1;
		int balance = scanToBlockEnd(); // XXX BUG here, find token
		return balance == 0;
	}

}
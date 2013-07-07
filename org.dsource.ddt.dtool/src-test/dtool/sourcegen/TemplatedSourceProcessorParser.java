package dtool.sourcegen;

import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.misc.StringUtil;
import dtool.tests.SimpleParser;


public class TemplatedSourceProcessorParser {
	
	@SuppressWarnings("serial")
	public class TemplatedSourceException extends Exception {
		public int errorOffset;
		public TemplatedSourceException(int errorOffset) {
			this.errorOffset = errorOffset;
		}
	}

	protected String kMARKER;
	protected String[] kMARKER_array;

	protected ArrayList<TspElement> parseSplitCase(String source, String keyMarker) throws TemplatedSourceException {
		this.kMARKER = keyMarker;
		this.kMARKER_array = new String[]{ keyMarker };
		ArrayList<TspElement> sourceElements = parseSource(source);
		return sourceElements;
	}
	
	protected ArrayList<TspElement> parseSource(String unprocessedSource) throws TemplatedSourceException {
		ArrayList<TspElement> unprocessedSourceElements = new ArrayList<TspElement>(); 
		
		SimpleParser parser = new SimpleParser(unprocessedSource);
		while(true) {
			TspElement tspElem = parseElement(parser);
			if(tspElem == null) {
				break;
			}
			unprocessedSourceElements.add(tspElem);
		}
		return unprocessedSourceElements;
	}
	
	protected static abstract class TspElement {
		public static String DEFAULT_TYPE = "DEFAULT";
		public String getElementType() { return DEFAULT_TYPE; };
		public String getSource() { return null; };
	}
	
	public static class TspStringElement extends TspElement {
		public static final String RAW_TEXT = "TextElement";
		public final String producedText;
		public final String elemType;
		
		protected TspStringElement(String source) {
			this(source, RAW_TEXT);
		}
		protected TspStringElement(String producedText, String elemType) {
			this.producedText = assertNotNull(producedText);
			this.elemType = elemType;
		}
		@Override
		public String getElementType() {
			return elemType;
		}
		@Override
		public String getSource() {
			return producedText;
		}
	}
	
	protected TspElement parseElement(SimpleParser parser) throws TemplatedSourceException {
		return parseElementWithCustomStarts(parser, kMARKER_array);
	}
	
	protected TspElement parseElementWithCustomStarts(SimpleParser parser, String... tokenStarts)
		throws TemplatedSourceException {
		if(parser.lookaheadIsEOF()) {
			return null;
		}
		
		int alt = parser.consumeUntilAny(tokenStarts);
		final String string = parser.getLastConsumedString();
		
		if(!string.isEmpty()) {
			return new TspStringElement(string);
		}
		
		String tokenStart = tokenStarts[alt];
		parser.consume(tokenStart);
		
		if(!tokenStart.equals(kMARKER)) {
			return new TspStringElement(parser.getLastConsumedString(), tokenStart);
		}
		
		if(parser.tryConsume(kMARKER)) {
			return new TspStringElement(kMARKER);
		}
		for (String escapableTokenStart : tokenStarts) {
			if(parser.tryConsume(escapableTokenStart)) {
				return new TspStringElement(escapableTokenStart);
			}
		}
		if(parser.lookAhead() == '{' || parser.lookAhead() == '@') {
			return parseExpansionCommand(parser); 
		} else if(parser.lookAhead() == '?') {
			return parseIfElseExpansionCommand(parser); 
		} else if(Character.isJavaIdentifierStart(parser.lookAhead())) {
			return parseMetadataElement(parser);
		} if(parser.lookAhead() == ':') {
			return parseCommandElement(parser); 
		}
		
		reportError(parser.getSourcePosition());
		return null;
	}
	
	protected void reportError(final int offset) throws TemplatedSourceException {
		throw new TemplatedSourceException(offset);
	}
	
	protected void checkError(boolean condition, SimpleParser parser) throws TemplatedSourceException {
		if(condition) {
			reportError(parser.getSourcePosition());
		}
	}
	
	public static class TspExpansionElement extends TspElement {
		public final String expansionId; 
		public final String pairedExpansionId; 
		public final ArrayList<Argument> arguments; 
		public final boolean dontOuputSource;
		public final boolean anonymousExpansion;
		
		public TspExpansionElement(String expansionId, String pairedExpansionId, ArrayList<Argument> arguments, 
			boolean anonymousExpansion, boolean dontOuputSource) {
			this.expansionId = expansionId;
			this.pairedExpansionId = pairedExpansionId;
			this.arguments = arguments;
			this.anonymousExpansion = anonymousExpansion;
			this.dontOuputSource = dontOuputSource;
		}
		
		@Override
		public String toString() {
			return "EXPANSION["+(anonymousExpansion?"^":"")+(dontOuputSource?"!":"")+
				StringUtil.nullAsEmpty(expansionId)+
				(pairedExpansionId == null ? "" : "("+pairedExpansionId+")")+
				(arguments == null ? "" : "{"+StringUtil.collToString(arguments, "#,#")+"}")+
				"]";
		}
	}
	
	@SuppressWarnings("serial")
	protected class Argument extends ArrayList<TspElement> { 
		@Override
		public String toString() {
			return "ARGUMENT["+StringUtil.collToString(this, "")+"]";
		}
	}
	
	protected TspElement parseExpansionCommand(SimpleParser parser) throws TemplatedSourceException {
		assertTrue(parser.lookAhead() == '{' || parser.lookAhead() == '@');
		
		String expansionId = null;
		boolean defineOnly = false; 
		boolean anonymousExpansion = false;
		
		if(parser.tryConsume("@")) {
			if(parser.tryConsume("^")) {
				anonymousExpansion = true;
			}
			expansionId = emptyToNull(parser.consumeAlphaNumericUS(false));
			if(anonymousExpansion) {
				checkError(expansionId == null, parser);
			} 
			if(parser.tryConsume("!")) {
				checkError(expansionId == null, parser); // No test case for this
				checkError(anonymousExpansion, parser);
				defineOnly = true;
			}
		}
		
		ArrayList<Argument> arguments = null;
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt != -1) {
			String closeDelim = CLOSE_DELIMS[alt];
			arguments = parseArgumentList(parser, closeDelim);
		}
		checkError(defineOnly && arguments == null, parser);
		
		String pairedExpansionId = null;
		if(parser.tryConsume("(")) {
			pairedExpansionId = consumeDelimitedId(parser, ")");
		}
		
		// This is just an optional separator, useful when expansion only has an id, like: #@EXP•BLA
		parser.tryConsume("•"); 
		
		checkError(expansionId == null && pairedExpansionId == null && arguments == null, parser);
		return new TspExpansionElement(expansionId, pairedExpansionId, arguments, anonymousExpansion, defineOnly);
	}
	
	protected String consumeDelimitedId(SimpleParser parser, String closeDelim) throws TemplatedSourceException {
		String pairedExpansionId = emptyToNull(parser.consumeAlphaNumericUS(false));
		checkError(pairedExpansionId == null, parser);
		checkError(parser.tryConsume(closeDelim) == false, parser);
		return pairedExpansionId;
	}
	
	protected ArrayList<Argument> parseArgumentList(SimpleParser parser, String closeDelim) 
		throws TemplatedSourceException {
		String argSep = closeDelim.equals("}") ? "," : "●"; 
		return parseArgumentList(parser, argSep, closeDelim, false);
	}
	
	protected ArrayList<Argument> parseArgumentList(
		SimpleParser parser, final String argSeparator, final String listEnd, boolean eofTerminates
	) throws TemplatedSourceException {
		assertNotNull(listEnd);
		assertTrue(!eofTerminates || argSeparator == null);
		
		final String listEndPrefix = "¤";
		final String argStart = "►";
		final String[] tokenStarts = eofTerminates ? 
			new String[] { argStart, listEnd, kMARKER } : 
			argSeparator != null ?
			new String[] { argStart, argSeparator, listEndPrefix, listEnd, kMARKER } :
			new String[] { argStart, listEndPrefix, listEnd, kMARKER };
		
		boolean ignoreLastArg = false;
		boolean argumentStartFound = false;
		
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		Argument argument = new Argument();
		while(true) {
			TspElement element = parseElementWithCustomStarts(parser, tokenStarts);
			
			if(element != null && element.getElementType() == listEnd) {
				break;
			}
			checkError(ignoreLastArg, parser);
			
			if(element == null) {
				checkError(!eofTerminates, parser);
				break;
			} 
			if(element.getElementType() == argStart) {
				checkError(argumentStartFound || !argumentIsWhiteSpaceOnly(argument), parser);
				argumentStartFound = true;
				
				argument = new Argument();
			} else if(element.getElementType() == listEndPrefix) {
				checkError(argumentStartFound || !argumentIsWhiteSpaceOnly(argument), parser);
				ignoreLastArg = true;
				argument = null;
			} else if(element.getElementType() == argSeparator) {
				arguments.add(argument);
				argumentStartFound = false;
				
				argument = new Argument();
			} else {
				argument.add(element);
			}
		}
		
		if(ignoreLastArg) {
			assertTrue(argument == null);
		} else {
			arguments.add(assertNotNull(argument));
		}
		return arguments;
	}
	
	protected boolean argumentIsWhiteSpaceOnly(Argument argument) {
		if(argument.size() != 0) {
			if(argument.size() == 1) {
				String argSource = argument.get(0).getSource();
				return(argSource != null && argSource.trim().isEmpty());
			} 
			return false;
		}
		return true;
	}
	
	public static class TspMetadataElement extends TspElement {
		public final String tag; 
		public final String value; 
		public final Argument associatedElements; 
		public final boolean outputSource;
		
		public TspMetadataElement(String tag, String value, Argument associatedElements, boolean outputSource) {
			this.tag = tag;
			this.value = value;
			this.associatedElements = associatedElements;
			this.outputSource = outputSource;
		}
	}
	
	protected static final String[] OPEN_DELIMS  = {"{","«","〈","《","「","『","【","〔","〖","〚" };
	protected static final String[] CLOSE_DELIMS = {"}","»","〉","》","」","』","】","〕","〗","〛"} ;
	
	protected TspMetadataElement parseMetadataElement(SimpleParser parser) throws TemplatedSourceException {
		String name = parser.consumeAlphaNumericUS(false);
		assertTrue(!name.isEmpty());
		
		String value = parser.tryConsume("(") ? consumeDelimitedString(parser, ")", false) : null;
		
		Argument sourceValue = null;
		boolean colonSyntaxConsumed = false;
		
		if(value == null && parser.lookAhead() == ':') {
			String id = SimpleParser.readAlphaNumericUS(parser.getSource(), parser.getSourcePosition()+1);
			if(id.length() != 0) {
				value = id;
				parser.consumeAmount(id.length() + 1);
				colonSyntaxConsumed = true;
			}
		}
		
		boolean outputSource = parser.tryConsume("¤") == false;
		
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt != -1) {
			String closeDelim = CLOSE_DELIMS[alt];
			sourceValue = parseArgument(parser, closeDelim, false);
		} else if(colonSyntaxConsumed == false && parser.tryConsume(":")) {
			//checkError(parser.tryConsumeNewlineRule() == false, parser);
			parser.tryConsumeNewlineRule();
			sourceValue = parseArgument(parser, "#:END:", true);
			outputSource = false;
		}
		checkError(outputSource == false && sourceValue == null, parser);
		
		return new TspMetadataElement(name, value, sourceValue, outputSource);
	}
	
	protected Argument parseArgument(SimpleParser parser, String listEnd, boolean eofTerminates)
		throws TemplatedSourceException {
		ArrayList<Argument> argumentList = parseArgumentList(parser, null, listEnd, eofTerminates);
		assertTrue(argumentList.size() == 1);
		return argumentList.get(0);
	}
	
	protected String consumeDelimitedString(SimpleParser parser, String closeSep, boolean eofTerminates) 
		throws TemplatedSourceException {
		StringBuilder value = new StringBuilder();
		
		final String[] alts = new String[]{closeSep, kMARKER};
		while(true) {
			int alt = parser.consumeUntilAny(alts);
			value.append(parser.getLastConsumedString());
			
			if(parser.lookaheadIsEOF()) {
				checkError(!eofTerminates, parser); // Unterminated
				break;
			} else if(alt == 0) {
				parser.consume(closeSep);
				break;
			} else if(alt == 1) {
				parser.consume(kMARKER);
				if(parser.tryConsume("#")) {
					value.append("#");
				} else if(parser.tryConsume(closeSep)) {
					value.append(closeSep);
				} else {
					reportError(parser.getSourcePosition()); // Invalid Escape
				}
			} else {
				assertFail();
			}
		}
		return value.toString();
	}
	
	protected static class TspCommandElement extends TspElement {
		
		public static final String DISCARD_CASE = "DISCARD_CASE";
		
		public final String name; 
		
		public TspCommandElement(String name) {
			this.name = name;
		}
	}
	
	protected TspCommandElement parseCommandElement(SimpleParser parser) throws TemplatedSourceException {
		parser.consume(":");
		String name = parser.consumeAlphaNumericUS(false);
		checkError(name.isEmpty(), parser);
		checkError(!name.equals(TspCommandElement.DISCARD_CASE), parser);
		
		return new TspCommandElement(name);
	}
	
	protected TspIfElseExpansionElement parseIfElseExpansionCommand(SimpleParser parser) 
		throws TemplatedSourceException {
		parser.consume("?");
		
		String mdConditionId = emptyToNull(parser.consumeAlphaNumericUS(false));
		checkError(mdConditionId == null, parser);
		
		boolean invert = parser.tryConsume("!");
		
		ArrayList<Argument> arguments = null;
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt == -1) {
			reportError(parser.getSourcePosition());
		} else {
			arguments = parseArgumentList(parser, CLOSE_DELIMS[alt]);
		}
		
		checkError(arguments.size() > 2, parser);
		
		Argument argElse = arguments.size() == 1 ? null: arguments.get(1);
		return new TspIfElseExpansionElement(mdConditionId, invert, arguments.get(0), argElse);
	}
	
	public static class TspIfElseExpansionElement extends TspElement {
		public final String mdConditionId; 
		public final boolean invert;
		public final Argument argThen; 
		public final Argument argElse;
		
		public TspIfElseExpansionElement(String mdConditionId, boolean invert, Argument argThen, Argument argElse) {
			this.mdConditionId = mdConditionId;
			this.invert = invert;
			this.argThen = argThen;
			this.argElse = argElse;
		}
		
		@Override
		public String toString() {
			return "IF?【"+ StringUtil.nullAsEmpty(mdConditionId)+"{"+argThen+","+argElse+"}"+"】";
		}
	}
	
}

package dtool.parser;

import dtool.ast.ASTNode;
import dtool.parser.DeeParser.DeeParserState;
import dtool.parser.LexElement.MissingLexElement;

public class AbstractParserRule extends DeeParser_Statements {
	
	protected final DeeParser parser;
	
	public AbstractParserRule(DeeParser parser) {
		this.parser = parser;
	}
	
	@Override
	protected DeeParser thisParser() {
		return parser;
	}
	
	@Override
	public String getSource() {
		return parser.getSource();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		parser.setEnabled(enabled);
	}
	
	@Override
	public boolean isEnabled() {
		return parser.isEnabled();
	}
	
	@Override
	public LexElement lookAheadElement(int laIndex) {
		return parser.lookAheadElement(laIndex);
	}
	
	@Override
	public int getLexPosition() {
		return parser.getLexPosition();
	}
	
	@Override
	public LexElement consumeLookAhead() {
		return parser.consumeLookAhead();
	}
	
	@Override
	public MissingLexElement consumeSubChannelTokens() {
		return parser.consumeSubChannelTokens();
	}
	
	@Override
	public LexElement lastLexElement() {
		return parser.lastLexElement();
	}
	
	@Override
	protected void nodeConcluded(ASTNode node) {
		parser.nodeConcluded(node);
	}
	
	public static abstract class AbstractDecidingParserRule<T> extends AbstractParserRule {
		
		protected DeeParserState savedParserState;
		
		public AbstractDecidingParserRule(DeeParser parser) {
			super(parser);
		}
		
		public abstract T parse(ParseHelper parse);
		
		public T parseDeciderMode(ParseHelper parse) {
			savedParserState = parser.enterBacktrackableMode();
			return parse(parse);
		}
		
		public boolean isDeciderMode() {
			return savedParserState != null;
		}
		
		public void acceptDeciderResult() {
			savedParserState = null;
		}
		
		public void discardDeciderResult() {
			parser.restoreOriginalState(savedParserState);
		}
		
	}
	
}
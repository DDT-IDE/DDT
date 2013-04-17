package dtool.parser;

import dtool.parser.LexElement.MissingLexElement;

public class AbstractParserRule extends DeeParser_Decls {
	
	protected final DeeParser parser;
	
	public AbstractParserRule(DeeParser parser) {
		this.parser = parser;
		this.pendingMissingTokenErrors = parser.pendingMissingTokenErrors;
	}
	
	@Override
	protected DeeParser getDeeParser() {
		return parser;
	}
	
	@Override
	protected void submitError(ParserError error) {
		parser.submitError(error);
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
	public String getSource() {
		return parser.getSource();
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
	public LexElement lastLexElement() {
		return parser.lastLexElement();
	}
	
	@Override
	public LexElement consumeInput() {
		return parser.consumeInput();
	}
	
	@Override
	public MissingLexElement consumeSubChannelTokens() {
		return parser.consumeSubChannelTokens();
	}
	
}

package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;

import dtool.parser.LexElement.MissingLexElement;

public class AbstractParserRule extends DeeParser_Decls {
	
	protected final DeeParser parser;
	
	public AbstractParserRule(DeeParser parser) {
		this.parser = parser;
		this.pendingMissingTokenErrors = parser.pendingMissingTokenErrors;
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
	public LexElement consumeInput() {
		return parser.consumeInput();
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
	protected void submitError(ParserError error) {
		parser.submitError(error);
	}
	
	public static abstract class AbstractDecidingParserRule<T> extends AbstractParserRule {
		
		protected ArrayList<ParserError> ruleErrors = null;
		protected LexElementSource savedState = null;
		
		public AbstractDecidingParserRule(DeeParser parser) {
			super(parser);
		}
		
		public abstract T parse();
		
		public T parseDeciderMode() {
			ruleErrors = new ArrayList<>();
			savedState = parser.getEnabledLexSource().saveState();
			return parse();
		}
		
		public boolean isDeciderMode() {
			return ruleErrors != null;
		}
		
		@Override
		protected void submitError(ParserError error) {
			if(ruleErrors == null) {
				super.submitError(error);
			} else {
				ruleErrors.add(error);
			}
		}
		
		public void acceptDeciderResult() {
			assertNotNull(ruleErrors);
			for (ParserError error : ruleErrors) {
				parser.submitError(error);
			}
			ruleErrors = null;
		}
		
		public void discardDeciderResult() {
			parser.getEnabledLexSource().resetState(savedState);
		}
		
	}
	
}
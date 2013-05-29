package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;


public abstract class DeeParser_Common extends AbstractParser {
	
	/* ----------------------------------------------------------------- */
	
	public DeeTokens lookAheadGrouped() {
		return lookAheadToken().type.getGroupingToken();
	}
	
	public String idTokenToString(BaseLexElement id) {
		return id.isMissingElement() ? null : id.getSourceValue();
	}
	
	public static boolean isCloseBracketChar(DeeTokens token) {
		return 
			token == DeeTokens.CLOSE_BRACE || 
			token == DeeTokens.CLOSE_BRACKET || 
			token == DeeTokens.CLOSE_PARENS;
	}
	
	/* -----------------------  List parse helper  ----------------------- */
	
	public abstract class ElementListParseHelper<T extends ASTNode> extends ParseHelper {
		
		public ArrayView<T> members; 
		public boolean hasEndingSep = false;
		
		public ElementListParseHelper() {
			nodeStart = -1;
		}
		
		public void parseList(DeeTokens tkOPEN, DeeTokens tkSEP, DeeTokens tkCLOSE) {
			parseList(true, tkOPEN, tkSEP, tkCLOSE, true);
		}
		public void parseList(boolean required, DeeTokens tkOPEN, DeeTokens tkSEP, DeeTokens tkCLOSE, 
			boolean canHaveEndingSep) {
			ParseHelper parse = this;
			if(parse.consume(tkOPEN, !required, required) == false) {
				return;
			}
			setStartPosition(lastLexElement().getStartPos());
			
			ArrayList<T> membersList = new ArrayList<T>();
			
			boolean requireElement = false;
			while(true) {
				if(requireElement == false && tryConsume(tkCLOSE))
					break;
				
				T entry = parseElement(requireElement || lookAhead() == tkSEP);
				if(entry != null) {
					membersList.add(entry);
					hasEndingSep = false;
				}
				
				if(tryConsume(tkSEP)) {
					hasEndingSep = true;
					requireElement = !canHaveEndingSep;
					continue;
				} else {
					parse.consumeRequired(tkCLOSE);
					break;
				}
			}
			members = arrayView(membersList);
		}
		
		protected abstract T parseElement(boolean createMissing);
		
	}
	
	public abstract class SimpleListParseHelper<T extends ASTNode> {
		
		public ArrayView<T> members; 
		
		public ArrayView<T> parseSimpleList(boolean canBeEmpty, DeeTokens tkSEP) {
			ArrayList<T> membersList = new ArrayList<T>();
			
			do {
				T entry = parseElement(!canBeEmpty || lookAhead() == tkSEP);
				if(entry != null) {
					membersList.add(entry);
				}
				canBeEmpty = false; // after first element next elements become required
			} while(tryConsume(tkSEP));
			
			members = arrayView(membersList);
			return members;
		}
		
		public ArrayView<T> parseSimpleListWithClose(ParseHelper parse, boolean canBeEmpty, DeeTokens tkSEP, 
			DeeTokens tkCLOSE) {
			parseSimpleList(canBeEmpty, tkSEP);
			
			parse.consumeRequired(tkCLOSE);
			return members;
		}
		
		protected abstract T parseElement(boolean createMissing);
	}
	
	/* ----------------------------------------------------------------- */
	
	public static ProtoDefSymbol defSymbol(BaseLexElement id) {
		// possible bug here, should be srEffectiveRange
		return new ProtoDefSymbol(id.getSourceValue(), id.getSourceRange(), id.getError());
	}
	
	public ProtoDefSymbol parseMissingDefIdNoError() {
		return new ProtoDefSymbol("", srAt(getLexPosition()), null);
	}
	
	public final ProtoDefSymbol parseDefId() {
		BaseLexElement defId = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return defSymbol(defId);
	}
	
	public final ProtoDefSymbol nullIdToMissingDefId(ProtoDefSymbol defId) {
		if(defId == null) {
			return defSymbol(consumeSubChannelTokens());
		}
		return defId;
	}
	
	public static boolean couldHaveBeenParsedAsId(Reference ref) {
		return ref instanceof RefIdentifier;
	}
	
	public static ProtoDefSymbol convertRefIdToDef(Reference ref) {
		assertTrue(couldHaveBeenParsedAsId(ref));
		RefIdentifier refId = (RefIdentifier) ref;
		ParserError error = refId.name != null ? null : refId.getData().getNodeErrors().iterator().next();
		return new ProtoDefSymbol(refId.name == null ? "" : refId.name, ref.getSourceRange(), error);
	}
	
	public final Symbol parseIdSymbol() {
		BaseLexElement token = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return createIdSymbol(token);
	}
	public final Symbol createIdSymbol(BaseLexElement token) {
		return conclude(token.getError(), srOf(token, new Symbol(token.getSourceValue())));
	}
	
	/* ----------------------------------------------------------------- */
	
	protected class TypeId_or_Id_RuleFragment {
		
		public Reference type = null;
		public ProtoDefSymbol defId = null;
		
		public void parseRuleFragment(ParseHelper parse, boolean createMissing) {
			type = parse.checkResult(thisParser().parseTypeReference());
			
			if(lookAhead() == DeeTokens.IDENTIFIER) {
				missingDefIdParse();
			} else if(couldHaveBeenParsedAsId(type)) {
				assertTrue(parse.ruleBroken == false);
				singleIdReparse();
			} else {
				if(type == null && !createMissing) {
					return;
				}
				if(parse.ruleBroken) {
					defId = parseMissingDefIdNoError();
				} else {
					missingDefIdParse();
				}
			}
			
			if(parse.nodeStart == -1) {
				parse.setStartPosition(type != null ? type.getStartPos() : defId.getStartPos());
			}
		}
		
		protected void singleIdReparse() {
			defId = convertRefIdToDef(type);
			type = null;
		}
		
		protected void missingDefIdParse() {
			defId = parseDefId(); //This will create a full missing defId, with error
		}
		
	}
	
	protected final class TypeId_RuleFragment extends TypeId_or_Id_RuleFragment {
		
		@Override
		public void singleIdReparse() {
			defId = parseDefId();
		}
		
		@Override
		public void missingDefIdParse() {
			if(type == null) {
				type = thisParser().parseMissingTypeReference(true);
				defId = defSymbol(consumeSubChannelTokens());
			} else {
				super.missingDefIdParse();
			}
		}
	}
	
}
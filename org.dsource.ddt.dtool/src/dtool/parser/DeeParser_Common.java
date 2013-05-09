package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;
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
			if(!tryConsume(tkOPEN)) {
				parse.ruleBroken = required;
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
		
		public void parseSimpleList(boolean canBeEmpty, DeeTokens tkSEP) {
			ArrayList<T> membersList = new ArrayList<T>();
			
			do {
				T entry = parseElement(!canBeEmpty || lookAhead() == tkSEP);
				if(entry != null) {
					membersList.add(entry);
				}
				canBeEmpty = false; // after first element next elements become require
			} while(tryConsume(tkSEP));
			
			members = arrayView(membersList);
		}
		
		protected abstract T parseElement(boolean createMissing);
		
	}
	
	/* ----------------------------------------------------------------- */
	
	public static ProtoDefSymbol defSymbol(BaseLexElement id) {
		// possible bug here, should be srEffectiveRange
		return new ProtoDefSymbol(id.getSourceValue(), id.getSourceRange(), id.getError());
	}
	
	public final ProtoDefSymbol parseDefId() {
		BaseLexElement defId = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return defSymbol(defId);
	}
	
	public final ProtoDefSymbol nullIdToMissingDefId(ProtoDefSymbol defId) {
		if(defId == null) {
			return defSymbol(createExpectedToken(DeeTokens.IDENTIFIER));
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
	
	/* ----------------------------------------------------------------- */
	
	protected class TypeId_or_Id_PatternParse {
		
		public Reference type = null;
		public ProtoDefSymbol defId = null;
		
		public void parsePattern(ParseHelper parse, boolean createMissing) {
			type = parse.checkResult(thisParser().parseTypeReference());
			
			if(lookAhead() == DeeTokens.IDENTIFIER) {
				defId = parseDefId();
			} else if(couldHaveBeenParsedAsId(type)) {
				defId = convertRefIdToDef(type);
				type = null;
			} else {
				if(type == null && !createMissing) {
					return;
				}
				if(parse.ruleBroken) {
					defId = new ProtoDefSymbol("", srAt(getLexPosition()), null);
				} else {
					defId = parseDefId(); //This will create a full missing defId, with error
				}
			}
			
			if(parse.nodeStart == -1) {
				parse.setStartPosition(type != null ? type.getStartPos() : defId.getStartPos());
			}
		}
		
	}
	
}
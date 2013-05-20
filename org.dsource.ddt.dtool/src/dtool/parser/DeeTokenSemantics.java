package dtool.parser;

import java.util.List;
import java.util.Map;

import melnorme.utilbox.misc.Pair;

import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.NewUtils;

public class DeeTokenSemantics {
	
	public static void checkTokenErrors(Token token, List<ParserError> lexerErrors) {
		if(token.type == DeeTokens.INVALID_TOKEN) {
			lexerErrors.add(createError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, token, null));
			return;
		}
		
		if(token.getError() != null) {
			lexerErrors.add(createError(ParserErrorTypes.MALFORMED_TOKEN, token, token.getError()));
			return;
		}
		
		// Check token content validity  TODO: strings, unicode escapes, HTML entities, etc.
		switch (token.type) {
		case CHARACTER:
			if(token.source.length() > 3) {
				lexerErrors.add(createError(ParserErrorTypes.MALFORMED_TOKEN, token, 
					LexerErrorTypes.CHAR_LITERAL_SIZE_GREATER_THAN_ONE));
			}
			break;
		default:
			break;
		}
	}
	
	public static ParserError createError(ParserErrorTypes errorType, Token token, Object msgData) {
		return new ParserError(errorType, token.getSourceRange(), token.getSourceValue(), msgData);
	}
	
	public static Protection getProtectionFromToken(DeeTokens token) {
		switch(token) {
		case KW_PRIVATE: return Protection.PRIVATE;
		case KW_PACKAGE: return Protection.PACKAGE;
		case KW_PROTECTED: return Protection.PROTECTED;
		case KW_PUBLIC: return Protection.PUBLIC;
		case KW_EXPORT: return Protection.EXPORT;
		default: return null;
		}
	}
	
	protected static final Map<String, Boolean> traitsIdMapper = NewUtils.initMap(
		Pair.create("isAbstractClass", true),
		Pair.create("isArithmetic", true),
		Pair.create("isAssociativeArray", true),
		Pair.create("isFinalClass", true),
		Pair.create("isPOD", true),
		Pair.create("isFloating", true),
		Pair.create("isIntegral", true),
		Pair.create("isScalar", true),
		Pair.create("isStaticArray", true),
		Pair.create("isUnsigned", true),
		Pair.create("isVirtualFunction", true),
		Pair.create("isVirtualMethod", true),
		Pair.create("isAbstractFunction", true),
		Pair.create("isFinalFunction", true),
		Pair.create("isStaticFunction", true),
		Pair.create("isRef", true),
		Pair.create("isOut", true),
		Pair.create("isLazy", true),
		Pair.create("hasMember", true),
		Pair.create("identifier", true),
		Pair.create("getAttributes", true),
		Pair.create("getMember", true),
		Pair.create("getOverloads", true),
		Pair.create("getProtection", true),
		Pair.create("getVirtualFunctions", true),
		Pair.create("getVirtualMethods", true),
		Pair.create("parent", true),
		Pair.create("classInstanceSize", true),
		Pair.create("allMembers", true),
		Pair.create("derivedMembers", true),
		Pair.create("isSame", true),
		Pair.create("compiles", true)
	);
	
	public static ParserError checkTraitsId(Token traitsId) {
		if(traitsIdMapper.get(traitsId.getSourceValue()) != null) {
			return null;
		}
		return createError(ParserErrorTypes.INVALID_TRAITS_ID, traitsId, null);
	}
	
}
package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoNode;
import dtool.parser.DeeTokens;
import dtool.util.ArrayView;

public interface IFunctionParameter extends IASTNeoNode {
	
	public static enum FunctionParamAttribKinds {
		AUTO(DeeTokens.KW_AUTO),
		
		//Disabled since these are parsed as type modifiers
//		CONST(DeeTokens.KW_CONST), 
//		IMMUTABLE(DeeTokens.KW_IMMUTABLE), 
//		INOUT(DeeTokens.KW_INOUT), 
//		SHARED(DeeTokens.KW_SHARED),
		
		FINAL(DeeTokens.KW_FINAL),
		IN(DeeTokens.KW_IN),
		LAZY(DeeTokens.KW_LAZY),
		OUT(DeeTokens.KW_OUT),
		REF(DeeTokens.KW_REF),
		SCOPE(DeeTokens.KW_SCOPE),
		;
		public final DeeTokens token;

		private FunctionParamAttribKinds(DeeTokens token) {
			this.token = token;
		}
		
		public String getSourceValue() {
			return token.getSourceValue();
		}
		
		public static FunctionParamAttribKinds fromToken(DeeTokens token) {
			switch (token) {
			case KW_AUTO: return AUTO;
//			case KW_CONST: return CONST;
//			case KW_IMMUTABLE: return IMMUTABLE;
//			case KW_INOUT: return INOUT;
//			case KW_SHARED: return SHARED;
			case KW_FINAL: return FINAL;
			case KW_IN: return IN;
			case KW_LAZY: return LAZY;
			case KW_OUT: return OUT;
			case KW_REF: return REF;
			case KW_SCOPE: return SCOPE;
			default: return null;
			}
		}
		static { 
			for (FunctionParamAttribKinds attrib : values()) {
				assertTrue(FunctionParamAttribKinds.fromToken(attrib.token) == attrib);
			}
		}
	}
	
	public static class FnParameterAttributes {
		
		public final ArrayView<FunctionParamAttribKinds> attribs;
		
		public FnParameterAttributes(ArrayView<FunctionParamAttribKinds> attribList) {
			attribs = assertNotNull_(attribList);
		}
		
		public void toStringAsCode(ASTCodePrinter cp) {
			for (FunctionParamAttribKinds attribKind : attribs) {
				cp.appendStrings(attribKind.getSourceValue(), " ");
			}
		}
		
		public static FnParameterAttributes create(ArrayView<FunctionParamAttribKinds> attribList) {
			return attribList == null ? EMPTY_FN_PARAMS : new FnParameterAttributes(attribList);
		}
		
		public static final FnParameterAttributes EMPTY_FN_PARAMS = new FnParameterAttributes(
			ArrayView.<FunctionParamAttribKinds>create(new FunctionParamAttribKinds[0]));
		
	}
	
	boolean isVariadic();
	
	/** Basicly, returns the type string for this function parameter. */
	String toStringAsFunctionSimpleSignaturePart();
	
	/** A String representation for the initializer*/
	String toStringInitializer();
	
	/** Returns a string to be used as part of function full signature.  */
	String toStringAsFunctionSignaturePart();

}
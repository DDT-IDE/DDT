package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.ISourceRepresentation;
import dtool.parser.DeeTokens;

public class AttribBasic extends Attribute {
	
	public final AttributeKinds declAttrib;
	
	public AttribBasic(AttributeKinds declAttrib) {
		this.declAttrib = declAttrib;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_BASIC;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendStrings(declAttrib.getSourceValue());
	}
	
	public static enum AttributeKinds implements ISourceRepresentation {
		ABSTRACT,
		AUTO,
		
		CONST,
		IMMUTABLE,
		INOUT,
		SHARED,
		
		DEPRECATED,
		ENUM,
		EXTERN,
		FINAL,
		NOTHROW,
		OVERRIDE,
		PURE,
		__GSHARED,
		SCOPE,
		STATIC,
		SYNCHRONIZED,
		
		REF,
		__THREAD,
		;
		
		public static AttributeKinds fromToken(DeeTokens token) {
			switch (token) {
			case KW_ABSTRACT: return ABSTRACT;
			case KW_AUTO: return AUTO;
			
			case KW_CONST: return CONST;
			case KW_IMMUTABLE: return IMMUTABLE;
			case KW_INOUT: return INOUT;
			case KW_SHARED: return SHARED;
			
			case KW_DEPRECATED: return DEPRECATED;
			case KW_ENUM: return ENUM;
			case KW_EXTERN: return EXTERN;
			case KW_FINAL: return FINAL;
			case KW_NOTHROW: return NOTHROW;
			case KW_OVERRIDE: return OVERRIDE;
			case KW_PURE: return PURE;
			case KW___GSHARED: return __GSHARED;
			case KW_SCOPE: return SCOPE;
			case KW_STATIC: return STATIC;
			case KW_SYNCHRONIZED: return SYNCHRONIZED;
			case KW_REF: return REF;
			case KW___THREAD: return __THREAD;
			default:
				return null;
			}
		}
		
		@Override
		public String getSourceValue() {
			return toString().toLowerCase();
		}
	}
	
}
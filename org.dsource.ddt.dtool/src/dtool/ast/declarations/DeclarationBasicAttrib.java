package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Definition;
import dtool.parser.DeeTokens;
import dtool.refmodel.INonScopedBlock;

public class DeclarationBasicAttrib extends DeclarationAttrib {
	
	public static enum AttributeKinds {
		DEPRECATED,
		STATIC,
		EXTERN,
		FINAL,
		SYNCHRONIZED,
		OVERRIDE,
		ABSTRACT,
		CONST,
		SCOPE,
		__GSHARED,
		SHARED,
		IMMUTABLE,
		INOUT,
		;
		
		public static AttributeKinds fromToken(DeeTokens token) {
			switch (token) {
			case KW_DEPRECATED: return DEPRECATED;
			case KW_STATIC: return STATIC;
			case KW_EXTERN: return EXTERN;
			case KW_FINAL: return FINAL;
			case KW_SYNCHRONIZED: return SYNCHRONIZED;
			case KW_OVERRIDE: return OVERRIDE;
			case KW_ABSTRACT: return ABSTRACT;
			case KW_CONST: return CONST;
			case KW_SCOPE: return SCOPE;
			case KW___GSHARED: return __GSHARED;
			case KW_SHARED: return SHARED;
			case KW_IMMUTABLE: return IMMUTABLE;
			case KW_INOUT: return INOUT;
			default:
				return null;
			}
		}
		
		// TODO review usage of this
		public int getBitForBitFlag() {
			return 2 >> ordinal();
		}
		
		public String toStringAsCode() {
			return toString().toLowerCase();
		}
	}
	
	public final AttributeKinds declAttrib;
	
	public DeclarationBasicAttrib(AttributeKinds declAttrib, AttribBodySyntax abs, NodeList2 decls, 
		SourceRange sr) {
		super(abs, decls, sr);
		this.declAttrib = declAttrib;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_BASIC_ATTRIB;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(declAttrib.toStringAsCode(), " ");
		toStringAsCode_body(cp);
	}
	
	public void processEffectiveModifiers() {
		INonScopedBlock block = this;
		processEffectiveModifiers(block);
	}
	
	private void processEffectiveModifiers(INonScopedBlock block) {
		Iterator<? extends IASTNode> iter = block.getMembersIterator();
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			if(node instanceof Definition) {
				Definition def = (Definition) node;
				def.effectiveModifiers |= declAttrib.getBitForBitFlag();
			} /*else if (node instanceof DeclarationImport && stclass == STC.STCstatic) {
				DeclarationImport declImport = (DeclarationImport) node;
				declImport.isStatic = true;
			} */else if(node instanceof INonScopedBlock) {
				processEffectiveModifiers((INonScopedBlock) node);
			}
		}
	}
	
}
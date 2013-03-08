package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.ISourceRepresentation;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Definition;
import dtool.refmodel.INonScopedBlock;

public class DeclarationProtection extends DeclarationAttrib {
	
	public enum Protection implements ISourceRepresentation {
	    PRIVATE,
	    PACKAGE,
	    PROTECTED,
	    PUBLIC,
	    EXPORT,
	    ;
		
		@Override
		public String getSourceValue() {
			return toString().toLowerCase();
		}
	}
	
	public final Protection protection;
	
	public DeclarationProtection(Protection protection, AttribBodySyntax bodySyntax, NodeList2 body, SourceRange sr) {
		super(bodySyntax, body, sr);
		this.protection = protection;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_PROTECTION;
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
		cp.append(protection);
		cp.append(" ");
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
				def.protection = Definition.fromProtection(protection);
			} else if (node instanceof DeclarationProtection) {
				// Do not descend, that inner decl take priority
			} else if (node instanceof DeclarationImport && protection == Protection.PUBLIC) {
				DeclarationImport declImport = (DeclarationImport) node;
				declImport.isTransitive = true;
			} else if(node instanceof INonScopedBlock) {
				processEffectiveModifiers((INonScopedBlock) node);
			}
		}
	}
	
}
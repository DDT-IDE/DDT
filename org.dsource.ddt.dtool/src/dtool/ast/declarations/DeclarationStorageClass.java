package dtool.ast.declarations;

import java.util.Iterator;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Definition;
import dtool.refmodel.INonScopedBlock;

public final class DeclarationStorageClass extends DeclarationAttrib {
	
	public final int stclass; // we assume there is only one storage class flag here
	
	public DeclarationStorageClass(int stclass, ASTNeoNode[] decls, boolean hasCurlies, SourceRange sourceRange) {
		super(new NodeList(decls, hasCurlies), sourceRange);
		this.stclass = stclass;
	}
	
	public DeclarationStorageClass(int stclass, NodeList decls, SourceRange sourceRange) {
		super(decls, sourceRange);
		this.stclass = stclass;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[DeclarationModifier]";
		//return "["+stclass+"]";
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
				def.effectiveModifiers |= stclass;
			} /*else if (node instanceof DeclarationImport && stclass == STC.STCstatic) {
				DeclarationImport declImport = (DeclarationImport) node;
				declImport.isStatic = true;
			} */else if(node instanceof INonScopedBlock) {
				processEffectiveModifiers((INonScopedBlock) node);
			}
		}
	}

}

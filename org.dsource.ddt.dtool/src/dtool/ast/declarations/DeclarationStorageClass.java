package dtool.ast.declarations;

import java.util.Iterator;

import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Definition;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public final class DeclarationStorageClass extends DeclarationAttrib {

	public int stclass; // we assume there is only one storage class flag here
	
	public DeclarationStorageClass(StorageClassDeclaration elem, ASTConversionContext convContex) {
		super(elem, elem.decl, convContex);
		this.stclass = elem.stc;
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

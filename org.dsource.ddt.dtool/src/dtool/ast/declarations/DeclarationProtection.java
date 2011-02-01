package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Definition;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public class DeclarationProtection extends DeclarationAttrib {

	public Modifier modifier;
	public PROT prot;
	
	public DeclarationProtection(ProtDeclaration elem, ASTConversionContext convContex) {
		super(elem, elem.decl, convContex);
		this.modifier = elem.modifier;
		this.prot = elem.protection;
		Assert.isTrue(PROT.fromTOK(this.modifier.tok) == this.prot);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, prot);
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "["+modifier+"]";
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
				def.protection = prot;
			} else if (node instanceof DeclarationProtection) {
				// Do not descend, that inner decl take priority
			} else if (node instanceof DeclarationImport && prot == PROT.PROTpublic) {
				DeclarationImport declImport = (DeclarationImport) node;
				declImport.isTransitive = true;
			} else if(node instanceof INonScopedBlock) {
				processEffectiveModifiers((INonScopedBlock) node);
			}
		}
	}
}

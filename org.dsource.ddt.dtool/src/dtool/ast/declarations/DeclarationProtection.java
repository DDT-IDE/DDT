package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;

import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Definition;
import dtool.refmodel.INonScopedBlock;

public class DeclarationProtection extends DeclarationAttrib {
	
	public final Modifier modifier;
	public final PROT prot;
	
	public DeclarationProtection(PROT prot, Modifier modifier, NodeList decls, SourceRange sourceRange) {
		super(decls, sourceRange);
		this.prot = prot;
		this.modifier = modifier;
		assertTrue(PROT.fromTOK(this.modifier.tok) == this.prot);
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
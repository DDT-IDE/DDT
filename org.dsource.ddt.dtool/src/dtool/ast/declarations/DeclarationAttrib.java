package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList_OLD;
import dtool.ast.declarations.AttribProtection.Protection;
import dtool.ast.definitions.Definition;
import dtool.ast.statements.BlockStatementUnscoped;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;
import dtool.util.ArrayView;

/**
 * Attribute declarations
 *  
 * Technicaly DMD doesn't accept certain attributes as statements (such as protection, align), 
 * but structurally we allow it, even though a syntax or semantic error may still be issued.
 */
public class DeclarationAttrib extends ASTNode implements INonScopedBlock, IDeclaration, IStatement {
	
	public static enum AttribBodySyntax { SINGLE_DECL, BRACE_BLOCK, COLON }
	
	public final ArrayView<Attribute> attributes;
	public final AttribBodySyntax bodySyntax;
	public final ASTNode body; // Note: can be DeclList
	
	public DeclarationAttrib(ArrayView<Attribute> attributes, AttribBodySyntax bodySyntax, ASTNode bodyDecls) {
		this.attributes = parentize(assertNotNull_(attributes));
		this.bodySyntax = assertNotNull_(bodySyntax);
		this.body = parentize(bodyDecls);
		
		localAnalysis();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ATTRIB;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, attributes);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(attributes, " ", true);
		cp.append(bodySyntax == AttribBodySyntax.COLON, " :\n");
		cp.append(body);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return getBodyIterator(body);
	}
	
	public static Iterator<? extends ASTNode> getBodyIterator(ASTNode body) {
		if(body == null) {
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		if(body instanceof NodeList_OLD) { /*BUG here MAKE, do DeclList instead of NodeList*/
			return ((NodeList_OLD<?>) body).nodes.iterator();
		}
		if(body instanceof DeclList) { /*BUG here MAKE, */
			return ((DeclList) body).nodes.iterator();
		}
		if(body instanceof BlockStatementUnscoped) { /*BUG here MAKE, comment*/
			return ((BlockStatementUnscoped) body).getMembersIterator();
		}
		return IteratorUtil.singletonIterator(body);
	}
	
	public void localAnalysis() {
		for (Attribute attribute : attributes) {
			if(attribute instanceof AttribBasic) {
				AttribBasic attribBasic = (AttribBasic) attribute;
				applyBasicAttributes(attribBasic, this);			
			} else if(attribute instanceof AttribProtection) {
				AttribProtection attribProtection = (AttribProtection) attribute;
				applyProtectionAttributes(attribProtection.protection, this);			
			} 
		}
	}
	
	protected void applyBasicAttributes(AttribBasic attribute, INonScopedBlock block) {
		Iterator<? extends IASTNode> iter = block.getMembersIterator();
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			if(node instanceof Definition) {
				Definition def = (Definition) node;
				def.setAttribute(attribute);
			} else if(node instanceof INonScopedBlock) {
				applyBasicAttributes(attribute, (INonScopedBlock) node);
			}
		}
	}
	
	protected void applyProtectionAttributes(Protection protection, INonScopedBlock block) {
		Iterator<? extends IASTNode> iter = block.getMembersIterator();
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			if(node instanceof Definition) {
				Definition def = (Definition) node;
				def.setProtection(protection);
			} else if (node instanceof AttribProtection) {
				// Do not descend, that inner decl take priority
			} else if (node instanceof DeclarationImport && protection == Protection.PUBLIC) {
				DeclarationImport declImport = (DeclarationImport) node;
				declImport.isTransitive = true;
			} else if(node instanceof INonScopedBlock) {
				applyProtectionAttributes(protection, (INonScopedBlock) node);
			}
		}
	}
	
}
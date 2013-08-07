package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList;
import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.statements.IStatement;
import dtool.resolver.INonScopedBlock;
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
		this.attributes = parentize(assertNotNull(attributes));
		this.bodySyntax = assertNotNull(bodySyntax);
		this.body = parentize(bodyDecls);
		
		localAnalysis();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ATTRIB;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, attributes);
		acceptVisitor(visitor, body);
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
		if(body instanceof NodeList<?>) {
			return ((NodeList<?>) body).nodes.iterator();
		}
		return IteratorUtil.singletonIterator(body);
	}
	
	public void localAnalysis() {
		for (Attribute attribute : attributes) {
			if(attribute instanceof AttribBasic) {
				AttribBasic attribBasic = (AttribBasic) attribute;
				applyBasicAttributes(attribBasic, this);			
			}
		}
		for (int ix = attributes.size() - 1; ix >= 0; ix--) {
			Attribute attribute = attributes.get(ix);
			if(attribute instanceof AttribProtection) {
				AttribProtection attribProtection = (AttribProtection) attribute;
				applyProtectionAttributes(attribProtection.protection, this);
				break; // last atribute takes precedence
			} 
		}
	}
	
	protected void applyBasicAttributes(AttribBasic attribute, INonScopedBlock block) {
		Iterator<? extends ASTNode> iter = block.getMembersIterator();
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			if(node instanceof CommonDefinition) {
				CommonDefinition def = (CommonDefinition) node;
				def.setAttribute(attribute);
			} else if(node instanceof INonScopedBlock) {
				applyBasicAttributes(attribute, (INonScopedBlock) node);
			}
		}
	}
	
	protected void applyProtectionAttributes(EProtection protection, INonScopedBlock block) {
		Iterator<? extends ASTNode> iter = block.getMembersIterator();
		while(iter.hasNext()) {
			ASTNode descendantNode = iter.next();
			
			if (anotherProtectionAttribPresent(descendantNode)) {
				continue; // Do not descend, other attrib takes precedence
			}
			if(descendantNode instanceof CommonDefinition) {
				CommonDefinition def = (CommonDefinition) descendantNode;
				def.setProtection(protection);
			} else if (descendantNode instanceof DeclarationImport && protection == EProtection.PUBLIC) {
				DeclarationImport declImport = (DeclarationImport) descendantNode;
				declImport.isTransitive = true;
			} else if(descendantNode instanceof INonScopedBlock) {
				applyProtectionAttributes(protection, (INonScopedBlock) descendantNode);
			}
		}
	}
	
	public boolean anotherProtectionAttribPresent(ASTNode node) {
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			for (Attribute attrib : declAttrib.attributes) {
				if(attrib instanceof AttribProtection) 
					return true;
			}
		}
		return false;
	}
	
}
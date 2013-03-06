package dtool.ast.references;

import static dtool.util.NewUtils.assertInstance;
import static dtool.util.NewUtils.exactlyOneIsNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class RefTemplateInstance extends Reference implements IQualifierNode, ITemplateRefNode {
	
	public final Reference tplRef;
	public final Resolvable singleArg;
	public final ArrayView<Resolvable> tplArgs;
	
	public RefTemplateInstance(ITemplateRefNode tplRef, Resolvable singleArg, ArrayView<Resolvable> tplArgs, 
		SourceRange sourceRange) {
		this.tplRef = parentizeI(assertInstance(tplRef, Reference.class));
		assertTrue(exactlyOneIsNull(singleArg, tplArgs));
		this.singleArg = parentize(singleArg);
		this.tplArgs = parentize(tplArgs);
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TEMPLATE_INSTANCE;
	}
	
	public boolean isSingleArgSyntax() {
		return singleArg != null;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tplRef);
			TreeVisitor.acceptChildren(visitor, singleArg);
			TreeVisitor.acceptChildren(visitor, tplArgs);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(tplRef, "!");
		if(isSingleArgSyntax()) {
			cp.append(singleArg);
		} else {
			cp.appendNodeList("(", tplArgs, ", ", ")");
		}
	}
	
	@Override
	public final boolean canMatch(DefUnitDescriptor defunit) {
		return tplRef.canMatch(defunit);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return tplRef.findTargetDefUnits(moduleResolver, findOneOnly);
	}
	
}
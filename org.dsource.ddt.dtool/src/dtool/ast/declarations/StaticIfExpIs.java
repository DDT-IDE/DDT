package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.ExpIs;
import dtool.ast.expressions.ExpIs.ExpIsSpecialization;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class StaticIfExpIs extends Expression {
	
	public final Reference typeRef;
	public final StaticIfExpIsDefUnit isExpDefUnit;
	public final ExpIsSpecialization specKind;
	public final Reference specTypeRef;
	public final ArrayView<TemplateParameter> tplParams;
	
	public StaticIfExpIs(Reference typeRef, StaticIfExpIsDefUnit isExpDefUnit, ExpIsSpecialization specKind, 
		Reference specTypeRef, ArrayView<TemplateParameter> tplParams) {
		this.typeRef = parentize(assertNotNull_(typeRef));
		this.isExpDefUnit = parentize(isExpDefUnit);
		this.specKind = specKind;
		this.specTypeRef = parentize(specTypeRef);
		assertTrue((specTypeRef == null) ==
			(specKind != ExpIsSpecialization.TYPE_SUBTYPE && specKind != ExpIsSpecialization.TYPE_EXACT)); 
		this.tplParams = parentize(tplParams);
		assertTrue((tplParams == null) ? true :
			(specKind == ExpIsSpecialization.TYPE_SUBTYPE || specKind == ExpIsSpecialization.TYPE_EXACT)); 
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATIC_IF_EXP_IS;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, typeRef);
			TreeVisitor.acceptChildren(visitor, isExpDefUnit);
			TreeVisitor.acceptChildren(visitor, specTypeRef);
			TreeVisitor.acceptChildren(visitor, tplParams);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("is(");
		cp.append(typeRef, " ");
		cp.append(isExpDefUnit);
		ExpIs.toStringAsCode_isExpSpecKind(cp, specKind, specTypeRef);
		cp.appendList(", ", tplParams, ", ", "");
		cp.append(")");
	}
	
	@Override
	public void afterModuleParseCheck() {
		super.afterModuleParseCheck();
		if(!(getParent() instanceof DeclarationStaticIf)) {
			// TODO add error
		}
	}
	
	public static class StaticIfExpIsDefUnit extends DefUnit {
		
		public StaticIfExpIsDefUnit(ProtoDefSymbol defIdTuple) {
			super(defIdTuple);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.STATIC_IF_EXP_IS_DEF_UNIT;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, defname);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
		}
		
		@Override
		public void afterModuleParseCheck() {
			super.afterModuleParseCheck();
			assertTrue(getParent() instanceof StaticIfExpIs);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			StaticIfExpIs staticIfIsExp = (StaticIfExpIs) getParent();
			// This is only correct for the basic ExpIs case, the other scenarios are hard to calculate
			return staticIfIsExp.typeRef.getTargetScope(moduleResolver);
		}
	}
	
}
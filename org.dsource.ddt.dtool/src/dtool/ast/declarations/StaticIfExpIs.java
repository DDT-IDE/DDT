package dtool.ast.declarations;

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
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
import dtool.resolver.CommonDefUnitSearch;
import dtool.util.ArrayView;

public class StaticIfExpIs extends Expression {
	
	public final Reference typeRef;
	public final StaticIfExpIsDefUnit isExpDefUnit;
	public final ExpIsSpecialization specKind;
	public final Reference specTypeRef;
	public final ArrayView<TemplateParameter> tplParams;
	
	public StaticIfExpIs(Reference typeRef, StaticIfExpIsDefUnit isExpDefUnit, ExpIsSpecialization specKind, 
		Reference specTypeRef, ArrayView<TemplateParameter> tplParams) {
		this.typeRef = parentize(assertNotNull(typeRef));
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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, typeRef);
		acceptVisitor(visitor, isExpDefUnit);
		acceptVisitor(visitor, specTypeRef);
		acceptVisitor(visitor, tplParams);
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
	public void doNodeSimpleAnalysis() {
		if(!(getParent() instanceof DeclarationStaticIf)) {
			// TODO add error
		}
	}
	
	public static class StaticIfExpIsDefUnit extends DefUnit {
		
		public StaticIfExpIsDefUnit(ProtoDefSymbol defIdTuple) {
			super(defIdTuple);
		}
		
		@Override
		protected StaticIfExpIs getParent_Concrete() {
			return assertCast(parent, StaticIfExpIs.class);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.STATIC_IF_EXP_IS_DEF_UNIT;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			StaticIfExpIs staticIfIsExp = getParent_Concrete();
			Reference.resolveSearchInReferedMembersScope(search, staticIfIsExp.typeRef);
		}
		
	}
	
}
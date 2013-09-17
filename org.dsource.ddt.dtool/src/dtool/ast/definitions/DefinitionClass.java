package dtool.ast.definitions;

import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {
	
	public final ArrayView<Reference> baseClasses;
	public final boolean baseClassesAfterConstraint;
	
	public DefinitionClass(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, ArrayView<Reference> baseClasses, boolean baseClassesAfterConstraint, 
		IAggregateBody aggrBody) 
	{
		super(comments, defId, tplParams, tplConstraint, aggrBody);
		this.baseClasses = parentize(baseClasses);
		this.baseClassesAfterConstraint = baseClassesAfterConstraint;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CLASS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "class ");
	}
	
	public void classLikeToStringAsCode(ASTCodePrinter cp, String keyword) {
		cp.append(keyword);
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		if(baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendList(": ", baseClasses, ",", " ");
		if(!baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append(aggrBody, "\n");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		if(baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, baseClasses);
		if(!baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		if(getBodyScope() == null) {
			// Even without a body scope, we can resolve in super scopes
			resolveSearchInSuperScopes(search);
		}
		super.resolveSearchInMembersScope(search);
	}
	
	public void resolveSearchInSuperScopes(CommonDefUnitSearch search) {
		IModuleResolver moduleResolver = search.getModuleResolver();
		for(Reference baseclass : CollectionUtil.nullToEmpty(baseClasses)) {
			INamedElement baseClassElem = baseclass.findTargetDefElement(moduleResolver);
			if(baseClassElem == null)
				continue;
			
			baseClassElem.resolveSearchInMembersScope(search);
		}
	}
	
}
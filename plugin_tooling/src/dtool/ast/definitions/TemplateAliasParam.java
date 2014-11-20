package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NullNamedElementSemantics;
import dtool.ast.expressions.Resolvable;
import dtool.engine.analysis.templates.AliasElement;

public class TemplateAliasParam extends TemplateParameter {
	
	public final Resolvable specializationValue;
	public final Resolvable defaultValue;
	
	public TemplateAliasParam(ProtoDefSymbol defId, Resolvable specializationValue, Resolvable defaultValue){
		super(defId);
		this.specializationValue = parentize(specializationValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_ALIAS_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, specializationValue);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.append(defname);
		cp.append(" : ", specializationValue);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getSemantics() {
		return semantics;
	}
	
	protected final INamedElementSemantics semantics = new NullNamedElementSemantics(); // Need template instance
	
	@Override
	public AliasElement createTemplateArgument(Resolvable argument) {
		return new AliasElement(defname, argument);
	}
	
}
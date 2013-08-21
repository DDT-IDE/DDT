package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;

/**
 * A synthetic defunit is not created from parsing. 
 * TODO: BM: then perhaps it should not be an ASTNode at all
 */
public abstract class SyntheticDefUnit extends DefUnit {
	
	public SyntheticDefUnit(DefSymbol defname) {
		super(defname);
	}
	
	public SyntheticDefUnit(String defName) {
		super(defName);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.NULL; // TODO: review if this is correct 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		// TODO: review if this is correct
		cp.append("#");
		cp.append(defname);
	}
	
	@Override
	public boolean isSynthetic() {
		return true;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return null;
	}
	
}
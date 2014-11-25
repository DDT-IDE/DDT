package melnorme.lang.tooling.engine.intrinsics;

import java.nio.file.Path;

import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * Base class for intrinsic elements. See {@link #isLanguageIntrinsic()} 
 */
public abstract class IntrinsicDefUnit extends AbstractNamedElement {
	
	protected final ElementDoc doc;
	
	public IntrinsicDefUnit(String name, ElementDoc doc) {
		super(name);
		this.doc = doc;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	// ATM #isLanguageIntrinsic is always true, but in the future we may refactor this class 
	// so that it can be used in non-intrinsic contexts (element instantiation for example).
	@Override
	public boolean isLanguageIntrinsic() {
		if(getParentElement() == null) {
			return true;
		}
		return getParentElement().isLanguageIntrinsic();
	}
	
	@Override
	public Path getModulePath() {
		return null;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return name;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return ""; // Special module value for intrinsic elements.
	}
	
	@Override
	public INamedElement getParentElement() {
		return null;
	}
	
	@Override
	public DefUnit resolveUnderlyingNode() {
		return null;
	}
	
	@Override
	public final ElementDoc resolveDDoc() {
		return doc;
	}
	
	@Override
	public String toString() {
		return "intrinsic_type#" + getName();
	}
	
}
package melnorme.lang.tooling.engine.intrinsics;

import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * Base class for intrinsic elements. See {@link #isLanguageIntrinsic()} 
 */
public abstract class IntrinsicNamedElement extends AbstractNamedElement {
	
	protected final ElementDoc doc;
	
	public IntrinsicNamedElement(String name, ElementDoc doc) {
		super(name, null);
		this.doc = doc;
	}
	
	@Override
	public INamedElement getParentNamedElement() {
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
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
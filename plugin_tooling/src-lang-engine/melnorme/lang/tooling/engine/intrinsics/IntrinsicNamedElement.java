package melnorme.lang.tooling.engine.intrinsics;

import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * Base class for builtin elements. See {@link #isBuiltinElement()}
 */
/* FIXME: refactor this */
public abstract class IntrinsicNamedElement extends AbstractNamedElement {
	
	protected final ElementDoc doc;
	
	public IntrinsicNamedElement(String name, ElementDoc doc, boolean isCompleted) {
		super(name, null, null, isCompleted);
		this.doc = doc;
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
	public String getModuleFullName() {
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
	
}
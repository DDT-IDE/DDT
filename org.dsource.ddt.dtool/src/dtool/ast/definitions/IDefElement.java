package dtool.ast.definitions;

/**
 * A lightweight handle to a defined named D element. 
 * May exists in source or outside source, or be implicitly defined. 
 */
public interface IDefElement {
	
	/** The name of the element that is referred to. */
	String getName();
	
	/** @return the extended name of the element referred to. 
	 * The extended name is the name of the element/defunit plus additional adornments(can contain spaces) that
	 * allow to disambiguate this defUnit from homonym defUnits in the same scope 
	 * (for example the adornment can be function parameters for function elements).
	 */
	String getExtendedName();

	/** Gets the archetype (the kind) of this DefElement. */
	EArcheType getArcheType();
	
	/** @return the fully qualified name of the module this element belongs to. 
	 * Can be null if element is not contained in a module. */
	String getModuleFullyQualifiedName();
	
	/** @return true if this is a pre-defined/native language element. 
	 * (example: primitives such as int, void, or native types like arrays, pointer types) 
	 */
	boolean isLanguageIntrinsic();
	
	/** @return the nearest enclosing {@link IDefElement}.
	 * That will be the qualifiying namespace, even if this element cannot be referred to directly. */
	IDefElement getParentNamespace();
	
	/** @return this element as a {@link DefUnit}, if it so, or null if it is not. 
	 * Synthetic defUnits must return null. */
	DefUnit asDefUnit();
	
}
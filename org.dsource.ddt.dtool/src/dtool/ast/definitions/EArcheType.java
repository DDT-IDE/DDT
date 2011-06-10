package dtool.ast.definitions;

public enum EArcheType {
	Module,
	Package,
	
	Variable,
	Function,
	
	//Native,
	Class(true),
	Interface(true),
	Struct(true),
	Union(true),
	
	Enum(true),
	EnumMember, // same as var?
	Alias,
	Typedef(true),
	Template,
	TypeParameter(true),
	Mixin,
	Tuple,
	;
	
	protected final boolean isType;
	
	private EArcheType() {
		this(false);
	}
	
	private EArcheType(boolean isType) {
		this.isType = isType;
	}
	
	/** Archetype kind is TYPE, meaning it can be used to declare variables. */
	public boolean isType() {
		return isType;
	}
	
}
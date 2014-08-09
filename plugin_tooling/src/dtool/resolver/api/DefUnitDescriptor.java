package dtool.resolver.api;

/**
 * An info class to reference a DefUnit
 * XXX: incomplete
 */
public class DefUnitDescriptor {
	
	protected final String qualifiedId;
	
	public DefUnitDescriptor(String qualifiedId) {
		this.qualifiedId = qualifiedId;
	}
	
	public String getQualifiedId() {
		return qualifiedId;
	}
	
	public boolean isNative() {
		return false;
	}
	
}
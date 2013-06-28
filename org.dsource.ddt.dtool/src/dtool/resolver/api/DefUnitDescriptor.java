package dtool.resolver.api;

/**
 * An info class to reference a DefUnit
 * XXX: incomplete
 */
public class DefUnitDescriptor {
	
	protected final String qualifiedId;
	protected final String idQualification;
	
	
	public DefUnitDescriptor(String qualifiedId) {
		this.qualifiedId = qualifiedId;
		this.idQualification = null;
	}
	
	public DefUnitDescriptor(String qualifiedId, String idQualification) {
		this.qualifiedId = qualifiedId;
		this.idQualification = idQualification;
	}
	
	public String getQualifiedId() {
		return qualifiedId;
	}
	
	public String getIdQualification() {
		return idQualification;
	}
	
	public boolean isNative() {
		return false;
	}
	
}
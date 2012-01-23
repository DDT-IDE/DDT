package dtool.ast;

/**
 * An info class to reference a DefUnit
 * XXX: incomplete
 */
public class DefUnitDescriptor {
	
	protected final String baseName;
	protected final String nameQualification;
	
	
	public DefUnitDescriptor(String baseName) {
		this.baseName = baseName;
		this.nameQualification = null;
	}
	
	public DefUnitDescriptor(String baseName, String nameQualification) {
		this.baseName = baseName;
		this.nameQualification = nameQualification;
	}
	
	public String getBaseName() {
		return baseName;
	}
	
	public String getNameQualification() {
		return nameQualification;
	}
	
	public boolean isNative() {
		return false;
	}
	
}


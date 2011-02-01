package dtool;

public class DToolBundle {
	
	public static final String BUNDLE_ID = "dtool";
	
	// Marker for commented out buggy code
	public static final boolean BUGS_MODE = false; 
	
	// Markers for commented out functionality that is not fully supported
	public static final boolean UNSUPPORTED_DMD_CONTRACTS = false; 
	public static final boolean UNSUPPORTED_DMD_FUNCTIONALITY = false;
	public static final boolean UNSUPPORTED_DMD_FUNCTIONALITY$;
	// Don't use initializer so that var does not become a compile time constant,
	// This is just a handy workaroud to avoid certain Eclipse dead code warnings:
	static { UNSUPPORTED_DMD_FUNCTIONALITY$ = UNSUPPORTED_DMD_FUNCTIONALITY; } 
	
}

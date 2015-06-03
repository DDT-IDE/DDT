package _org.eclipse.dltk.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class ValidatorMessages {

	static {
		NLS.initializeMessages(ValidatorMessages.class.getName(), ValidatorMessages.class);
	}

	private ValidatorMessages() {
		// Do not instantiate
	}

	public static String FileNameIsEmpty;

	// Path validator
	public static String FilePathIsEmpty;
	public static String FilePathNotExists;
	public static String FilePathIsInvalid;

	public static String DirPathIsEmpty;
	public static String DirPathNotExists;
	public static String DirPathIsInvalid;

	// Positive number validator
	public static String PositiveNumberIsEmpty;

	public static String PositiveNumberIsInvalid;

	// Port validator
	public static String PortIsEmpty;
	public static String PortShouldBeInRange;

	public static String MinValueInvalid;
}

package dtool;


/** 
 * dtool's and upwards log streams
 * TODO BM: use a proper logging system
 */
public class Logg {

	public static SimpleLogger nolog = new SimpleLogger(false);
	public static SimpleLogger main = new SimpleLogger();
	public static SimpleLogger model = new SimpleLogger();
	public static SimpleLogger astmodel = new SimpleLogger();
	public static SimpleLogger codeScanner = new SimpleLogger(false);
	public static SimpleLogger builder = new SimpleLogger(true);

}

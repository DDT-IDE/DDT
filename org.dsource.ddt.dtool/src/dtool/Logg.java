package dtool;


/** 
 * dtool's and upwards log streams
 * TODO BM: use a proper logging system
 */
public class Logg {

	public static SimpleLogger main = new SimpleLogger();
	public static SimpleLogger model = new SimpleLogger();
	public static SimpleLogger astmodel = new SimpleLogger();
	public static SimpleLogger builder = new SimpleLogger(true);

}

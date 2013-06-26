package dtool.refmodel.api;

public class PrefixSearchOptions {
	
	public String searchPrefix;
	public int namePrefixLen;
	public int rplLen;
	
	public PrefixSearchOptions() {
		searchPrefix = "";
		rplLen = 0;
		namePrefixLen = 0;
	}
	
}

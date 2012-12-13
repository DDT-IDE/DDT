package dtool.tests;

import java.util.ArrayList;

import dtool.ast.SourceRange;

public class AnnotatedSource {
	
	public final String source;
	public final ArrayList<AnnotatedSource.MetadataEntry> metadata;
	
	public AnnotatedSource(String source) {
		this.source = source;
		this.metadata = new ArrayList<MetadataEntry>();
	}
	
	public AnnotatedSource(String source, ArrayList<MetadataEntry> arrayList) {
		this.source = source;
		this.metadata = arrayList;
	}
	
	public static class MetadataEntry {
		public final String name;
		public final String extraValue;
		public final String associatedSource;
		public final SourceRange sourceRange;
		
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset) {
			this.name = name;
			this.extraValue = extraValue;
			this.associatedSource = associatedSource;
			this.sourceRange = new SourceRange(offset, associatedSource == null ? 0 : associatedSource.length());
		}
	}
	
}
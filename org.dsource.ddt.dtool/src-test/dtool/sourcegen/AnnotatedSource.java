package dtool.sourcegen;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.ArrayList;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.SourceRange;

public class AnnotatedSource {
	
	public final String source;
	public final String originalTemplatedSource;
	public final ArrayList<AnnotatedSource.MetadataEntry> metadata;
	
	public AnnotatedSource(String source, String originalTemplatedSource, ArrayList<MetadataEntry> arrayList) {
		this.source = source;
		this.originalTemplatedSource = originalTemplatedSource;
		this.metadata = arrayList;
	}
	
	public static class MetadataEntry {
		public final String name;
		public final String value;
		public final String associatedSource;
		public final int offset;
		
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset) {
			this.name = name;
			this.value = extraValue;
			this.associatedSource = associatedSource;
			this.offset = offset;
		}
		
		public SourceRange getSourceRange() {
			return new SourceRange(offset, associatedSource == null ? 0 : associatedSource.length());
		}
		
		@Override
		public String toString() {
			return "["+offset+"]" + name + "("+value+")" + associatedSource ;
		}
	}
	
	@Override
	public String toString() {
		return source + "\n--------- METADATA: ---------\n" + StringUtil.collToString(metadata, "\n");
	}
	
	public MetadataEntry findMetadata(String name) {
		MetadataEntry foundMde = null;
		for (MetadataEntry mde : metadata) {
			if(areEqual(mde.name, name)) {
				assertTrue(foundMde == null);
				foundMde = mde;
			}
		}
		return foundMde;
	}
	
}
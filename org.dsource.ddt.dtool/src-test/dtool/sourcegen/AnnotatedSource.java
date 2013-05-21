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
		public final int offset;
		public final String sourceValue;
		public final boolean sourceWasIncluded;
		
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset) {
			this(name, extraValue, associatedSource, offset, true);
		}
		
		public MetadataEntry(String name, String extraValue, String mdSource, int offset, boolean sourceWasIncluded) {
			this.name = name;
			this.value = extraValue;
			this.sourceValue = mdSource;
			this.offset = offset;
			this.sourceWasIncluded = sourceWasIncluded;
		}
		
		public SourceRange getSourceRange() {
			return new SourceRange(offset, sourceValue == null ? 0 : sourceValue.length());
		}
		
		public int getOffsetFromNoLength() {
			assertTrue(offset >= 0);
			assertTrue(sourceValue == null);
			return offset;
		}
		
		@Override
		public String toString() {
			return "["+offset+"]" + name + "("+value+")" + sourceValue ;
		}
	}
	
	@Override
	public String toString() {
		return source + "\n--------- METADATA: ---------\n" + StringUtil.collToString(metadata, "\n");
	}
	
	public MetadataEntry findMetadata(String name) {
		return findMetadata(name, true);
	}
	public MetadataEntry findMetadata(String name, boolean requireUnique) {
		MetadataEntry foundMde = null;
		for (MetadataEntry mde : metadata) {
			if(areEqual(mde.name, name)) {
				if(!requireUnique){
					return mde;
				}
				assertTrue(foundMde == null);
				foundMde = mde;
			}
		}
		return foundMde;
	}
	
	public MetadataEntry findMetadata(String name, String value) {
		MetadataEntry foundMde = null;
		for (MetadataEntry mde : metadata) {
			if(areEqual(mde.name, name) && areEqual(mde.value, value)) {
				assertTrue(foundMde == null);
				foundMde = mde;
			}
		}
		return foundMde;
	}
	
}
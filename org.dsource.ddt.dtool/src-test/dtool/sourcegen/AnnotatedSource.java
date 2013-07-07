package dtool.sourcegen;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.ArrayList;
import java.util.ListIterator;

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
		// the parent MDE to which offset applies, or null for top-level TODO
		private final Object parentScopeMDE; 
		public final boolean sourceWasIncluded;
		
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset) {
			this(name, extraValue, associatedSource, offset, null, true);
		}
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset, 
			Object parentScopeMDE) {
			this(name, extraValue, associatedSource, offset, parentScopeMDE, true);
		}
		public MetadataEntry(String name, String extraValue, String associatedSource, int offset, 
			boolean sourceWasIncluded) {
			this(name, extraValue, associatedSource, offset, null, sourceWasIncluded);
		}
		
		public MetadataEntry(String name, String extraValue, String mdSource, int offset, 
			Object parentScopeMDE, boolean sourceWasIncluded) {
			this.name = name;
			this.value = extraValue;
			this.offset = offset;
			assertTrue(offset >= 0);
			this.sourceValue = mdSource;
			this.parentScopeMDE = parentScopeMDE;
			this.sourceWasIncluded = sourceWasIncluded;
		}
		
		public SourceRange getSourceRange() {
			return new SourceRange(offset, sourceValue == null ? 0 : sourceValue.length());
		}
		
		public boolean isTopLevelMetadata() {
			return parentScopeMDE == null;
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
	
	public static String printSourceWithMetadata(AnnotatedSource testCase) {
		ListIterator<MetadataEntry> mdeIter = testCase.metadata.listIterator();
		StringBuffer sb = new StringBuffer();
		printCaseSourceWithMetaData(testCase.source, mdeIter, 0, testCase.source.length(), sb);
		assertTrue(mdeIter.hasNext() == false);
		return sb.toString();
	}
	
	public static void printCaseSourceWithMetaData(String source, ListIterator<MetadataEntry> mdeIter, 
		final int startOffset, final int maxSourceOffset, StringBuffer sb) {
		int offset = startOffset;

		while(mdeIter.hasNext()) {
			MetadataEntry mde = mdeIter.next();
			assertTrue(offset >= 0 && maxSourceOffset >= offset);
			int nextOffset = mde.offset;
			assertTrue(nextOffset >= offset);
			if(nextOffset > maxSourceOffset) {
				mdeIter.previous();
				break;
			}
			
			sb.append(source.substring(offset, nextOffset));
			offset = nextOffset;
			
			sb.append("#" + mde.name);
			if(mde.value != null) {
				sb.append("(" + mde.value + ")");
			}
			if(mde.sourceValue != null) {
				sb.append(mde.sourceWasIncluded ? "【" : "¤【");
				if(mde.sourceWasIncluded) {
					nextOffset += mde.sourceValue.length();
					printCaseSourceWithMetaData(source, mdeIter, offset, nextOffset, sb);
					offset = nextOffset;
				} else {
					printCaseSourceWithMetaData(mde.sourceValue, mdeIter, 0, mde.sourceValue.length(), sb);
				}
				sb.append("】");
			}
		}
		assertTrue(offset >= 0 && maxSourceOffset >= offset);
		sb.append(source.substring(offset, maxSourceOffset));
	}
}
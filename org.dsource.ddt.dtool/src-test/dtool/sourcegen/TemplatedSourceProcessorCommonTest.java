package dtool.sourcegen;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessor.StandardErrors;
import dtool.sourcegen.TemplatedSourceProcessor.TemplatedSourceProcessingException;
import dtool.sourcegen.TemplatedSourceProcessorParser.TemplatedSourceException;
import dtool.tests.CommonTest;

public class TemplatedSourceProcessorCommonTest extends CommonTest {
	
	public static class TestsTemplatedSourceProcessor extends TemplatedSourceProcessor {
		@Override
		protected void handleError(TemplatedSourceException tse) throws TemplatedSourceException {
			assertFail();
		}
	}
	
	public void testSourceProcessing(String defaultMarker, String source, GeneratedSourceChecker... checkers) {
		testSourceProcessing_____(defaultMarker, source, checkers);
	}
	
	public static void testSourceProcessing_____(String defaultMarker, String source, 
		GeneratedSourceChecker... checkers) {
		TemplatedSourceProcessor tsp = new TestsTemplatedSourceProcessor();
		AnnotatedSource[] annotatedSources = tsp.processSource_unchecked(defaultMarker, source);
		visitContainer(annotatedSources, checkers);
	}
	
	public void testSourceProcessing(String marker, String source, int errorOffset) {
		try {
			TemplatedSourceProcessor tsp = new TemplatedSourceProcessor();
			tsp.processSource(marker, source);
			assertFail();
		} catch(TemplatedSourceException tse) {
			assertTrue(tse.errorOffset == errorOffset);
		}
	}
	
	public void testSourceProcessing(String marker, String source, StandardErrors errorType, String errorObject) {
		try {
			TemplatedSourceProcessor tsp = new TemplatedSourceProcessor();
			tsp.processSource(marker, source);
			assertFail();
		} catch(TemplatedSourceException tse) {
			TemplatedSourceProcessingException tspe = assertCast(tse, TemplatedSourceProcessingException.class);
			assertEquals(tspe.errorString, errorType);
			assertEquals(tspe.errorObject.toString(), errorObject);
		}
	}
	
	protected abstract class GeneratedSourceChecker implements Visitor<AnnotatedSource> {} 
	protected GeneratedSourceChecker checkMD(final String expSource, final MetadataEntry... expMetadataArray) {
		return new GeneratedSourceChecker () {
			@Override
			public void visit(AnnotatedSource genSource) {
				assertEquals(genSource.source, expSource);
				assertEquals(genSource.metadata.size(), expMetadataArray.length);
				for (int i = 0; i < expMetadataArray.length; i++) {
					checkMetadata(genSource.metadata.get(i), expMetadataArray[i]);
				}
			}
		};
	}
	
	protected GeneratedSourceChecker checkSourceOnly(final String expSource, final int mdSize) {
		return new GeneratedSourceChecker () {
			@Override
			public void visit(AnnotatedSource genSource) {
				assertEquals(genSource.source, expSource);
				assertEquals(genSource.metadata.size(), mdSize);
			}
		};
	}
	
	public static final String DONT_CHECK = new String("NO_CHECK");
	
	protected void checkMetadata(MetadataEntry mde1, MetadataEntry expMde) {
		assertAreEqual(mde1.name, expMde.name);
		assertAreEqual(mde1.value, expMde.value);
		assertAreEqual(mde1.offset, expMde.offset);
		assertAreEqual(mde1.isTopLevelMetadata(), expMde.isTopLevelMetadata());
		assertAreEqual(mde1.sourceWasIncluded, expMde.sourceWasIncluded);
		if(expMde.sourceValue != DONT_CHECK)
			assertAreEqual(mde1.sourceValue, expMde.sourceValue);
	}
	
	public static String prepString(String source, String openDelim, String closeDelim) {
		source = source.replaceAll("►", openDelim);
		source = source.replaceAll("◄", closeDelim);
		source = source.replaceAll("◙", "●");
		return source;
	}
	
}
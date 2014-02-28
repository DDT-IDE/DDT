package dtool.sourcegen;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.utilbox.misc.StreamUtil;
import dtool.sourcegen.TemplatedSourceProcessorParser.TemplatedSourceException;
import dtool.tests.utils.SimpleParser;

public abstract class SplitProcessor {
	
	protected void handleError(TemplatedSourceException tse) throws TemplatedSourceException {
		throw tse;
	}
	
	protected static final String[] splitKeywords = { "#:HEADER", "Ⓗ", "#:SPLIT", "━━", "▂▂", "▃▃"};
	
	public static boolean isTSPSourceStart(Reader reader) throws IOException {
		String sourceIntro = new String(StreamUtil.readCharAmountFromReader(reader, 10));
		SimpleParser parser = new SimpleParser(sourceIntro);
		return parser.tryConsume(splitKeywords) > 0;
	}
	
	public void splitSourceCases(String defaultMarker, String fileSource) throws TemplatedSourceException {
		SimpleParser parser = new SimpleParser(fileSource);
		
		do {
			boolean isHeader = false;
			String keyMarker = defaultMarker;
			
			int alt = parser.tryConsume(splitKeywords);
			if(alt != SimpleParser.EOF) {
				if(alt == 0 || alt == 1) {
					isHeader = true;
				}
				if(parser.seekToNewLine() == false) {
					handleError(new TemplatedSourceException(parser.getSourcePosition()));
				}
				Matcher matcher = Pattern.compile("→(.)").matcher(parser.getLastConsumedString());
				if(matcher.find()) {
					keyMarker = matcher.group(1);
				}
			} else {
				assertTrue(parser.getSourcePosition() == 0);
			}
			
			parser.consumeUntilAny(splitKeywords);
			
			String unprocessedCaseSource = parser.getLastConsumedString();
			processSplitCaseSource(unprocessedCaseSource, isHeader, keyMarker);
		} while(!parser.lookaheadIsEOF());
	}
	
	protected abstract void processSplitCaseSource(String caseSource, boolean isHeader, String keyMarker) 
		throws TemplatedSourceException;
	
}

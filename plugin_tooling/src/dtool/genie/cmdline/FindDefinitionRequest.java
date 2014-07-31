/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.genie.cmdline;

import static melnorme.utilbox.core.CoreUtil.blindCast;
import static melnorme.utilbox.core.CoreUtil.nullToEmpty;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dtool.ast.SourceRange;
import dtool.genie.GenieServer.GenieCommandException;
import dtool.genie.JsonDeserializeHelper;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;
import dtool.util.JsonReaderExt;
import dtool.util.JsonWriterExt;

public class FindDefinitionRequest extends AbstractClientOperation {
	
	public FindDefinitionRequest() {
		super("find_definition");
	}
	
	@Override
	public String getOneLineSummary() {
		return "Find the definition of the symbol referenced at a given location.";
	}
	
	@Override
	public void printCommandHelp(PrintStream out) {
		out.println(helpUsageIntro() + "<filepath> <offset> [<port>]");
		out.println();
		out.println(getOneLineSummary());
		out.println("  filepath  - absolute file path of a D module.");
		out.println("  offset    - offset (in Unicode characters) of the reference to resolve.");
	}
	
	protected Path modulePath;
	protected int offset = -1;
	
	public FindDefinitionRequest setArguments(Path modulePath, int offset) {
		this.modulePath = modulePath; this.offset = offset;
		return this;
	}
	
	@Override
	protected void processArgs() {
		modulePath = parseValidPath(retrieveFirstUnparsedArgument(true));
		offset = parsePositiveInt(retrieveFirstUnparsedArgument(true));
		super.processArgs();
	}
	
	@Override
	protected void writeRequestObjectProperties(JsonWriterExt jsonWriter) throws IOException {
		jsonWriter.name("find_definition");
		jsonWriter.beginObject();
		jsonWriter.writeProperty("filepath", modulePath.toString());
		jsonWriter.writeProperty("offset", offset);
		jsonWriter.endObject();
	}
	
	public static class FindDefinitionResultParser extends JsonDeserializeHelper<GenieCommandException> {
		@Override
		protected GenieCommandException validationError(String message) {
			return new GenieCommandException(message);
		}
		
		public FindDefinitionResult read(JsonReaderExt jsonParser) throws GenieCommandException, IOException {
			HashMap<String, Object> response = JsonReaderExt.readJsonObject(jsonParser);
			
			String errorMessage = getStringOrNull(response, "error");
			if(errorMessage != null) {
				return new FindDefinitionResult(errorMessage);
			}
			
			List<?> jsonResults = blindCast(response.get("results"));
			ArrayList<FindDefinitionResultEntry> results = new ArrayList<>();
			
			for (Object jsonResultEntry : nullToEmpty(jsonResults)) {
				results.add(findDefResult(jsonResultEntry));
			}
			
			return new FindDefinitionResult(results);
		}
		
		protected FindDefinitionResultEntry findDefResult(Object object) throws GenieCommandException {
			Map<String, Object> resultEntry = blindCast(object);
			
			SourceRange sr = null;
			
			Integer offset = getIntegerOrNull(resultEntry, "offset");
			if(offset != null) {
				sr = new SourceRange(offset, getInt(resultEntry, "length"));
			}
			
			return new FindDefinitionResultEntry(
				getString(resultEntry, "extendedName"), 
				getBoolean(resultEntry, "isIntrinsic"), 
				getPathOrNull(resultEntry, "modulePath"), 
				sr);
		}
		
	}
	
}
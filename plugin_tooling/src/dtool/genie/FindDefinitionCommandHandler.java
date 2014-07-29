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
package dtool.genie;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import dtool.genie.GenieServer.GenieCommandException;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;
import melnorme.utilbox.misc.StringUtil;

public class FindDefinitionCommandHandler extends JsonCommandHandler {
	
	public FindDefinitionCommandHandler(GenieServer genieServer) {
		super("find_definition", genieServer);
	}
	
	protected HashMap<String, Object> commandArguments;
	
	@Override
	protected void parseCommandInput() throws IOException {
		commandArguments = jsonParser.readJsonObject();
	}
	
	@Override
	protected void writeResponseJsonContents() throws IOException, GenieCommandException {
		System.out.println(StringUtil.collToString(commandArguments.entrySet(), "\n"));
		
		Path modulePath = getPath(commandArguments, "filepath");
		int offset = getInt(commandArguments, "offset");
		
		FindDefinitionResult cmdResult = getDToolServer().doFindDefinition(modulePath, offset);
		
		if(cmdResult.errorMessage != null) {
			jsonWriter.writeProperty("error", cmdResult.errorMessage);
		}
		
		if(cmdResult.results != null) {
			jsonWriter.name("results");
			
			jsonWriter.beginArray();
			for (FindDefinitionResultEntry resultEntry : cmdResult.results) {
				writeResultEntry(resultEntry);
			}
			jsonWriter.endArray();
		}
		
	}
	
	protected void writeResultEntry(FindDefinitionResultEntry result) throws IOException {
		jsonWriter.beginObject();
		jsonWriter.writeProperty("extendedName", result.extendedName);
		jsonWriter.writeProperty("isIntrinsic", result.isLanguageIntrinsic);
		jsonWriter.writeProperty("modulePath", result.modulePath.toString());
		jsonWriter.writeProperty("offset", result.sourceRange.getOffset());
		jsonWriter.writeProperty("length", result.sourceRange.getLength());
		jsonWriter.endObject();
	}
	
}
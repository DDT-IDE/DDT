/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.structure;

import dtool.parser.DeeParserResult.ParsedModule;
import melnorme.lang.tooling.ast.ParserError;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.Location;

public class SourceFileStructure extends SourceFileStructure_Default {
	
	public final ParsedModule parsedModule;
	
	public SourceFileStructure(Location location, Indexable<StructureElement> children,
			Indexable<ParserError> parserProblems) {
		super(location, children, parserProblems);
		this.parsedModule = null;
	}
	
	public SourceFileStructure(Location location, Indexable<StructureElement> children, 
			ParsedModule parsedModule) {
		super(location, children, parsedModule.getErrors());
		this.parsedModule = parsedModule;
	}
	
}
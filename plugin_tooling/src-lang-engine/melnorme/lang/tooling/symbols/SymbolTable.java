/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.symbols;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.utilbox.collections.Collection2;
import melnorme.utilbox.collections.HashMap2;
import dtool.ast.definitions.EArcheType;
import dtool.engine.analysis.ModuleProxy;

/**
 * A symbol table (map of named elements).
 * 
 * Automatically handles merging of package namespaces.
 * 
 */
public class SymbolTable {
	
	protected final HashMap2<String, INamedElement> map = new HashMap2<>();
	
	public HashMap<String,INamedElement> getMap() {
		return map;
	}
	
	public Set<Entry<String, INamedElement>> getEntries() {
		return map.entrySet();
	}
	
	public Collection2<INamedElement> getElements() {
		return map.getValuesView();
	}
	
	public void addSymbols(SymbolTable symbolTable) {
		addSymbols(symbolTable.map.values());
	}
	
	public void addSymbols(Iterable<INamedElement> values) {
		for (INamedElement namedElement : values) {
			addSymbol(namedElement);
		}
	}
	
	public void addSymbol(INamedElement newElement) {
		String name = newElement.getNameInRegularNamespace();
		
		INamedElement existingNamedElement = map.get(name);
		
		if(existingNamedElement instanceof PackageNamespace && newElement instanceof PackageNamespace) {
			PackageNamespace existingNamespace = (PackageNamespace) existingNamedElement;
			PackageNamespace newNamespace = (PackageNamespace) newElement;
			
			if(existingNamespace.isCompleted()) {
				existingNamespace = existingNamespace.doCloneTree();
				map.put(name, existingNamespace);
			}
			existingNamespace.getNamespaceForModification().addSymbols(newNamespace.getNamespaceElements());
		} else {
			addEntryToMap(name, newElement);
		}
	}
	
	protected void addEntryToMap(String name, INamedElement newElement) {
		INamedElement existingEntry = map.get(name);
		if(existingEntry == null) {
			doAddEntryToMap(name, newElement);
		} else {
			// An entry already exists
			
			if(existingEntry.getArcheType() == EArcheType.Module && newElement.getArcheType() == EArcheType.Module) {
				assertTrue(existingEntry.getFullyQualifiedName().equals(newElement.getFullyQualifiedName()));
				return; // Don't add duplicated element.
			}
			
			if(existingEntry == newElement) {
				// I don't think this case actually happens, but still, just in case:
				return; // They are the same, so ignore
			}
			
			OverloadedNamedElement overloadElement;
			
			if(existingEntry instanceof OverloadedNamedElement) {
				overloadElement = (OverloadedNamedElement) existingEntry;
			} else {
				// Give priority to ModuleProxy element (note: this isn't entirely like DMD behavior
				if(newElement instanceof ModuleProxy && existingEntry instanceof PackageNamespace) {
					doAddEntryToMap(name, newElement);
					return;
				}
				if(newElement instanceof PackageNamespace && existingEntry instanceof ModuleProxy) {
					return;
				}
				
				overloadElement = new OverloadedNamedElement(existingEntry);
				doAddEntryToMap(name, overloadElement);
			}
			
			overloadElement.addElement(newElement);
		}
	}
	
	public void doAddEntryToMap(String name, INamedElement newElement) {
		map.put(name, newElement);
	}
	
	public void addVisibleSymbols(SymbolTable symbolTable) {
		
		for (Entry<String, INamedElement> nameEntry : symbolTable.getEntries()) {
			String matchedName = nameEntry.getKey();
			INamedElement matchedElement = nameEntry.getValue();
			
			INamedElement existingSymbol = map.get(matchedName);
			if(existingSymbol == null || 
					(existingSymbol instanceof PackageNamespace && matchedElement instanceof PackageNamespace)) {
				
				if(matchedElement instanceof OverloadedNamedElement) {
					OverloadedNamedElement overloadedNamedElement = (OverloadedNamedElement) matchedElement;
					overloadedNamedElement.setCompleted();
				}
				
				addSymbol(matchedElement);
			}
		}
		
		symbolTable.map.clear(); // symbolTable can no longer be used
	}
	
}
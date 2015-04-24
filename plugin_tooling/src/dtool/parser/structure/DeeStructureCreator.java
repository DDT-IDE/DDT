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
package dtool.parser.structure;

import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast.util.NodeList;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.lang.tooling.structure.IStructureElement;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.lang.tooling.structure.StructureElement;
import melnorme.lang.tooling.structure.StructureElementData;
import melnorme.lang.tooling.structure.StructureElementKind;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.Location;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.references.Reference;
import dtool.engine.analysis.IVarDefinitionLike;
import dtool.parser.DeeParserResult.ParsedModule;

public class DeeStructureCreator extends ASTVisitor {
	
	protected ArrayList2<StructureElement> elements;
	
	public SourceFileStructure createStructure(Location location, ParsedModule parsedModule) {
		Indexable<IStructureElement> moduleChildren = collectChildElements(parsedModule.module).upcastTypeParameter();
		return new SourceFileStructure(location, moduleChildren);
	}
	
	public Indexable<StructureElement> collectChildElements(ASTNode node) {
		elements = new ArrayList2<>();
		node.visitChildren(this);
		
		Indexable<StructureElement> nodeChildren = elements;
		elements = null;
		return nodeChildren;
	}
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(node instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) node;
			return visit(defUnit);
		}
		if(node instanceof NodeList<?>) {
			return true;
		}
		if(node instanceof INonScopedContainer) {
			return true;
		}

		if(node instanceof EnumBody) {
			return true;
		}
		
		return false; 
	}
	
	@Override
	public void postVisit(ASTNode node) {
	}
	
	public void pushNewElement(StructureElement newStructureElement) {
		elements.add(newStructureElement);
	}
	
	public boolean visit(DefUnit node) {
		
		StructureElementKind kind = null;
		String type = null;
		StructureElementData elementData = null;
		boolean hasStructuralChildren = true;
		
		switch (node.getArcheType()) {
		case Module:
			return true;
		
		case Variable:
			kind = StructureElementKind.VARIABLE;
			hasStructuralChildren = false;
			break;
		case Function:
			DefinitionFunction defFunction = (DefinitionFunction) node;
			kind = StructureElementKind.FUNCTION;
			type = getTypeDesc(defFunction.retType);
			hasStructuralChildren = false;
			break;
		case Constructor:
			kind = StructureElementKind.CONSTRUCTOR;
			hasStructuralChildren = false;
			break;
		
		case Struct: kind = StructureElementKind.STRUCT; break;
		case Union: kind = StructureElementKind.UNION; break;
		case Class: kind = StructureElementKind.CLASS; break;
		case Interface: kind = StructureElementKind.INTERFACE; break;
		case Template: kind = StructureElementKind.TEMPLATE; break;
		case Enum: 
			DefinitionEnum defEnum = (DefinitionEnum) node;
			kind = StructureElementKind.ENUM;
			type = getTypeDesc(defEnum.type);
			break;
		case EnumMember: 
			kind = StructureElementKind.VARIABLE;
			hasStructuralChildren = false;
			break;
		case Mixin: 
			kind = StructureElementKind.MIXIN;
			break;
		
		case Alias: 
			kind = StructureElementKind.ALIAS; 
			if(node instanceof DefinitionAliasFragment) {
				DefinitionAliasFragment aliasFragment = (DefinitionAliasFragment) node;
				type = getTypeDesc(aliasFragment.target);
			} else {
				// TODO: type for other aliases
			}
			hasStructuralChildren = false;
			break;
		
		case Error:
		case Package:
		case Tuple:
		case TypeParameter:
			return false;
		}
		
		if(node instanceof IVarDefinitionLike) {
			IVarDefinitionLike varDef = (IVarDefinitionLike) node;
			type = getTypeDesc(varDef.getDeclaredType());
			
			if(node instanceof DefinitionVariable) {
				hasStructuralChildren = true; // Review this, might change in the future
			}
			
		} else {
			
		}
		
		if(node instanceof CommonDefinition) {
			CommonDefinition commonDefinition = (CommonDefinition) node;
			elementData = getAttributes(commonDefinition); 
		} else {
			elementData = new StructureElementData();
		}
		
		pushNewElement(new StructureElement(
			node.getName(),
			node.getNameSourceRangeOrNull(),
			node.getSourceRange(),
			kind,
			elementData,
			type,
			hasStructuralChildren ? collectChildren(node) : null)
		);
		return false;
	}
	
	protected StructureElementData getAttributes(DefUnit node) {
		if(node instanceof CommonDefinition) {
//			CommonDefinition commonDef = (CommonDefinition) node;
			// TODO
		}
		return new StructureElementData();
	}
	
	protected String getTypeDesc(Reference type) {
		return type == null ? null : type.toStringAsCode();
	}
	
	public static Indexable<StructureElement> collectChildren(DefUnit node) {
		return new DeeStructureCreator().collectChildElements(node);
	}
	
}
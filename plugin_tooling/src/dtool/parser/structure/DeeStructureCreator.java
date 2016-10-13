/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.structure;

import java.util.EnumSet;

import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.ITemplateParameter;
import dtool.engine.operations.DeeNamedElementLabelProvider.DeeNamedElementSimpleLabelProvider;
import dtool.parser.DeeParserResult.ParsedModule;
import melnorme.lang.tooling.EAttributeFlag;
import melnorme.lang.tooling.ElementAttributes;
import melnorme.lang.tooling.ElementLabelInfo;
import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast.util.NodeList;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.lang.tooling.structure.StructureElement;
import melnorme.lang.tooling.structure.StructureElementKind;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.Location;

public class DeeStructureCreator extends ASTVisitor {
	
	protected ArrayList2<StructureElement> elements;
	
	public DeeStructureCreator() {
	}
	
	/**
	 * Create a {@link SourceFileStructure} from given parsedModule
	 * @param location optional parameter, can be null.
	 */
	public SourceFileStructure createStructure(ParsedModule parsedModule, Location location) {
		Indexable<StructureElement> moduleChildren = collectChildElements(parsedModule.module);
		return new SourceFileStructure(location, moduleChildren, parsedModule);
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
		if(node instanceof DeclarationSpecialFunction) {
			return visit_DeclarationSpecialFunction((DeclarationSpecialFunction) node);
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
		
		if(node.getArcheType() == EArcheType.Module) {
			return true; // Ignore node, just go to children
		}
		if(node instanceof ITemplateParameter) {
			return false; // Ignore all subtree
		}
		
		ElementLabelInfo labelInfo = new DeeLabelInfoProvider().getLabelInfo(node);
		
		boolean hasStructuralChildren = 
				node instanceof DefinitionAggregate ||
				node instanceof DefinitionEnum ||
				node instanceof DefinitionVariable; // Review this, might change in the future
		
		String nameLabel = new DeeNamedElementSimpleLabelProvider().getLabel(node);
		pushNewElement(new StructureElement(
			nameLabel,
			node.getNameSourceRangeOrNull(),
			node.getSourceRange(),
			labelInfo.kind,
			labelInfo.elementAttribs,
			labelInfo.type,
			hasStructuralChildren ? collectChildren(node) : null)
		);
		return false;
	}
	
	public static Indexable<StructureElement> collectChildren(ASTNode node) {
		return new DeeStructureCreator().collectChildElements(node);
	}
	
	
	public boolean visit_DeclarationSpecialFunction(DeclarationSpecialFunction node) {
		
		EnumSet<EAttributeFlag> flagsSet = ElementAttributes.newFlagsSet();
		ElementAttributes elementAttribs = new ElementAttributes(null, flagsSet);
		
		boolean hasStructuralChildren = true; 
		
		pushNewElement(new StructureElement(
			node.kind.toStringAsCode(),
			null,
			node.getSourceRange(),
			StructureElementKind.CONSTRUCTOR,
			elementAttribs,
			null,
			hasStructuralChildren ? collectChildren(node) : null)
		);
		return false;
	}
	
}
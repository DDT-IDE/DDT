/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.expressions.Resolvable;
import dtool.engine.analysis.templates.TemplateParameterAnalyser;
import dtool.engine.analysis.templates.TupleElement;

public class TemplateTupleParam extends DefUnit implements IConcreteNamedElement, ITemplateParameter {
	
	public TemplateTupleParam(DefSymbol defName) {
		super(defName);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TUPLE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TemplateTupleParam(clone(defName));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defName);
		cp.append("...");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Tuple;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NonValueConcreteElementSemantics(this, pickedElement) {
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				return;
			}
			
		};
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public TemplateParameterAnalyser getParameterAnalyser() {
		return templateParameterAnalyser;
	}
	
	protected final TemplateParameterAnalyser templateParameterAnalyser = new TemplateParameterAnalyser() {
		
		@Override
		public TplMatchLevel getMatchPriority(Resolvable tplArg, ISemanticContext context) {
			return TplMatchLevel.TUPLE;
		}
		
		@Override
		public INamedElementNode createTemplateArgument(Indexable<Resolvable> tplArgs, int argIndex, 
				ISemanticContext tplRefContext) {
			
			// Consume all available arguments.
			ArrayList2<Resolvable> tupleArgs = new ArrayList2<>();
			for (int ix = argIndex; ix < tplArgs.size(); ix++) {
				tupleArgs.add(tplArgs.get(ix));
			}
			
			NodeVector<Resolvable> tupleArg = new NodeVector<>(tupleArgs.toArray(Resolvable.class));
			return new TupleElement(defName, tupleArg);
		}
	};
	
}
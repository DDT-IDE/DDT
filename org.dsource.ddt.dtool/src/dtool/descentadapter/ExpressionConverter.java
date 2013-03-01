/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;

import java.util.List;

import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.CallExp;
import dtool.DToolBundle;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.ExpLiteralArray;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

public class ExpressionConverter extends BaseDmdConverter {
	
	public static Expression convert(descent.internal.compiler.parser.Expression exp, 
			ASTConversionContext convContext) {
		// TODO: AST: convert Exp parenthesis?
		return downCast(DescentASTConverter.convertElem(exp, convContext), Expression.class);
	}
	
	public static Resolvable convert2(descent.internal.compiler.parser.Expression exp, 
			ASTConversionContext convContext) {
		Resolvable newExp = convert(exp, convContext);
		if (newExp instanceof ExpReference) {
			newExp = downCast(newExp, ExpReference.class).ref;
			((ASTNeoNode) newExp).parent = null;
		}
		return newExp;
	}
	
	public static ArrayView<Resolvable> convertMany(List<descent.internal.compiler.parser.Expression> elements
			, ASTConversionContext convContext) {
		return DescentASTConverter.convertMany(elements, Resolvable.class, convContext);
	}
	
	/* ------------------------- */
	
	public static ExpCall createExpCall(CallExp elem, ASTConversionContext convContext) {
		SourceRange sourceRange = DefinitionConverter.sourceRange(elem);
		Expression callee = ExpressionConverter.convert(elem.e1, convContext); 
		ArrayView<Expression> args = DescentASTConverter.convertMany(elem.arguments, Expression.class, convContext);
		return new ExpCall(callee, args, sourceRange);
	}
	
	public static ExpLiteralArray createExpArrayLiteral(ArrayLiteralExp elem, ASTConversionContext convContext) {
		
		ArrayView<Expression> args = DescentASTConverter.convertMany(elem.elements, Expression.class, convContext);
		
		SourceRange sourceRange = DefinitionConverter.sourceRange(elem);
		if(sourceRange == null && DToolBundle.DMDPARSER_PROBLEMS__BUG41) {
			int last = args.size() - 1;
			if(last >= 0) {
				assertTrue(args.get(0).hasNoSourceRangeInfo() == false);
				assertTrue(args.get(last).hasNoSourceRangeInfo() == false);
				sourceRange = DefinitionConverter.sourceRangeStrict(
						args.get(0).getStartPos(), args.get(last).getEndPos());
			} else {
				// We're screwed, can't estimate a source range...
			}
		}
		
		return new ExpLiteralArray(args, sourceRange);
	}
	
}

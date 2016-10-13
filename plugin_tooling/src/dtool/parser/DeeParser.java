/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dtool.ast.definitions.Module;
import dtool.engine.modules.ModuleNamingRules;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.common.LexElementProducer;
import dtool.parser.common.LexElementSource;
import dtool.parser.common.Token;
import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.common.ParserError;
import melnorme.lang.tooling.common.ParserError.ErrorSourceRangeComparator;
import melnorme.utilbox.concurrency.ICancelMonitor;
import melnorme.utilbox.concurrency.ICancelMonitor.NullCancelMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;

/**
 * Concrete D Parser class
 */
public class DeeParser 
//It's not very elegant, but inheritance is used here just for the purpose of namespace importing:
	extends DeeParser_Statements 
{
	
	public static ParsedModule parseUnlocatedModule(String source, String defaultModuleName) {
		return parseSourceModule(source, defaultModuleName, null);
	}
	
	public static ParsedModule parseSourceModule(String source, Path modulePath) {
		return parseSourceModule(source, null, modulePath);
	}
	
	public static ParsedModule parseSourceModule(String source, String defaultModuleName, Path modulePath) {
		ICancelMonitor cm = null;
		try {
			return new DeeParser(source, cm).parseModuleSource(defaultModuleName, modulePath);
		} catch(OperationCancellation e) {
			throw assertUnreachable(); // CM is null
		}
	}
	
	public static ParsedModule parseUnlocatedModule(String source, String defaultModuleName, ICancelMonitor cm)
			throws OperationCancellation {
		return parseSourceModule(source, defaultModuleName, null, cm);
	}
	
	public static ParsedModule parseSourceModule(String source, Path modulePath, ICancelMonitor cm)
			throws OperationCancellation {
		return parseSourceModule(source, null, modulePath, cm);
	}
	
	public static ParsedModule parseSourceModule(String source, String defaultModuleName, Path modulePath, 
			ICancelMonitor cm) throws OperationCancellation {
		return new DeeParser(source, cm).parseModuleSource(defaultModuleName, modulePath);
	}
	
	public static String getDefaultModuleName(Path modulePath) {
		String fileName = modulePath.getFileName().toString();
		return ModuleNamingRules.getDefaultModuleNameFromFileName(fileName);
	}
	
	/* -----------------  ----------------- */
	
	protected ArrayList<ParserError> lexerErrors = new ArrayList<>();
	protected final ICancelMonitor cancelMonitor;
	
	public DeeParser(String source, ICancelMonitor cancelMonitor) {
		this(new DeeLexer(source), cancelMonitor);
	}
	
	public DeeParser(String source) {
		this(new DeeLexer(source), null);
	}
	
	protected DeeParser(DeeLexer deeLexer, ICancelMonitor cancelMonitor) {
		this.cancelMonitor = (cancelMonitor != null) ? cancelMonitor : new NullCancelMonitor();
		
		this.source = deeLexer.getSource();
		DeeLexElementProducer deeLexElementProducer = new DeeLexElementProducer();
		this.lexSource = new LexElementSource(deeLexElementProducer.produceLexTokens(deeLexer));
		this.lexerErrors = deeLexElementProducer.lexerErrors;
	}
	
	public static final class DeeLexElementProducer extends LexElementProducer {
		protected ArrayList<ParserError> lexerErrors = new ArrayList<>();
		
		@Override
		protected void tokenParsed(Token token) {
			DeeTokenSemantics.checkTokenErrors(token, lexerErrors);
		}
	}
	
	@Override
	protected final DeeParser thisParser() {
		return this;
	}
	
	public ParsedModule parseModuleSource(String defaultModuleName, Path modulePath) throws OperationCancellation {
		if(defaultModuleName == null) {
			assertNotNull(modulePath);
			defaultModuleName = getDefaultModuleName(modulePath);
		}
		
		NodeResult<Module> nodeResult = parseModule(defaultModuleName, modulePath);
		return (ParsedModule) prepParseResult(null, nodeResult, modulePath);
	}
	
	@Override
	protected void nodeConcluded(ASTNode node) throws OperationCancellation {
		if(cancelMonitor.isCancelled()) {
			throw new OperationCancellation();
		}
		super.nodeConcluded(node);
	}
	
	public DeeParserResult parseUsingRule(ParseRuleDescription parseRule) throws OperationCancellation {
		NodeResult<? extends ASTNode> nodeResult;
		assertNotNull(parseRule);
		
		if(parseRule == DeeParser.RULE_EXPRESSION) {
			nodeResult = parseExpression();
		} else if(parseRule == DeeParser.RULE_REFERENCE) {
			nodeResult = parseTypeReference();
		} else if(parseRule == DeeParser.RULE_DECLARATION) {
			nodeResult = parseDeclaration();
		} else if(parseRule == RULE_TYPE_OR_EXP) {
			nodeResult = parseTypeOrExpression(true);
		} else if(parseRule == DeeParser.RULE_INITIALIZER) {
			nodeResult = parseInitializer();
		} else if(parseRule == DeeParser.RULE_STATEMENT) {
			nodeResult = parseStatement();
		} else if(parseRule == DeeParser.RULE_STRUCT_INITIALIZER) {
			nodeResult = parseStructInitializer();
		} else {
			throw assertFail();
		}
		return prepParseResult(parseRule, nodeResult, null);
	}
	
	protected DeeParserResult prepParseResult(ParseRuleDescription parseRule, NodeResult<?> nodeResult, 
			Path modulePath) {
		assertTrue(enabled);
		ASTNode node = nodeResult.node;
		if(node != null) {
			node.setElementReady();
		}
		
		List<ParserError> errors = initErrors(lexerErrors, node);
		if(parseRule == null) {
			Module module = (Module) node;
			return new ParsedModule(getSource(), lexSource.lexElementList, module, nodeResult.ruleBroken, errors, 
				modulePath);
		} else {
			return new DeeParserResult(getSource(), lexSource.lexElementList, node, nodeResult.ruleBroken, errors);
		}
	}
	
	public static List<ParserError> initErrors(ArrayList<ParserError> lexerErrors, ASTNode resultNode) {
		return lexerErrors == null ? null : collectErrors(lexerErrors, resultNode);
	}
	
	// TODO: this could be optimized
	protected static ArrayList<ParserError> collectErrors(final ArrayList<ParserError> errors, ASTNode node) {
		if(node != null) {
			node.accept(new ASTVisitor() {
				@Override
				public void postVisit(ASTNode node) {
					for (ParserError parserError : node.getData().getNodeErrors()) {
						errors.add(parserError);
					}
				}
			});
		}
		Collections.sort(errors, new ErrorSourceRangeComparator());
		return errors;
	}
	
	
}
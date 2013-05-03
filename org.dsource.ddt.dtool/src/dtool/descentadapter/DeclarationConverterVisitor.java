package dtool.descentadapter;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IftypeCondition;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateThisParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import dtool.DToolBundle;
import dtool.ast.ASTNeoNode;
import dtool.ast.DeclList;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.declarations.AbstractConditionalDeclaration.VersionSymbol;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationBasicAttrib.AttributeKinds;
import dtool.ast.declarations.DeclarationDebugVersionSpec;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.declarations.DeclarationPostBlit;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.MissingDeclaration;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.ImportStatic;
import dtool.ast.declarations.IncompleteDeclaration;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.DefinitionAliasDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EnumContainer;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.TemplateAliasParam;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.definitions.TemplateTupleParam;
import dtool.ast.definitions.TemplateTypeParam;
import dtool.ast.definitions.TemplateValueParam;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.parser.DeeTokens;
import dtool.parser.Token;
import dtool.util.ArrayView;

/**
 * Converts from DMD's AST to a nicer AST ("Neo AST")
 */
public abstract class DeclarationConverterVisitor extends RefConverterVisitor {

	@Override
	public boolean visit(Version node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(DebugSymbol elem) {
		VersionSymbol identifier =
			elem.ident != null ?
				connect(DefinitionConverter.sourceRange(elem.ident), 
					new VersionSymbol(DefinitionConverter.convertId(elem.ident).name)) : 
				connect(DefinitionConverter.sourceRange(elem.version), 
					new VersionSymbol(new String(elem.version.value)))
			;
		
		return endAdapt(connect(DefinitionConverter.sourceRange(elem),
			new DeclarationDebugVersionSpec(
				true,
				identifier
			)
		));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.VersionSymbol elem) {
		VersionSymbol identifier = elem.ident != null ?
				connect(DefinitionConverter.sourceRange(elem.ident), 
					new VersionSymbol(DefinitionConverter.convertId(elem.ident).name)) : 
				connect(DefinitionConverter.sourceRange(elem.version), 
					new VersionSymbol(new String(elem.version.value)))
			;
		
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
			new DeclarationDebugVersionSpec(
				false,
				identifier
			)
		));
	}
	
	@Override
	public boolean visit(StaticIfCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(DebugCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(VersionCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(IftypeCondition node) {
		return assertFailHandledDirectly();
	}

	@Override
	public boolean visit(AnonDeclaration node) {
		DeclList body = createDeclList(DescentASTConverter.convertMany(node.decl, ASTNeoNode.class, convContext));
		return endAdapt(connect(DefinitionConverter.sourceRange(node), 
			new DefinitionStruct(new ProtoDefSymbol("", null, null), null, null, body)));
	}

	@Override
	public boolean visit(AttribDeclaration node) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(Modifier node) {
		throw assertFail();
	}

	/*  =======================================================  */

	@Override
	public boolean visit(descent.internal.compiler.parser.Module elem) {
		Assert.fail(); return false;
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.BaseClass elem) {
		return endAdapt(connect(DefinitionConverter.sourceRange(elem.hasNoSourceRangeInfo() ? elem.type : elem), 
			ReferenceConverter.convertType(elem.type, convContext))
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateParameter elem) {
		Assert.fail(); return false; // abstract class
	}

	/*  ---------  Decls  --------  */
	

	@Override
	public boolean visit(descent.internal.compiler.parser.AlignDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList2(elem.decl, convContext);
		SourceRange sr = DefinitionConverter.sourceRange(elem);
		return endAdapt(connect(sr, new DeclarationAlign(null, AttribBodySyntax.COLON, body)));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ConditionalDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	// Helper function for the ImportSelective conversion.
	private static IImportSelectiveSelection createSelectionFragment(IdentifierExp name, IdentifierExp alias) {
		assertTrue(!(name.ident.length == 0));
		RefImportSelection impSelection = connect(DefinitionConverter.sourceRange(name), new RefImportSelection(
			new String(name.ident))
		);
		
		if(alias == null) {
			return impSelection; //implements IImportSelectiveFragment
		}
		else {
			DefUnitTuple dudt = new DefUnitTuple(
				new SourceRange(alias.start, name.getEndPos() - alias.start),
				DefinitionConverter.convertIdToken(alias),
				null
			);
			
			return connect(dudt.sourceRange, new ImportSelectiveAlias(dudt.defSymbol, impSelection));
		}
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.Import elem) {
		int importsNum = 1;
		Import imprt = elem;
		while(imprt.next != null) {
			imprt = imprt.next;
			importsNum++;
		}
		
		// Selective import are at the end		
		IImportFragment[] imports = new IImportFragment[importsNum];
		imprt = elem;
		for(int i = 0; i < importsNum; i++, imprt = imprt.next) {
			
			IImportFragment imprtFragment = null;
			
			// Storing the packages.
			Token[] packages;
			SourceRange sr;
			if (imprt.packages == null) {
				packages = new Token[0];
				sr = DefinitionConverter.sourceRange(imprt.id);
			} else {
				packages = new Token[imprt.packages.size()];
				int idx = 0;
				for (IdentifierExp ie : imprt.packages) {
					packages[idx++] = new Token(DeeTokens.IDENTIFIER, new String(ie.ident), ie.start) ;
				}
				int startPos = imprt.packages.get(0).getStartPos();
				sr = new SourceRange(startPos, imprt.id.getEndPos() - startPos);
			}
			
			if(elem.isstatic) {
				imprtFragment = connect(DefinitionConverter.sourceRange(imprt),  
					new ImportStatic(
					connect(sr, new RefModule(ArrayView.create(packages), new String(imprt.id.ident)))
				));
				//Ignore FQN aliasing for now.
				//Assert.isTrue(imprt.alias == null);
			} else if(imprt.aliasId != null) {
				SourceRange entireRange = DefinitionConverter.sourceRange(imprt);
				DefUnitTuple dudt = new DefUnitTuple(
					new SourceRange(imprt.aliasId.start, entireRange.getEndPos() - imprt.aliasId.start),
					DefinitionConverter.convertIdToken(imprt.aliasId), null
				);
				imprtFragment = connect(dudt.sourceRange, new ImportAlias(
					dudt.defSymbol,
					connect(sr, new RefModule(ArrayView.create(packages), new String(imprt.id.ident)))
				));
			} else {
				ImportContent importContent;
				imprtFragment = importContent = new ImportContent(
					connect(sr, new RefModule(ArrayView.create(packages), new String(imprt.id.ident)))
				);
				importContent.initSourceRange(importContent.moduleRef.getSourceRange());
				
				if(imprt.names != null) {
					assertTrue(imprt.names.size() == imprt.aliases.size());
					assertTrue(imprt.names.size() > 0 );
					IImportSelectiveSelection[] impSelFrags = new IImportSelectiveSelection[imprt.names.size()];
					for(int selFragment = 0; selFragment < imprt.names.size(); selFragment++) {
						impSelFrags[selFragment] = createSelectionFragment(
							imprt.names.get(selFragment),
							imprt.aliases.get(selFragment)
						);
					}
					
					imprtFragment = connect(DefinitionConverter.sourceRange(imprt), 
						new ImportSelective(imprtFragment, ArrayView.create(impSelFrags)
					));
				}
			}
			
			imports[i] = imprtFragment;
		}
		assertTrue(imprt == null);
		
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), new DeclarationImport(
			elem.isstatic, ArrayView.create(imports)))
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InvariantDeclaration elem) {
		return endAdapt(connect(DefinitionConverter.sourceRange(elem),
			new DeclarationInvariant(
				(BlockStatement) StatementConverterVisitor.convertStatement(elem.fbody, convContext))
		));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.LinkDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList2(elem.decl, convContext);
		SourceRange sr = DefinitionConverter.sourceRange(elem);
		Linkage linkage = fromLINK(elem.linkage);
		return endAdapt(connect(sr, new DeclarationLinkage(linkage.name, AttribBodySyntax.SINGLE_DECL, body)));
	}
	
    public static Linkage fromLINK(LINK linkage) {
    	switch (linkage) {
    	case LINKdefault: return null;
    	case LINKd: return Linkage.D;
    	case LINKc: return Linkage.C;
    	case LINKcpp: return Linkage.CPP;
    	case LINKwindows: return Linkage.WINDOWS;
    	case LINKpascal: return Linkage.PASCAL;
    	case LINKsystem: return Linkage.SYSTEM;
		}
    	throw assertUnreachable();
    }
    
	@Override
	public boolean visit(descent.internal.compiler.parser.PragmaDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList2(elem.decl, convContext);
		return endAdapt(connect(DefinitionConverter.sourceRange(elem),
			new DeclarationPragma(
				DefinitionConverter.convertId(elem.ident),
				ExpressionConverter.convertMany(elem.args, convContext),
				AttribBodySyntax.BRACE_BLOCK, body
			)
		));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ProtDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList2(elem.decl, convContext);
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), new DeclarationProtection(
			fromPROT(elem.protection), AttribBodySyntax.BRACE_BLOCK, body)));
	}
	
	public static Protection fromPROT(PROT prot) {
		switch(prot) {
		case PROTprivate: return Protection.PRIVATE;
		case PROTpackage: return Protection.PACKAGE;
		case PROTprotected: return Protection.PROTECTED;
		case PROTpublic: return Protection.PUBLIC;
		case PROTexport: return Protection.EXPORT;
		default: return null;
		}
	}
	
	@Override
	public void endVisit(descent.internal.compiler.parser.ProtDeclaration elem) {
//		DeclarationProtection scDecl = (DeclarationProtection) ret;
//		scDecl.localAnalysis();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList2(elem.decl, convContext);
		AttributeKinds declAttrib = AttributeKinds.FINAL; // WRONG, but dont care, deprecated
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
			new DeclarationBasicAttrib(declAttrib, AttribBodySyntax.BRACE_BLOCK, body)));
	}
	
	@Override
	public void endVisit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
//		DeclarationBasicAttrib scDecl = (DeclarationBasicAttrib) ret;
//		scDecl.processEffectiveModifiers();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.UnitTestDeclaration elem) {
		IStatement stmt = StatementConverterVisitor.convertStatement(elem.fbody, convContext);
		if (stmt instanceof BlockStatement) {
			return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
				new DeclarationUnitTest((BlockStatement) stmt)));
		} else {
			// Syntax errors
			IStatement[] stmts = (stmt == null) ? new IStatement[0] : new IStatement[] { stmt };
			return endAdapt(connect(DefinitionConverter.sourceRange(elem),
				new DeclarationUnitTest(
					connect(DefinitionConverter.sourceRange(elem), new BlockStatement(ArrayView.create(stmts), false)))
				)
			);
		}
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticAssert elem) {
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
			new DeclarationStaticAssert(
				ExpressionConverter.convert(elem.exp, convContext),
				ExpressionConverter.convert(elem.msg, convContext))
			)
		);
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StaticIfDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.AliasThis elem) {
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
			new DeclarationAliasThis(false, new RefIdentifier(new String(elem.ident.ident)))
			)
		);
	}
	
	
	/*  ---------  DEFINITIONS  --------  */

	@Override
	public final boolean visit(descent.internal.compiler.parser.AliasDeclaration elem) {

		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionAliasDecl(
				convertDsymbol.defSymbol,
				(Reference) DescentASTConverter.convertElem(elem.type, convContext)
			)
		);
	}	

	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateDeclaration elem) {
		ArrayView<TemplateParameter> tplParams = 
				DescentASTConverter.convertMany(elem.parameters, TemplateParameter.class, convContext);
		
		if(elem.wrapper && DToolBundle.UNIMPLEMENTED_FUNCTIONALITY) { 
			// This code is disabled until we can fix some ref resolver bugs
			assertTrue(elem.members.size() == 1);
			Dsymbol dsymbol = elem.members.get(0);
			if(dsymbol instanceof AggregateDeclaration) {
				AggregateDeclaration aggrDeclaration = (AggregateDeclaration) dsymbol;
				assertTrue(aggrDeclaration.templated);
				
				if(dsymbol instanceof ClassDeclaration) {
					return convertClass((ClassDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof InterfaceDeclaration) {  // BUG HERE
					return convertInterface((InterfaceDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof StructDeclaration) {
					return convertStruct((StructDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof UnionDeclaration) {
					return convertUnion((UnionDeclaration) dsymbol, tplParams);
				}
			}
		}
		
		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionTemplate(
				false,
				convertDsymbol.defSymbol,
				tplParams,
				null,
				createDeclList(DescentASTConverter.convertManyNoNulls(elem.members, ASTNeoNode.class, convContext))
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateMixin elem) {
		return endAdapt(ReferenceConverter.convertMixinInstance(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypedefDeclaration elem) {
		return endAdapt(
			new DefinitionTypedef(
				DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				ReferenceConverter.convertType(elem.sourceBasetype, convContext),
				DescentASTConverter.convertElem(elem.init, Initializer.class, convContext)
			)
		);
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.VarDeclaration elem) {
		if(elem.ident == null) {
//			return endAdapt(new InvalidSyntaxDeclaration_Old(
//				DefinitionConverter.sourceRange(elem), 
//				ArrayView.create(array(
//						ReferenceConverter.convertType(elem.type, convContext),
//						DescentASTConverter.convertElem(elem.init, Initializer.class, convContext)
//				))
//			));
			return endAdapt(DefinitionConverter.sourceRange(elem),
				new MissingDeclaration());
		} 
		
		Reference typeRef = ReferenceConverter.convertType(elem.type, convContext);
		if(typeRef == null) {
			DefUnitTuple defUnitInfo = DefinitionConverter.convertDsymbol(elem, convContext);
			return endAdapt(defUnitInfo.sourceRange,
				new DefinitionVariable.DefinitionAutoVariable(
				defUnitInfo.defSymbol,
				DescentASTConverter.convertElem(elem.init, Initializer.class, convContext), null
			));
		} else {
			DefUnitTuple defUnitInfo = DefinitionConverter.convertDsymbol(elem, convContext);
			return endAdapt(defUnitInfo.sourceRange, 
				new DefinitionVariable(
				defUnitInfo.defSymbol,
				typeRef,
				DescentASTConverter.convertElem(elem.init, Initializer.class, convContext), null
			));
		}
	}
	
	public static ASTNeoNode convertEnumDecl(EnumDeclaration elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			DefUnitTuple defUnitTuple = DefinitionConverter.convertDsymbol(elem, convContext);
			return connect(defUnitTuple.sourceRange, 
				new DefinitionEnum(
				defUnitTuple.defSymbol,
				ReferenceConverter.convertType(elem.memtype, convContext),
				new DefinitionEnum.EnumBody(DescentASTConverter.convertMany(elem.members, EnumMember.class, convContext), false)
			));
		} else {
			return connect(DefinitionConverter.sourceRange(elem), new EnumContainer(
				DescentASTConverter.convertMany(elem.members, EnumMember.class, convContext),
				ReferenceConverter.convertType(elem.memtype, convContext)
			));
		}
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumDeclaration elem) {
		return endAdapt(convertEnumDecl(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumMember elem) {
		return endAdapt(DefinitionConverter.createEnumMember(elem, convContext));
	}

	/* aggregates */
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.ClassDeclaration elem) {
		return convertClass(elem, null);
	}
	
	private boolean convertClass(descent.internal.compiler.parser.ClassDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionClass(
				convertDsymbol.defSymbol,
				tplParams,
				null,
				DescentASTConverter.convertMany(elem.sourceBaseclasses, Reference.class, convContext),
				createDeclList(DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext))
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InterfaceDeclaration elem) {
		return convertInterface(elem, null);
	}
	
	private boolean convertInterface(descent.internal.compiler.parser.InterfaceDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionInterface(
				convertDsymbol.defSymbol,
				tplParams,
				null,
				DescentASTConverter.convertMany(elem.sourceBaseclasses, Reference.class, convContext),
				createDeclList(DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext))
			)
		);
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.StructDeclaration elem) {
		return convertStruct(elem, null);
	}
	
	private boolean convertStruct(descent.internal.compiler.parser.StructDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionStruct(
				convertDsymbol.defSymbol,
				tplParams,
				null,
				createDeclList(DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext))
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.UnionDeclaration elem) {
		return convertUnion(elem, null);
	}
	
	private boolean convertUnion(descent.internal.compiler.parser.UnionDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		DefUnitTuple convertDsymbol = DefinitionConverter.convertDsymbol(elem, convContext);
		return endAdapt(convertDsymbol.sourceRange,
			new DefinitionUnion(
				convertDsymbol.defSymbol,
				tplParams,
				null,
				createDeclList(DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext))
			)
		);
	}
	
	
	/* --- func --- */

	@Override
	public boolean visit(descent.internal.compiler.parser.FuncDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionFunction(elem, convContext));
	}
	
	@Override
	public boolean visit(Argument elem) {
		return endAdapt(DefinitionConverter.convertFunctionParameter(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.CtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.PostBlitDeclaration elem) {
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), 
			new DeclarationPostBlit(
				DefinitionConverter.convertFnBody(elem, convContext)
				)
			)
		);
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.DtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticCtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}	
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticDtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.NewDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.DeleteDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}

	/* ---- other ---- */
	

	@Override
	public boolean visit(TemplateAliasParameter elem) {
		DefUnitTuple dudt = new DefUnitTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(dudt.sourceRange, new TemplateAliasParam(dudt.defSymbol, null, null));
	}
	
	@Override
	public boolean visit(TemplateTupleParameter elem) {
		DefUnitTuple dudt = new DefUnitTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(dudt.sourceRange, new TemplateTupleParam(dudt.defSymbol));
	}
	
	@Override
	public boolean visit(TemplateTypeParameter elem) {
		DefUnitTuple dudt = new DefUnitTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(dudt.sourceRange,
			new TemplateTypeParam(
				dudt.defSymbol,
				ReferenceConverter.convertType(elem.specType, convContext),
				ReferenceConverter.convertType(elem.defaultType, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(TemplateThisParameter elem) {
		// TODO: There was something to do with this...
		return visit((TemplateTypeParameter) elem);
	}
	
	@Override
	public boolean visit(TemplateValueParameter elem) {
		DefUnitTuple dudt = new DefUnitTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(dudt.sourceRange,
			new TemplateValueParam(
				dudt.defSymbol,
				ReferenceConverter.convertType(elem.valType, convContext),
				ExpressionConverter.convert(elem.specValue, convContext),
				ExpressionConverter.convert(elem.defaultValue, convContext)
			)
		);
	}

}

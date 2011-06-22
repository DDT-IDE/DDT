package dtool.descentadapter;


import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.IftypeCondition;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateThisParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAnonMember;
import dtool.ast.declarations.DeclarationConditionalDefinition;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.DeclarationStorageClass;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.BaseClass;
import dtool.ast.definitions.DefModifier;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionPostBlit;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.TemplateParamAlias;
import dtool.ast.definitions.TemplateParamTuple;
import dtool.ast.definitions.TemplateParamType;
import dtool.ast.definitions.TemplateParamValue;
import dtool.ast.references.ReferenceConverter;

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
		return endAdapt(new DeclarationConditionalDefinition(elem));
	}
	
	@Override
	public boolean visit(VersionSymbol elem) {
		return endAdapt(new DeclarationConditionalDefinition(elem));
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
		return endAdapt(new DeclarationAnonMember(node, convContext));
	}

	@Override
	public boolean visit(AttribDeclaration node) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(Modifier node) {
		return endAdapt(new DefModifier(node));
	}

	/*  =======================================================  */

	@Override
	public boolean visit(descent.internal.compiler.parser.Module elem) {
		Assert.fail(); return false;
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.BaseClass elem) {
		return endAdapt(new BaseClass(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateParameter elem) {
		Assert.fail(); return false; // abstract class
	}

	/*  ---------  Decls  --------  */
	

	@Override
	public boolean visit(descent.internal.compiler.parser.AlignDeclaration elem) {
		return endAdapt(new DeclarationAlign(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ConditionalDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.Import elem) {
		return endAdapt(new DeclarationImport(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InvariantDeclaration elem) {
		return endAdapt(new DeclarationInvariant(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.LinkDeclaration elem) {
		return endAdapt(new DeclarationLinkage(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.PragmaDeclaration elem) {
		return endAdapt(new DeclarationPragma(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ProtDeclaration elem) {
		return endAdapt(new DeclarationProtection(elem, convContext));
	}

	@Override
	public void endVisit(descent.internal.compiler.parser.ProtDeclaration elem) {
		DeclarationProtection scDecl = (DeclarationProtection) ret;
		scDecl.processEffectiveModifiers();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		return endAdapt(new DeclarationStorageClass(elem, convContext));
	}
	
	@Override
	public void endVisit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		DeclarationStorageClass scDecl = (DeclarationStorageClass) ret;
		scDecl.processEffectiveModifiers();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.UnitTestDeclaration elem) {
		return endAdapt(new DeclarationUnitTest(elem, convContext));
	}


	@Override
	public boolean visit(descent.internal.compiler.parser.StaticAssert elem) {
		return endAdapt(new DeclarationStaticAssert(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StaticIfDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.AliasThis elem) {
		return endAdapt(new DeclarationAliasThis(elem));
	}

	
	
	/*  ---------  DEFINITIONS  --------  */

	@Override
	public final boolean visit(descent.internal.compiler.parser.AliasDeclaration elem) {
		return endAdapt(new DefinitionAlias(elem, convContext));
	}	

	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateDeclaration elem) {
		return endAdapt(new DefinitionTemplate(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateMixin elem) {
		return endAdapt(ReferenceConverter.convertMixinInstance(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypedefDeclaration elem) {
		return endAdapt(new DefinitionTypedef(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.VarDeclaration elem) {
		return endAdapt(DefinitionVariable.convert(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumDeclaration elem) {
		return endAdapt(DefinitionEnum.convertEnumDecl(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumMember elem) {
		return endAdapt(DefinitionConverter.createEnumMember(elem, convContext));
	}

	/* agregates */
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.ClassDeclaration elem) {
		return endAdapt(new DefinitionClass(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InterfaceDeclaration elem) {
		return endAdapt(new DefinitionInterface(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.StructDeclaration elem) {
		return endAdapt(new DefinitionStruct(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.UnionDeclaration elem) {
		return endAdapt(new DefinitionUnion(elem, convContext));
	}	
	
	
	/* --- func --- */

	@Override
	public boolean visit(descent.internal.compiler.parser.FuncDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem, convContext));
	}
	@Override
	public boolean visit(Argument elem) {
		return endAdapt(DefinitionFunction.convertFunctionParameter(elem, convContext));
	}
	

	@Override
	public boolean visit(descent.internal.compiler.parser.CtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.PostBlitDeclaration elem) {
		return endAdapt(new DefinitionPostBlit(elem, convContext));
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
		return endAdapt(new TemplateParamAlias(elem));
	}
	
	@Override
	public boolean visit(TemplateTupleParameter elem) {
		return endAdapt(new TemplateParamTuple(elem));
	}
	
	@Override
	public boolean visit(TemplateTypeParameter elem) {
		return endAdapt(new TemplateParamType(elem, convContext));
	}
	
	@Override
	public boolean visit(TemplateThisParameter elem) {
		return endAdapt(new TemplateParamType(elem, convContext));
	}
	
	@Override
	public boolean visit(TemplateValueParameter elem) {
		return endAdapt(new TemplateParamValue(elem, convContext));
	}

}

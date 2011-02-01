package dtool.descentadapter;

import descent.internal.compiler.parser.*;
import descent.internal.compiler.parser.Package;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.ASTRangeLessNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

@SuppressWarnings("unused")
public abstract class CoreConverter extends ASTCommonConverter {

	@Override
	public boolean visit(BoolExp node) {
		return assertFailFAKENODE();
	}
	@Override
	public boolean visit(DotExp node) {
		return assertFailFAKENODE();
	}
	@Override
	public boolean visit(DotTemplateExp node) {
		return assertFailFAKENODE();
	}
	@Override
	public boolean visit(ComplexExp node) {
		return assertFailFAKENODE();
	}
	
	
	@Override
	public boolean visit(DotVarExp node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(DsymbolExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(TupleExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(DotTypeExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(AnonymousAggregateDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(LabelDsymbol node) {
		return assertFailFAKENODE();
	}


	@Override
	public boolean visit(FuncAliasDeclaration node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(ArrayScopeSymbol node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(ClassInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(DelegateExp node) {
		return assertFailFAKENODE();
	}



	@Override
	public boolean visit(TupleDeclaration node) {
		return assertFailFAKENODE();
	}
	@Override
	public boolean visit(TemplateExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(ThisDeclaration node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(Tuple node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(TypeTypedef node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(TypeClass node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeEnum node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeTuple node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeStruct elem) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(RemoveExp node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(SymOffExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(WithScopeSymbol node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(VarExp node) {
		return assertFailFAKENODE();
	}
	@Override
	public boolean visit(HaltExp node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(UnrolledLoopStatement node) {
		return assertFailFAKENODE();
	}
	
	@Override
	public boolean visit(ModuleInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	
	@Override
	public boolean visit(TypeInfoArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoAssociativeArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoClassDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoDelegateDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoEnumDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoFunctionDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoInterfaceDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoPointerDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoStaticArrayDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoStructDeclaration node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(TypeInfoTypedefDeclaration node) {
		return assertFailFAKENODE();
	}


	/*  =======================================================  */

	public boolean visit(IASTNode elem) {
		return assertFailABSTRACT_NODE();
	}
	public void endVisit(IASTNode node) {
	}
	
	@Override
	public boolean visit(ASTNode elem) {
		return assertFailABSTRACT_NODE();
	}
	@Override
	public void endVisit(ASTNode node) {
	}
	
	@Override
	public boolean visit(ASTDmdNode elem) {
		return assertFailABSTRACT_NODE();
	}
	
	
	public boolean visit(ASTRangeLessNode elem) {
		return assertFailABSTRACT_NODE();
	}


	@Override
	public boolean visit(Dsymbol elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Declaration elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Initializer elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(AggregateDeclaration elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Statement elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Type elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Expression elem) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(DefaultInitExp elem) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(ModuleDeclaration elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(ScopeDsymbol elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(TypeQualified elem) {
		return assertFailABSTRACT_NODE();
	}

	@Override
	public boolean visit(Package elem) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(Condition node) {
		return assertFailABSTRACT_NODE();
	}
	/* === */
	
	@Override
	public void endVisit(AddAssignExp node) {
	}

	@Override
	public void endVisit(AddExp node) {
	}

	@Override
	public void endVisit(AddrExp node) {
	}

	@Override
	public void endVisit(AggregateDeclaration node) {
	}

	@Override
	public void endVisit(AliasDeclaration node) {
	}
	
	@Override
	public void endVisit(AliasThis node) {
	}

	@Override
	public void endVisit(AlignDeclaration node) {
	}

	@Override
	public void endVisit(AndAndExp node) {
	}

	@Override
	public void endVisit(AndAssignExp node) {
	}

	@Override
	public void endVisit(AndExp node) {
	}

	@Override
	public void endVisit(AnonDeclaration node) {
	}

	@Override
	public void endVisit(AnonymousAggregateDeclaration node) {
	}

	@Override
	public void endVisit(Argument node) {
	}

	@Override
	public void endVisit(ArrayExp node) {
	}

	@Override
	public void endVisit(ArrayInitializer node) {
	}
	
	@Override
	public void endVisit(ArrayLengthExp node) {
	}
	
	@Override
	public void endVisit(ArrayLiteralExp node) {
	}

	@Override
	public void endVisit(ArrayScopeSymbol node) {
	}

	@Override
	public void endVisit(AsmBlock node) {
	}

	@Override
	public void endVisit(AsmStatement node) {
	}

	@Override
	public void endVisit(AssertExp node) {
	}

	@Override
	public void endVisit(AssignExp node) {
	}

	@Override
	public void endVisit(AssocArrayLiteralExp node) {
	}

	@Override
	public void endVisit(AttribDeclaration node) {
	}

	@Override
	public void endVisit(BaseClass node) {
	}

	@Override
	public void endVisit(BinExp node) {
	}

	@Override
	public void endVisit(BoolExp node) {
	}

	@Override
	public void endVisit(BreakStatement node) {
	}

	@Override
	public void endVisit(CallExp node) {
	}

	@Override
	public void endVisit(CaseStatement node) {
	}
	
	@Override
	public void endVisit(CaseRangeStatement node) {
	}
	
	@Override
	public void endVisit(CastExp node) {
	}

	@Override
	public void endVisit(CatAssignExp node) {
	}

	@Override
	public void endVisit(Catch node) {
	}

	@Override
	public void endVisit(CatExp node) {
	}

	@Override
	public void endVisit(ClassDeclaration node) {
	}

	@Override
	public void endVisit(ClassInfoDeclaration node) {
	}

	@Override
	public void endVisit(CmpExp node) {
	}

	@Override
	public void endVisit(ComExp node) {
	}

	@Override
	public void endVisit(CommaExp node) {
	}

	@Override
	public void endVisit(CompileDeclaration node) {
	}

	@Override
	public void endVisit(CompileExp node) {
	}

	@Override
	public void endVisit(CompileStatement node) {
	}

	@Override
	public void endVisit(ComplexExp node) {
	}

	@Override
	public void endVisit(CompoundStatement node) {
	}

	@Override
	public void endVisit(CondExp node) {
	}

	@Override
	public void endVisit(Condition node) {
	}

	@Override
	public void endVisit(ConditionalDeclaration node) {
	}

	@Override
	public void endVisit(ConditionalStatement node) {
	}

	@Override
	public void endVisit(ContinueStatement node) {
	}

	@Override
	public void endVisit(CtorDeclaration node) {
	}

	@Override
	public void endVisit(DebugCondition node) {
	}

	@Override
	public void endVisit(DebugSymbol node) {
	}

	@Override
	public void endVisit(Declaration node) {
	}

	@Override
	public void endVisit(DeclarationExp node) {
	}

	@Override
	public void endVisit(DeclarationStatement node) {
	}
	
	@Override
	public void endVisit(DefaultInitExp node) {
	}

	@Override
	public void endVisit(DefaultStatement node) {
	}

	@Override
	public void endVisit(DelegateExp node) {
	}

	@Override
	public void endVisit(DeleteDeclaration node) {
	}

	@Override
	public void endVisit(DeleteExp node) {
	}

	@Override
	public void endVisit(DivAssignExp node) {
	}

	@Override
	public void endVisit(DivExp node) {
	}

	@Override
	public void endVisit(DollarExp node) {
	}

	@Override
	public void endVisit(DoStatement node) {

		
	}

	@Override
	public void endVisit(DotExp node) {

		
	}

	@Override
	public void endVisit(DotIdExp node) {

		
	}

	@Override
	public void endVisit(DotTemplateExp node) {

		
	}

	@Override
	public void endVisit(DotTemplateInstanceExp node) {

		
	}

	@Override
	public void endVisit(DotTypeExp node) {

		
	}

	@Override
	public void endVisit(DotVarExp node) {

		
	}

	@Override
	public void endVisit(Dsymbol node) {

		
	}

	@Override
	public void endVisit(DsymbolExp node) {

		
	}

	@Override
	public void endVisit(DtorDeclaration node) {

		
	}

	@Override
	public void endVisit(EnumDeclaration node) {

		
	}

	@Override
	public void endVisit(EnumMember node) {

		
	}

	@Override
	public void endVisit(EqualExp node) {

		
	}

	@Override
	public void endVisit(ExpInitializer node) {

		
	}

	@Override
	public void endVisit(Expression node) {

		
	}

	@Override
	public void endVisit(ExpStatement node) {

		
	}

	@Override
	public void endVisit(FileExp node) {
	}
	
	@Override
	public void endVisit(FileInitExp node) {
	}

	@Override
	public void endVisit(ForeachRangeStatement node) {

		
	}

	@Override
	public void endVisit(ForeachStatement node) {

		
	}

	@Override
	public void endVisit(ForStatement node) {

		
	}

	@Override
	public void endVisit(FuncAliasDeclaration node) {

		
	}

	@Override
	public void endVisit(FuncDeclaration node) {

		
	}

	@Override
	public void endVisit(FuncExp node) {

		
	}

	@Override
	public void endVisit(FuncLiteralDeclaration node) {

		
	}

	@Override
	public void endVisit(GotoCaseStatement node) {

		
	}

	@Override
	public void endVisit(GotoDefaultStatement node) {

		
	}

	@Override
	public void endVisit(GotoStatement node) {

		
	}

	@Override
	public void endVisit(HaltExp node) {

		
	}

	@Override
	public void endVisit(IdentifierExp node) {

		
	}

	@Override
	public void endVisit(IdentityExp node) {

		
	}

	@Override
	public void endVisit(IfStatement node) {

		
	}

	@Override
	public void endVisit(IftypeCondition node) {

		
	}

	@Override
	public void endVisit(IsExp node) {
		
	}

	@Override
	public void endVisit(Import node) {
		
	}

	@Override
	public void endVisit(IndexExp node) {

		
	}

	@Override
	public void endVisit(InExp node) {

		
	}

	@Override
	public void endVisit(Initializer node) {

		
	}

	@Override
	public void endVisit(IntegerExp node) {

		
	}

	@Override
	public void endVisit(InterfaceDeclaration node) {

		
	}

	@Override
	public void endVisit(InvariantDeclaration node) {

		
	}

	@Override
	public void endVisit(LabelDsymbol node) {

		
	}

	@Override
	public void endVisit(LabelStatement node) {
	}
	
	@Override
	public void endVisit(LineInitExp node) {
	}

	@Override
	public void endVisit(LinkDeclaration node) {

		
	}

	@Override
	public void endVisit(MinAssignExp node) {

		
	}

	@Override
	public void endVisit(MinExp node) {

		
	}

	@Override
	public void endVisit(ModAssignExp node) {

		
	}

	@Override
	public void endVisit(ModExp node) {

		
	}

	@Override
	public void endVisit(Modifier node) {

		
	}

	@Override
	public void endVisit(Module node) {

		
	}

	@Override
	public void endVisit(ModuleDeclaration node) {

		
	}

	@Override
	public void endVisit(ModuleInfoDeclaration node) {

		
	}

	@Override
	public void endVisit(MulAssignExp node) {

		
	}

	@Override
	public void endVisit(MulExp node) {

		
	}

	@Override
	public void endVisit(NegExp node) {

		
	}

	@Override
	public void endVisit(NewAnonClassExp node) {

		
	}

	@Override
	public void endVisit(NewDeclaration node) {

		
	}

	@Override
	public void endVisit(NewExp node) {

		
	}

	@Override
	public void endVisit(NotExp node) {

		
	}

	@Override
	public void endVisit(NullExp node) {

		
	}

	@Override
	public void endVisit(OnScopeStatement node) {

		
	}

	@Override
	public void endVisit(OrAssignExp node) {

		
	}

	@Override
	public void endVisit(OrExp node) {

		
	}

	@Override
	public void endVisit(OrOrExp node) {

		
	}

	@Override
	public void endVisit(Package node) {
	}

	@Override
	public void endVisit(PostBlitDeclaration node) {
	}

	@Override
	public void endVisit(PostExp node) {

		
	}

	@Override
	public void endVisit(PragmaDeclaration node) {

		
	}

	@Override
	public void endVisit(PragmaStatement node) {

		
	}

	@Override
	public void endVisit(ProtDeclaration node) {

		
	}

	@Override
	public void endVisit(PtrExp node) {

		
	}

	@Override
	public void endVisit(RealExp node) {

		
	}

	@Override
	public void endVisit(RemoveExp node) {

		
	}

	@Override
	public void endVisit(ReturnStatement node) {

		
	}

	@Override
	public void endVisit(ScopeDsymbol node) {

		
	}

	@Override
	public void endVisit(ScopeExp node) {

		
	}

	@Override
	public void endVisit(ScopeStatement node) {

		
	}

	@Override
	public void endVisit(ShlAssignExp node) {

		
	}

	@Override
	public void endVisit(ShlExp node) {

		
	}

	@Override
	public void endVisit(ShrAssignExp node) {

		
	}

	@Override
	public void endVisit(ShrExp node) {

		
	}

	@Override
	public void endVisit(SliceExp node) {

		
	}

	@Override
	public void endVisit(Statement node) {

		
	}

	@Override
	public void endVisit(StaticAssert node) {

		
	}

	@Override
	public void endVisit(StaticAssertStatement node) {

		
	}

	@Override
	public void endVisit(StaticCtorDeclaration node) {

		
	}

	@Override
	public void endVisit(StaticDtorDeclaration node) {

		
	}

	@Override
	public void endVisit(StaticIfCondition node) {

		
	}

	@Override
	public void endVisit(StaticIfDeclaration node) {

		
	}

	@Override
	public void endVisit(StorageClassDeclaration node) {

		
	}

	@Override
	public void endVisit(StringExp node) {

		
	}

	@Override
	public void endVisit(StructDeclaration node) {

		
	}

	@Override
	public void endVisit(StructInitializer node) {

		
	}

	@Override
	public void endVisit(SuperExp node) {

		
	}

	@Override
	public void endVisit(SwitchStatement node) {

		
	}

	@Override
	public void endVisit(SymOffExp node) {

		
	}

	@Override
	public void endVisit(SynchronizedStatement node) {

		
	}

	@Override
	public void endVisit(TemplateAliasParameter node) {

		
	}

	@Override
	public void endVisit(TemplateDeclaration node) {

		
	}

	@Override
	public void endVisit(TemplateExp node) {

		
	}

	@Override
	public void endVisit(TemplateInstance node) {

		
	}

	@Override
	public void endVisit(TemplateInstanceWrapper node) {

		
	}

	@Override
	public void endVisit(TemplateMixin node) {

		
	}

	@Override
	public void endVisit(TemplateParameter node) {
	}
	
	@Override
	public void endVisit(TemplateThisParameter node) {
	}

	@Override
	public void endVisit(TemplateTupleParameter node) {

		
	}

	@Override
	public void endVisit(TemplateTypeParameter node) {

		
	}

	@Override
	public void endVisit(TemplateValueParameter node) {

		
	}

	@Override
	public void endVisit(ThisDeclaration node) {

		
	}

	@Override
	public void endVisit(ThisExp node) {

		
	}

	@Override
	public void endVisit(ThrowStatement node) {

		
	}

	@Override
	public void endVisit(TraitsExp node) {

		
	}

	@Override
	public void endVisit(TryCatchStatement node) {

		
	}

	@Override
	public void endVisit(TryFinallyStatement node) {

		
	}

	@Override
	public void endVisit(Tuple node) {

		
	}

	@Override
	public void endVisit(TupleDeclaration node) {

		
	}

	@Override
	public void endVisit(TupleExp node) {

		
	}

	@Override
	public void endVisit(Type node) {

		
	}

	@Override
	public void endVisit(TypeAArray node) {

		
	}

	@Override
	public void endVisit(TypeBasic node) {

		
	}

	@Override
	public void endVisit(TypeClass node) {

		
	}

	@Override
	public void endVisit(TypeDArray node) {

		
	}

	@Override
	public void endVisit(TypedefDeclaration node) {

		
	}

	@Override
	public void endVisit(TypeDelegate node) {
	}

	@Override
	public void endVisit(TypeEnum node) {
	}

	@Override
	public void endVisit(TypeExp node) {
	}

	@Override
	public void endVisit(TypeFunction node) {
	}

	@Override
	public void endVisit(TypeIdentifier node) {
	}

	@Override
	public void endVisit(TypeidExp node) {
	}

	@Override
	public void endVisit(TypeInfoArrayDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoAssociativeArrayDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoClassDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoDelegateDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoEnumDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoFunctionDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoInterfaceDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoPointerDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoStaticArrayDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoStructDeclaration node) {
	}

	@Override
	public void endVisit(TypeInfoTypedefDeclaration node) {
	}

	@Override
	public void endVisit(TypeInstance node) {
	}

	@Override
	public void endVisit(TypePointer node) {
	}

	@Override
	public void endVisit(TypeQualified node) {
	}
	
	@Override
	public void endVisit(TypeReturn node) {
	}

	@Override
	public void endVisit(TypeSArray node) {
	}

	@Override
	public void endVisit(TypeSlice node) {
	}

	@Override
	public void endVisit(TypeStruct node) {
	}

	@Override
	public void endVisit(TypeTuple node) {
	}

	@Override
	public void endVisit(TypeTypedef node) {
	}

	@Override
	public void endVisit(TypeTypeof node) {
	}

	@Override
	public void endVisit(UAddExp node) {
	}

	@Override
	public void endVisit(UnaExp node) {
	}

	@Override
	public void endVisit(UnionDeclaration node) {
	}

	@Override
	public void endVisit(UnitTestDeclaration node) {
	}

	@Override
	public void endVisit(UnrolledLoopStatement node) {
	}

	@Override
	public void endVisit(UshrAssignExp node) {
	}

	@Override
	public void endVisit(UshrExp node) {
	}

	@Override
	public void endVisit(VarDeclaration node) {
	}

	@Override
	public void endVisit(VarExp node) {
	}

	@Override
	public void endVisit(Version node) {
	}

	@Override
	public void endVisit(VersionCondition node) {
	}

	@Override
	public void endVisit(VersionSymbol node) {
	}

	@Override
	public void endVisit(VoidInitializer node) {
	}

	@Override
	public void endVisit(VolatileStatement node) {
	}

	@Override
	public void endVisit(WhileStatement node) {
	}

	@Override
	public void endVisit(WithScopeSymbol node) {
	}

	@Override
	public void endVisit(WithStatement node) {
	}

	@Override
	public void endVisit(XorAssignExp node) {
	}

	@Override
	public void endVisit(XorExp node) {
	}


}
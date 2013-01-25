Ⓗ▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

pragma(foo) void foo;
pragma(foo) void foo;
// pragma(foo, 1, 2) void foo; // TODO expression list

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(DefinitionVariable(RefPrimitive DefSymbol))
DeclarationPragma(DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
pragma(foo) : 
int foo;
void bar;
pragma(foo) {
	int foo;
}

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(
	DefinitionVariable(RefPrimitive DefSymbol) DefinitionVariable(RefPrimitive DefSymbol)
	DeclarationPragma(
		DefinitionVariable(RefPrimitive DefSymbol)
	)
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
pragma(foo) #error(EXPRULE_decl)

#AST_STRUCTURE_EXPECTED:
DeclarationPragma()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
pragma(foo) ;

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(DeclarationEmpty)

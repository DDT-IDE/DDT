Ⓗ▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

pragma(foo) void foo;
pragma(foo) void foo;
// pragma(foo, 1, 2) void foo; // TODO

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(MiscDeclaration)
DeclarationPragma(MiscDeclaration)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
pragma(foo) : 
int foo;
void bar;
pragma(foo) {
	int foo;
}

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(
	MiscDeclaration MiscDeclaration
	DeclarationPragma(
		MiscDeclaration
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

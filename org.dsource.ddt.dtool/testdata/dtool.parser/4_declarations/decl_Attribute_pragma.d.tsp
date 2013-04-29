Ⓗ▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

pragma(foo) void foo;
pragma(foo) void foo;
// pragma(foo, 1, 2) void foo; // TODO expression list

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol DefinitionVariable(RefPrimitive DefSymbol))
DeclarationPragma(Symbol DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
pragma(foo) : 
#@DECLS_SAMPLE
pragma(foo) {
	#@DECLS_SAMPLE
}

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol DeclList( #@DECLS_SAMPLE DeclarationPragma(Symbol DeclList( #@DECLS_SAMPLE )) ))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
pragma(foo) #error(EXPRULE_decl)

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
pragma(foo) ;

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol DeclarationEmpty)

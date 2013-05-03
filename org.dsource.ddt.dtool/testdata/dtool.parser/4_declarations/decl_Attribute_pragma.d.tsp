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
#@DECL_LIST_SAMPLE
pragma(foo) {
	#@DECL_LIST_SAMPLE
}

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol DeclList( #@DECL_LIST_SAMPLE DeclarationPragma(Symbol DeclList( #@DECL_LIST_SAMPLE )) ))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
pragma(foo) #@NO_DECLorBLOCK

#STRUCTURE_EXPECTED: DeclarationPragma(Symbol #@NO_DECLorBLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
pragma(foo) ;

#STRUCTURE_EXPECTED:
DeclarationPragma(Symbol DeclarationEmpty)

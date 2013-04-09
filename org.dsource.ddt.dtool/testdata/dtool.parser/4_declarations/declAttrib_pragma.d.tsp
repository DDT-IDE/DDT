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
#@DECLS_SAMPLE
pragma(foo) {
	#@DECLS_SAMPLE
}

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(NodeList2( #@DECLS_SAMPLE DeclarationPragma(NodeList2( #@DECLS_SAMPLE )) ))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
pragma(foo) #error(EXPRULE_decl)

#AST_STRUCTURE_EXPECTED:
DeclarationPragma()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
pragma(foo) ;

#AST_STRUCTURE_EXPECTED:
DeclarationPragma(DeclarationEmpty)

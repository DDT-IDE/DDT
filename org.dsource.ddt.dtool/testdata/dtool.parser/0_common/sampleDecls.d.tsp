Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@SINGLE_DECL_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
】》

#@DECL_LIST_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK__SAMPLE《#?STRUCTURE_EXPECTED!【 { #@DECL_LIST_SAMPLE } ● DeclList(#@DECL_LIST_SAMPLE)】》


#@NO_DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#error(EXP_OPEN_BRACE)【】 ● 】●
¤》

#@DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#@NO_DECLS_BLOCK #@BREAK ● #@BREAK 】●
  ►#?STRUCTURE_EXPECTED!【{ } ● DeclList( )】●
  ►#?STRUCTURE_EXPECTED!【{ int foo; #@NO_CloseBRACE #@BREAK ●DeclList( DefinitionVariable(? DefSymbol) #@BREAK )】●
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK__SAMPLE●#@DECLS_BLOCK__SAMPLE】●
¤》

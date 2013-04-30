Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECLS_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK__SAMPLE《#?STRUCTURE_EXPECTED!【 { #@DECLS_SAMPLE } ● DeclList(#@DECLS_SAMPLE)】》

#@DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#error(EXP_OPEN_BRACE)【】 #@BREAK ● #@BREAK 】●
  ►#?STRUCTURE_EXPECTED!【{ } ● DeclList( )】●
  ►#?STRUCTURE_EXPECTED!【{ int foo; #@NO_CloseBRACE #@BREAK ●DeclList( DefinitionVariable(? DefSymbol) #@BREAK )】●
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK__SAMPLE●#@DECLS_BLOCK__SAMPLE】●
¤》

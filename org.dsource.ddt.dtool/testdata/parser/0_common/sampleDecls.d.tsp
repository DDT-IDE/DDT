Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@SINGLE_DECL_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
】》

#@DECL_LIST_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK__SAMPLE《#?STRUCTURE_EXPECTED!【 { #@DECL_LIST_SAMPLE } ● DeclBlock(#@DECL_LIST_SAMPLE)】》


#@NO_DECLorBLOCK《
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_DeclOrBlock)● MissingDeclaration】●
¤》

#@DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#@NO_DECLS_BLOCK #@BREAK ● #@BREAK 】●
  ►#@DECLS_BLOCK__NO_MISSING●
¤》
#@NO_DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_DeclarationBlock) ● 】●
¤》

#@DECLS_BLOCK__NO_MISSING《
  ►#?STRUCTURE_EXPECTED!【{ } ● DeclBlock( )】●
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK__SAMPLE●#@DECLS_BLOCK__SAMPLE】●
  ►#@DECLS_BLOCK__BROKEN●
¤》
#@DECLS_BLOCK__BROKEN《
  ►#?STRUCTURE_EXPECTED!【{ int foo; #@NO_CloseBRACE #@BREAK ●DeclBlock( DefinitionVariable(? DefSymbol) #@BREAK )】●
¤》
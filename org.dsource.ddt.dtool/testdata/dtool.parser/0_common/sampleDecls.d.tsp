Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECLS_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK_SAMPLE《#?STRUCTURE_EXPECTED!【 { #@DECLS_SAMPLE } ● DeclList(#@DECLS_SAMPLE)】》

#@DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK_SAMPLE●#@DECLS_BLOCK_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【{ } ● DeclList( )】●
  ►#?STRUCTURE_EXPECTED!【{ int foo; #error(EXP_CLOSE_BRACE) #@BREAK ●
    DeclList( DefinitionVariable(? DefSymbol) #@BREAK )】●
¤》
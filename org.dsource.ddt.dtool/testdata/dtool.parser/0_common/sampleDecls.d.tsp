Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECLS_SAMPLE《#?STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK_SAMPLE《#?STRUCTURE_EXPECTED!【 { #@DECLS_SAMPLE } ● NodeList2(#@DECLS_SAMPLE)】》

#@DECLS_BLOCK《
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK_SAMPLE●#@DECLS_BLOCK_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【{ } ● NodeList2( )】●
  ►#?STRUCTURE_EXPECTED!【{ int foo; #error(EXP_CLOSE_BRACE) #@BREAK ●
    NodeList2( DefinitionVariable(? DefSymbol) #@BREAK )】●
¤》
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECLS_SAMPLE《#?AST_STRUCTURE_EXPECTED!【 int foo; extern(C) Foo foo; ● 
  DefinitionVariable(RefPrimitive DefSymbol) 
  DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
】》

#@DECLS_BLOCK_SAMPLE《#?AST_STRUCTURE_EXPECTED!【 { #@DECLS_SAMPLE } ● NodeList2(#@DECLS_SAMPLE)】》

#@DECLS_BLOCK《
  ►#?AST_STRUCTURE_EXPECTED!【#@DECLS_BLOCK_SAMPLE●#@DECLS_BLOCK_SAMPLE】●
  ►#?AST_STRUCTURE_EXPECTED!【{ } ● NodeList2( )】●
  ►#?AST_STRUCTURE_EXPECTED!【{ int foo; #error(EXP_CLOSE_BRACE) #@BREAK ●
    NodeList2( DefinitionVariable(? DefSymbol) #@BREAK )】●
¤》
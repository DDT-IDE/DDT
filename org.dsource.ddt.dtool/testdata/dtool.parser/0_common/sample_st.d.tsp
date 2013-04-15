Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

// TODO: put statements in this block:
#@SAMPLE_BLOCK_ST《 
►#?STRUCTURE_EXPECTED!【 { Bar.foo foo = "asd"; void inner_func(int a = 42, foo ...) { } }●
BlockStatement(
  DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(String))
  DefFunction(RefPrimitive DefSymbol 
    FunctionParameter(RefPrimitive DefSymbol Integer) 
    NamelessParameter(RefIdentifier) 
    BlockStatement()
  )
)】●
¤》

// Consider merging BLK_ST and SAMPLE_BLOCK_ST
#@BLK_ST《                   ►#?STRUCTURE_EXPECTED!【 { Foo foo; } ● BlockStatement(DefVariable(RefIdentifier DefSymbol))】》
#@NO_BLOCK_ST《       ►#?STRUCTURE_EXPECTED!【 #error(EXPRULE_Block) ● BlockStatement 】》
#@BROKEN_BLOCK_ST《 ►#?STRUCTURE_EXPECTED!【 { Foo foo; #error(EXP_CLOSE_BRACE) #@BREAK● 
   BlockStatement(DefVariable(? ?)) $】》


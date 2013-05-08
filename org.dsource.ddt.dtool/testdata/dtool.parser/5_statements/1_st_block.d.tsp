▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    ;
#STRUCTURE_EXPECTED: EmptyStatement
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ST_BLOCK_TESTER《
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
 ►#?STRUCTURE_EXPECTED!【 /*No StBlock*/ ● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    #@ST_BLOCK_TESTER
#STRUCTURE_EXPECTED: #@ST_BLOCK_TESTER
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     { #error(SE_Statement) module ; }
#STRUCTURE_EXPECTED:  BlockStatement( InvalidSyntaxElement EmptyStatement )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     { #error(SE_Statement) ]      Foo foo; }
#STRUCTURE_EXPECTED:  BlockStatement( InvalidSyntaxElement DefVariable(RefIdentifier DefSymbol) )


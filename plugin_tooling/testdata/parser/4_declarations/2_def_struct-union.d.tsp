▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
struct MyStruct #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefinitionStruct(DefSymbol #@DECLS_BLOCK__SAMPLE)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@AGGR_KEYWORD《
  ►#?STRUCTURE_EXPECTED!【struct● DefinitionStruct】●
  ►#?STRUCTURE_EXPECTED!【union● DefinitionUnion】●
¤》

#@STRUCT_BODY《
  ►#?STRUCTURE_EXPECTED!【; ● DeclarationEmpty】●
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_AggregateBody) #@BREAK ● #@BREAK 】●
  ►#@DECLS_BLOCK__NO_MISSING●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  /*anon*/  #error(EXPRULE_DeclarationBlock) #@BREAK
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  anonymous struct
#PARSE(DECLARATION)   #@AGGR_KEYWORD  /*anon*/  #@DECLS_BLOCK
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@DECLS_BLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyStruct  #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@STRUCT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyStruct  #@TPL_AGGREGATE_TESTER #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@TPL_AGGREGATE_TESTER #@STRUCT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  /*anon*/  #error(EXPRULE_DeclarationBlock) #@BREAK #@TPL_AGGREGATE_TESTER #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@BREAK #@TPL_AGGREGATE_TESTER #@DECLS_BLOCK__SAMPLE)

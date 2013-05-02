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
  ►#@DECLS_BLOCK●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  #error(EXP_OPEN_BRACE)【】 #@BREAK
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  anonymous struct
#PARSE(DECLARATION)   #@AGGR_KEYWORD  #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@STRUCT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyStruct  #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@STRUCT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyStruct  #@TPL_AGGREGATE_TESTER #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@TPL_AGGREGATE_TESTER #@STRUCT_BODY)

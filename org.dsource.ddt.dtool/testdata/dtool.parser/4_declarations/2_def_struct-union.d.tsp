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
#PARSE(DECLARATION)   #@AGGR_KEYWORD  #@NO_DEFID #@BREAK
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (#@NO_DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyStruct  #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@STRUCT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@AGGR_KEYWORD  MyClass   #@TPL_AGGREGATE_TESTER #@STRUCT_BODY
#STRUCTURE_EXPECTED:  #@AGGR_KEYWORD (DefSymbol #@TPL_AGGREGATE_TESTER #@STRUCT_BODY)

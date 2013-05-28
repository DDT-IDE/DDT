▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
class MyClass : basetype #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefinitionClass(DefSymbol RefIdentifier #@DECLS_BLOCK__SAMPLE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@CLASS_KEYWORD《
  ►#?STRUCTURE_EXPECTED!【class● DefinitionClass】●
  ►#?STRUCTURE_EXPECTED!【interface● DefinitionInterface】●
¤》

#@CLASS_BODY《
  ►#?STRUCTURE_EXPECTED!【; ● DeclarationEmpty】●
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_AggregateBody) #@BREAK ● #@BREAK 】●
  ►#@DECLS_BLOCK__NO_MISSING●
¤》
#@FROM_BASE_CLASS《
  ►#@CLASS_BODY●
  ►#?STRUCTURE_EXPECTED!【: #@TYPEREF_NB__LITE  #@CLASS_BODY● #@TYPEREF_NB__LITE #@CLASS_BODY】●
  ►#?STRUCTURE_EXPECTED!【: #@TYPEREF_NB__LITE, #@TYPEREF_NB #@DECLS_BLOCK__SAMPLE● #@TYPEREF_NB__LITE  #@TYPEREF_NB #@DECLS_BLOCK__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【: int[foo #@NO_CloseBRACKET #@DECLS_BLOCK__SAMPLE● RefIndexing(RefPrimitive RefIdentifier) #@DECLS_BLOCK__SAMPLE】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@CLASS_KEYWORD  #@NO_DEFID #@BREAK
#STRUCTURE_EXPECTED:  #@CLASS_KEYWORD (#@NO_DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@CLASS_KEYWORD  MyClass #@FROM_BASE_CLASS
#STRUCTURE_EXPECTED:  #@CLASS_KEYWORD (DefSymbol #@FROM_BASE_CLASS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@CLASS_KEYWORD  MyClass #@TPL_AGGREGATE_TESTER #@FROM_BASE_CLASS
#STRUCTURE_EXPECTED:  #@CLASS_KEYWORD (DefSymbol #@TPL_AGGREGATE_TESTER #@FROM_BASE_CLASS)

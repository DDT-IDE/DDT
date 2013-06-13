Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TRAITS_VALID_ID《
►isAbstractClass●
►isArithmetic●
►isAssociativeArray●
►isFinalClass●
►isPOD●
►isNested●
►isFloating●
►isIntegral●
►isScalar●
►isStaticArray●
►isUnsigned●
►isVirtualFunction●
►isVirtualMethod●
►isAbstractFunction●
►isFinalFunction●
►isStaticFunction●
►isRef●
►isOut●
►isLazy●
►hasMember●
►identifier●
►getAttributes●
►getMember●
►getOverloads●
►getProtection●
►getVirtualFunctions●
►getVirtualMethods●
►parent●
►classInstanceSize●
►allMembers●
►derivedMembers●
►isSame●
►compiles●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   __traits #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpTraits()
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TRAITS_ID《
 ►#?STRUCTURE_EXPECTED!【#@NO_ID ● Symbol 】●
 ►#?STRUCTURE_EXPECTED!【#@TRAITS_VALID_ID ● Symbol 】●
 ►#?STRUCTURE_EXPECTED!【 xxxx #error(INVALID_TRAITS_ID) ● Symbol】●
¤》
#@TRAITS_ID__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【hasMember ● Symbol】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   __traits( #@TRAITS_ID #error(EXP_COMMA)【】 #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpTraits(#@TRAITS_ID)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG_NBNE《#@EXP_ASSIGN__NO_POSSIBLE_REFS●#@TYPEREF_NBNE》
  #@ARG_NB《#@EXP_ASSIGN__NO_POSSIBLE_REFS●#@TYPEREF_NBNE●#@NULL_EXP 》
  #@ARG_NB__LITE《#@EXP_ASSIGN__LITE●#@TYPEREF_NBNE__LITE●#@NO_TOE》
#@ARG_NBNE__LITE《#@EXP_ASSIGN__LITE●#@TYPEREF_NBNE__LITE》

#@TRAITS_ARGS《
 ►#?STRUCTURE_EXPECTED!【 ●  】●
 ►#?STRUCTURE_EXPECTED!【#@ARG_NBNE #@《 ●,》 ● #@ARG_NBNE 】●
 ►#?STRUCTURE_EXPECTED!【#@NO_TOE       , #@ARG_NBNE__LITE #@《 ●,》● #@NO_TOE       #@ARG_NBNE__LITE】●
 ►#?STRUCTURE_EXPECTED!【#@ARG_NB__LITE , #@ARG_NBNE       #@《 ●,》● #@ARG_NB__LITE #@ARG_NBNE】●
 ►#?STRUCTURE_EXPECTED!【#@ARG_NB__LITE , #@NO_TOE  , #@ARG_NBNE__LITE● #@ARG_NB__LITE #@NO_TOE #@ARG_NBNE__LITE 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test expression list
#PARSE(EXPRESSION)    __traits(#@TRAITS_ID__SAMPLE, #@TRAITS_ARGS #@NOBROKEN__CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpTraits(#@TRAITS_ID__SAMPLE  #@TRAITS_ARGS)



Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TRAITS_VALID_ID《
►isAbstractClass●
►isArithmetic●
►isAssociativeArray●
►isFinalClass●
►isPOD●
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

#@ARG_ANY《#@EXP_ASSIGN__NO_POSSIBLE_REFS●#@TYPEREF_NE  ●#@NO_ROE》
 #@ARG_NB《#@EXP_ASSIGN__NO_POSSIBLE_REFS●#@TYPEREF_NBNE●#@NO_ROE》
       #@ARG_NB__LITE《#@EXP_ASSIGN__LITE●#@TYPEREF_NBNE__LITE●#@NO_ROE》

#@TRAITS_ARGS《
 ►#?STRUCTURE_EXPECTED!【#@ARG_ANY ● #@ARG_ANY 】●
 ►#?STRUCTURE_EXPECTED!【#@ARG_NB__LITE , #@ARG_ANY● #@ARG_NB__LITE #@ARG_ANY】●
 ►#?STRUCTURE_EXPECTED!【#@ARG_NB__LITE, #@ARG_NB__LITE, #@ARG_NB__LITE● #@ARG_NB__LITE #@ARG_NB__LITE #@ARG_NB__LITE 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test expression list
#PARSE(EXPRESSION)    __traits(#@TRAITS_ID__SAMPLE, #@TRAITS_ARGS #@NOBROKEN__CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpTraits(#@TRAITS_ID__SAMPLE  #@TRAITS_ARGS)



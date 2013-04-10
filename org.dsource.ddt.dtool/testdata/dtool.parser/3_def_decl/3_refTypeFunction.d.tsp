▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(REFERENCE)     foo function(#@FN_PARAMS_SAMPLE) 
#STRUCTURE_EXPECTED:  RefTypeFunction(RefIdentifier #@FN_PARAMS_SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(REFERENCE)     foo delegate(#@FN_PARAMS_SAMPLE) const
#STRUCTURE_EXPECTED:  RefTypeFunction(RefIdentifier #@FN_PARAMS_SAMPLE)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FN_KEYWORD《function●delegate》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  ret type
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN function(#@FN_PARAMS_SAMPLE)
#STRUCTURE_EXPECTED: 
RefTypeFunction(#@TYPE_REFS__NO_BROKEN #@FN_PARAMS_SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)     bar[] function #ERROR(EXP_OPEN_PARENS)【】 #@BREAK 
#STRUCTURE_EXPECTED: 
RefTypeFunction(RefTypeDynArray(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  0 param
#PARSE(REFERENCE)     const(foo) #@FN_KEYWORD ( #@PaCLOSE_OR_BREAK 
#STRUCTURE_EXPECTED:
RefTypeFunction(RefTypeModifier(RefIdentifier) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  1 param
#PARSE(REFERENCE)     bar[] function ( #FN_PARAMETER_TEST(test) #@FN_PARAM_SINGLE ) 
#STRUCTURE_EXPECTED:
RefTypeFunction(RefTypeDynArray(?) #@FN_PARAM_SINGLE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 2+ params
#PARSE(REFERENCE)     bar[] #@FN_KEYWORD ( #@FN_PARAMS ) 
#STRUCTURE_EXPECTED:
RefTypeFunction( RefTypeDynArray(?) #@FN_PARAMS )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ fn attributes
#PARSE(REFERENCE)     bar[] #@FN_KEYWORD ( #@FN_PARAMS ) #@FN_ATTRIB
#STRUCTURE_EXPECTED:
RefTypeFunction( RefTypeDynArray(?) #@FN_PARAMS )
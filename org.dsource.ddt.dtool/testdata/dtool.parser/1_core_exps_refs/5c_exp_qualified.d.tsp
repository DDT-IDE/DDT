▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PREFIX!《
  ►#?STRUCTURE_EXPECTED!【Foo●RefIdentifier】●
  ►#?STRUCTURE_EXPECTED!【"asdf"●ExpLiteralString】●
  ►#?STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?STRUCTURE_EXPECTED!【(int)●ExpParentheses(RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【(Foo[int])●ExpParentheses(RefIndexing(RefIdentifier RefPrimitive))】●
  ►#?STRUCTURE_EXPECTED!【(Foo[foo*])●ExpParentheses(RefIndexing(RefIdentifier RefTypePointer(RefIdentifier)))】●
¤》
#PARSE(EXPRESSION)        #@PREFIX .init
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(#@PREFIX RefIdentifier))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@QUALIFIERS《
  ►#?STRUCTURE_EXPECTED!【1●Integer】●
  ►#?STRUCTURE_EXPECTED!【1.0●ExpLiteralFloat】●
  ►#?STRUCTURE_EXPECTED!【foo[] ● ExpSlice(#@ExpIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_POSTFIX__NO_POSSIBLE_REFS●#@EXP_POSTFIX__NO_POSSIBLE_REFS】●
  ►#?STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG__INVALID_QUALIFIER●*】●
  
  ►#?STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【foo[foo*]】 ● ExpReference(RefIndexing(RefIdentifier RefTypePointer(?)))】●
  ►#?STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【int[]】 ● ExpReference(RefTypeDynArray(RefPrimitive))】●
  ►#?STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER】●ExpReference(#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER)】●

  
  ►#?STRUCTURE_EXPECTED!【( #@EXPS__NO_POSSIBLE_REFS )●ExpParentheses( #@EXPS__NO_POSSIBLE_REFS )】●
  ►#?STRUCTURE_EXPECTED!【( #@TYPEREFS_UNAMBIG )●ExpParentheses( #@TYPEREFS_UNAMBIG )】●
  ►#?STRUCTURE_EXPECTED!【( #@TYPEREFS_AMBIG )●ExpParentheses( #@TYPEREFS_AMBIG )】●
¤》
// We also test ExpQualified and ExpParantheses interaction 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_QUALIFIED《
  ►#?STRUCTURE_EXPECTED!【#@QUALIFIERS .init● RefQualified(#@QUALIFIERS RefIdentifier )】●
  ►#?STRUCTURE_EXPECTED!【#@QUALIFIERS . init .blah● RefQualified(ExpReference(RefQualified(#@QUALIFIERS ?))  RefIdentifier)】●
¤》
#STRUCTURE_EXPECTED: ExpReference( #@EXP_QUALIFIED )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_QUALIFIED《
  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__QUALIFIER .init● RefQualified(#@TYPE_REFS__QUALIFIER RefIdentifier )】●
  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__QUALIFIER . init .blah● RefQualified(RefQualified(#@TYPE_REFS__QUALIFIER ?)  RefIdentifier)】●
¤》
#STRUCTURE_EXPECTED: ExpReference( #@EXP_QUALIFIED )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE) #@QUALIFIERS . #@NO_ID /* error doesnt break parsing */ + 23
#STRUCTURE_EXPECTED:   ExpInfix(  ExpReference(RefQualified(#@QUALIFIERS #@NO_ID)) Integer  )


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PREFIX!《
  ►#?AST_STRUCTURE_EXPECTED!【Foo●RefIdentifier】●
  ►#?AST_STRUCTURE_EXPECTED!【"asdf"●ExpLiteralString】●
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【(int)●ExpParentheses(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【(Foo[int])●ExpParentheses(RefIndexing(RefIdentifier RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【(Foo[foo*])●ExpParentheses(RefIndexing(RefIdentifier RefTypePointer(RefIdentifier)))】●
¤》
#PARSE(EXPRESSION)        #@PREFIX .init
#AST_STRUCTURE_EXPECTED:  ExpReference(RefQualified(#@PREFIX RefIdentifier))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@QUALIFIERS《
  ►#?AST_STRUCTURE_EXPECTED!【1●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【1.0●ExpLiteralFloat】●
  ►#?AST_STRUCTURE_EXPECTED!【foo[] ● ExpSlice(#@ExpIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_POSTFIX__NO_POSSIBLE_REFS●#@EXP_POSTFIX__NO_POSSIBLE_REFS】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG__INVALID_QUALIFIER●*】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【foo[foo*]】 ● ExpReference(RefIndexing(RefIdentifier RefTypePointer(?)))】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【int[]】 ● ExpReference(RefTypeDynArray(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(INV_QUALIFIER)【#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER】●ExpReference(#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER)】●

  
  ►#?AST_STRUCTURE_EXPECTED!【( #@EXPS__NO_POSSIBLE_REFS )●ExpParentheses( #@EXPS__NO_POSSIBLE_REFS )】●
  ►#?AST_STRUCTURE_EXPECTED!【( #@TYPEREFS_UNAMBIG )●ExpParentheses( #@TYPEREFS_UNAMBIG )】●
  ►#?AST_STRUCTURE_EXPECTED!【( #@TYPEREFS_AMBIG )●ExpParentheses( #@TYPEREFS_AMBIG )】●
¤》
// We also test ExpQualified and ExpParantheses interaction 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #@EXP_QUALIFIED《
  ►#?AST_STRUCTURE_EXPECTED!【#@QUALIFIERS .init● RefQualified(#@QUALIFIERS RefIdentifier )】●
  ►#?AST_STRUCTURE_EXPECTED!【#@QUALIFIERS . init .blah● RefQualified(ExpReference(RefQualified(#@QUALIFIERS ?))  RefIdentifier)】●
¤》
#AST_STRUCTURE_EXPECTED: ExpReference( #@EXP_QUALIFIED )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #@EXP_QUALIFIED《
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS__QUALIFIER .init● RefQualified(#@TYPE_REFS__QUALIFIER RefIdentifier )】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS__QUALIFIER . init .blah● RefQualified(RefQualified(#@TYPE_REFS__QUALIFIER ?)  RefIdentifier)】●
¤》
#AST_STRUCTURE_EXPECTED: ExpReference( #@EXP_QUALIFIED )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE)    #@QUALIFIERS . #@NO_ID /* error doesnt break parsing */ + 23
#AST_STRUCTURE_EXPECTED:  ExpInfix(  ExpReference(RefQualified(#@QUALIFIERS #@NO_ID)) Integer  )


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       Foo.init
#AST_STRUCTURE_EXPECTED: ExpReference( RefQualified(RefIdentifier RefIdentifier) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       "asdf".init
#AST_STRUCTURE_EXPECTED: ExpReference( RefQualified(ExpLiteralString RefIdentifier) )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@QUALIFIERS《
  ►#@EXP_PRIMARY_APPENDABLE●
  ►#?AST_STRUCTURE_EXPECTED!【1●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【1.0●ExpLiteralFloat】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS_QUALIFIER● #@TYPE_REFS_QUALIFIER 】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_AS_EXP[]● ExpSlice(ExpReference(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS_INVALID_QUALIFIER #parser(AllowAnyErrors)● *】●
  
  ►#?AST_STRUCTURE_EXPECTED!【( #@EXPS__NO_REFS )●ExpParentheses( #@EXPS__NO_REFS )】●
  ►#?AST_STRUCTURE_EXPECTED!【(foo[])●ExpParentheses(ExpSlice(#@ExpIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【(foo*[])●ExpParentheses(ExpInfix(#@ExpIdentifier ExpLiteralArray))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【( #@REFS_UNAMBIG )●ExpParentheses( #@REFS_UNAMBIG )】●
¤》


// TODO template instances

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #@EXP_QUALIFIED《
  ►#?AST_STRUCTURE_EXPECTED!【#@QUALIFIERS .init● RefQualified(#@QUALIFIERS RefIdentifier )】●
  ►#?AST_STRUCTURE_EXPECTED!【#@QUALIFIERS .init.blah● RefQualified(RefQualified(#@QUALIFIERS ?) RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@QUALIFIERS . init.blah● RefQualified(RefQualified(#@QUALIFIERS ?)  RefIdentifier)】●
¤》
#AST_STRUCTURE_EXPECTED: ExpReference( #@EXP_QUALIFIED )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #@QUALIFIERS . #error(EXP_ID) /* error doesnt break parsing */ + 23
#AST_STRUCTURE_EXPECTED: ExpInfix(  ExpReference(RefQualified(#@QUALIFIERS RefIdentifier)) Integer  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test wich qualifier applies to RefQualified in the case of a missing parentheses
#PARSE(EXPRESSION) (#error(INV_QUALIFIER)【int[]】 #error(EXP_CLOSE_PARENS) .init 
#AST_STRUCTURE_EXPECTED:
ExpReference(RefQualified( ExpParentheses(RefTypeDynArray(RefPrimitive))   RefIdentifier))
Ⓗ▂▂// alternate behavior for previous case 
#PARSE(EXPRESSION) (#error(INV_QUALIFIER)【int[]】 .init #error(EXP_CLOSE_PARENS)
#AST_STRUCTURE_EXPECTED:
ExpParentheses(  RefQualified(RefTypeDynArray(RefPrimitive) RefIdentifier)  ) 
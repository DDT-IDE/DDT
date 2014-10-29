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

#@EXP_QUALIFIER_BASE《
  ►#?STRUCTURE_EXPECTED!【123●Integer】●
  ►#?STRUCTURE_EXPECTED!【3.14●ExpLiteralFloat】●
  ►#?STRUCTURE_EXPECTED!【foo[] ● ExpIndex(#@ExpIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_POSTFIX__NO_POSSIBLE_REFS ●#@EXP_POSTFIX__NO_POSSIBLE_REFS】●
¤》
#@REF_QUALIFIER_BASE《
  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__QUALIFIER_ForDot●#@TYPE_REFS__QUALIFIER_ForDot】●
¤》

#@QUALIFIER《
  ►#?STRUCTURE_EXPECTED!【#@EXP_QUALIFIER_BASE●#@EXP_QUALIFIER_BASE】●
  ►#?STRUCTURE_EXPECTED!【#@REF_QUALIFIER_BASE●#@REF_QUALIFIER_BASE】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_QUALIFIER_BASE . init●ExpReference(RefQualified(#@EXP_QUALIFIER_BASE RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【#@REF_QUALIFIER_BASE . init●             RefQualified(#@REF_QUALIFIER_BASE RefIdentifier)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)     #@QUALIFIER .qualified
#STRUCTURE_EXPECTED:   ExpReference( RefQualified(#@QUALIFIER RefIdentifier) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)     #PARSING_AS_EXP(flag) #@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER .init #parser(AllowAnyErrors)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE) #@QUALIFIER . #@NO_ID /* error doesnt break parsing */ () + 23
#STRUCTURE_EXPECTED:   ExpInfix( ExpCall( ExpReference(RefQualified(#@QUALIFIER #@NO_ID)) )  Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE) . #@NO_ID /* error doesnt break parsing */ + 23
#STRUCTURE_EXPECTED:   ExpInfix(  ExpReference(RefModuleQualified(#@NO_ID)) Integer  )


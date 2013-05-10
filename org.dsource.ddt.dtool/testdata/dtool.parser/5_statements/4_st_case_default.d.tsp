Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@CASE_BODY__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【 #@ST__SAMPLE● ScopedStatementList( #@ST__SAMPLE )】●
¤》
#@CASE_BODY《
  ►#?STRUCTURE_EXPECTED!【 ● ScopedStatementList( )】●
  ►#?STRUCTURE_EXPECTED!【 #@ST__NB_NE● ScopedStatementList( #@ST__NB_NE )】●
  ►#?STRUCTURE_EXPECTED!【 { #@ST__SAMPLE #error(EXP_CLOSE_BRACE)  ● ScopedStatementList(BlockStatement(#@ST__SAMPLE $))】●
  ►#?STRUCTURE_EXPECTED!【 #@ST__SAMPLE #@ST__SAMPLE #@ST__NB_NE● ScopedStatementList( #@ST__SAMPLE #@ST__SAMPLE #@ST__NB_NE )】●
  ►#?STRUCTURE_EXPECTED!【 #@ST__SAMPLE #@IGNORE_REST case 123: ● ScopedStatementList( #@ST__SAMPLE )】●
  ►#?STRUCTURE_EXPECTED!【 #@ST__SAMPLE #@IGNORE_REST default:  ● ScopedStatementList( #@ST__SAMPLE )】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@EXP_ARGS  : #@CASE_BODY__SAMPLE
#STRUCTURE_EXPECTED: StatementCase( #@EXP_ARGS  #@CASE_BODY__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@EXP_ARGS__SAMPLE  #@COLON_OR_BREAK  #@CASE_BODY__SAMPLE
#STRUCTURE_EXPECTED: StatementCase( #@ASSIGNEXP__LITE #@COLON_OR_BREAK #@CASE_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@EXP_ARGS__SAMPLE : #@CASE_BODY
#STRUCTURE_EXPECTED: StatementCase( #@EXP_ARGS__SAMPLE  #@CASE_BODY )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@RANGE_LAST_EXP《
 ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_OrNO ● #@ASSIGNEXP_OrNO 】●
 ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_NBOrNO__LITE #error(EXP_COLON) #@BREAK , #@ASSIGNEXP_OrNO__LITE● #@ASSIGNEXP_NBOrNO__LITE #@BREAK】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@ASSIGNEXP__LITE, #@ASSIGNEXP_OrNO__LITE : #error(SE_Statement) .. #@IGNORE_REST case 123
#STRUCTURE_EXPECTED: StatementCase( #@ASSIGNEXP__LITE #@ASSIGNEXP_OrNO__LITE ScopedStatementList(InvalidSyntaxElement))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@ASSIGNEXP_OrNO__LITE  :  ..  #error(EXP_KW_CASE)【】 #@BREAK
#STRUCTURE_EXPECTED: StatementCaseRange( #@ASSIGNEXP_OrNO__LITE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@ASSIGNEXP_OrNO__LITE  :  ..  case #@RANGE_LAST_EXP : #@CASE_BODY__SAMPLE
#STRUCTURE_EXPECTED: StatementCaseRange( #@ASSIGNEXP_OrNO__LITE #@RANGE_LAST_EXP  #@CASE_BODY__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@ASSIGNEXP_OrNO__LITE  :  ..  case #@ASSIGNEXP__LITE  #@COLON_OR_BREAK  #@CASE_BODY__SAMPLE
#STRUCTURE_EXPECTED: StatementCaseRange( #@ASSIGNEXP_OrNO__LITE #@ASSIGNEXP__LITE #@COLON_OR_BREAK #@CASE_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    case #@ASSIGNEXP_OrNO__LITE  :  ..  case #@ASSIGNEXP__LITE : #@CASE_BODY
#STRUCTURE_EXPECTED: StatementCaseRange( #@ASSIGNEXP_OrNO__LITE #@ASSIGNEXP__LITE  #@CASE_BODY )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    default #@COLON_OR_BREAK  #@CASE_BODY__SAMPLE
#STRUCTURE_EXPECTED: StatementDefault(#@COLON_OR_BREAK #@CASE_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    default : #@CASE_BODY
#STRUCTURE_EXPECTED: StatementDefault( #@CASE_BODY )
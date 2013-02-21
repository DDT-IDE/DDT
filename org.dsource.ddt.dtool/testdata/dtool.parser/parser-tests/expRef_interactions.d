▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid( 7 * #error(TYPE_AS_EXP_VALUE)《int》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(Integer ExpReference(RefPrimitive))  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#@TYPE_EXP [foo]
#AST_STRUCTURE_EXPECTED:
ExpIndex(ExpReference(RefPrimitive) ExpReference(RefIdentifier))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
// various ref_or_exp prefixes , there are lots of corner cases to test

// Simple prefix:
#@PREFIX_S《
  ►#?AST_STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【dai* ● RefTypePointer(RefIdentifier)】 ●
  ►#?AST_STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
¤》
 
// These prefixes are ambiguous as to whether to parse as ref or exp
#@AMBIG_PREFIX《
  ►#?AST_STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【dai* #INFIX(flag)● RefTypePointer(RefIdentifier)】 ●
  ►#?AST_STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[7 /*EXP*/]● RefIndexing(#@PREFIX_S Integer) 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/]● RefIndexing(#@PREFIX_S RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[]/*AMBIG*/]● RefIndexing(#@PREFIX_S 
    RefTypeDynArray(RefTypePointer(RefIdentifier))) 】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpIndex(* Integer Integer))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* ?(Integer Integer)))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[2 : #@NO_EXP]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* *))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[bar]/*AMBIG*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》

// These prefixes make the rule parse as ref
#@REF_PREFIX《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*/*REF*/]● RefIndexing(#@PREFIX_S RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[int/*REF*/]● RefIndexing(#@PREFIX_S RefPrimitive)】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[int]/*REF*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》

// These suffix parts make the rule parse as exp
#@EXP_SUFFIX《
  ►#?AST_STRUCTURE_EXPECTED!【*foo●ExpInfix(* #?INFIX【ExpPrefix(*)●#@ExpIdentifier】)】●
  ►#?AST_STRUCTURE_EXPECTED!【*#@TYPE_EXP●ExpInfix(* #?INFIX【ExpPrefix(*)●ExpReference(RefPrimitive)】)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【[foo , 2] * []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【[#@NO_EXP ,2] * #@NULL_EXP●ExpInfix(* #@NULL_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【*[foo: #@NO_EXP] * [foo]●ExpInfix(* ExpLiteralArray(#@ExpIdentifier))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#?INFIX【[7/*INFIX FIX*/]●】  (1, 2)* []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【#?INFIX【[7/*INFIX FIX*/]●】  [foo .. 2] * []●ExpInfix(* ExpLiteralArray)】●
  
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to reference, decided at various points during parsing
#PARSE(EXPRESSION)
typeid(#@AMBIG_OR_REF《#@AMBIG_PREFIX●#@REF_PREFIX》   #@REF_END《 ● *● * * ● [foo*] ●[]》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId( 
  #@《 ●RefTypePointer(●RefTypePointer(RefTypePointer(●RefIndexing(●RefTypeDynArray(》(REF_END) 
  #@AMBIG_OR_REF 
  #@《●)●))●RefTypePointer(RefIdentifier) )●)》(REF_END)
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to reference, with exp extra
#PARSE(EXPRESSION)
typeid(#@REF_PREFIX #@REST《 ● * #parser(IgnoreRest) #error(EXP_CLOSE_PARENS) 42 》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId( 
  #@《 ●RefTypePointer( 》(REST) #@REF_PREFIX #@《 ●) 》(REST)
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to expression, decided in middle or end
#PARSE(EXPRESSION)
typeid(#@AMBIG_PREFIX #@EXP_SUFFIX )
#AST_STRUCTURE_EXPECTED:
ExpTypeId( #@EXP_SUFFIX )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test op category mismatch
#PARSE(EXPRESSION)
typeid(foo / * #error(EXPRULE_exp) )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(ExpReference(?) ExpPrefix() )  )


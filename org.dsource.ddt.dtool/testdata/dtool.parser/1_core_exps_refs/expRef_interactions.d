▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid( 7 * #error(TYPE_AS_EXP_VALUE)《int》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(Integer ExpReference(RefPrimitive))  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#@TYPE_AS_EXP [foo]
#AST_STRUCTURE_EXPECTED:
ExpIndex(ExpReference(RefPrimitive) ExpReference(RefIdentifier))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to reference, decided at various points during parsing
#PARSE(EXPRESSION)        typeid(#@AMBIG_OR_REF《#@AMBIG_AS_REF●#@REFS_UNAMBIG》   #@REF_END《 ● *● * * ● [foo*] ●[]》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(
  #@《 ●RefTypePointer(●RefTypePointer(RefTypePointer(●RefIndexing(●RefTypeDynArray(》(REF_END) 
  #@AMBIG_OR_REF 
  #@《●)●))●RefTypePointer(RefIdentifier) )●)》(REF_END)
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to reference, with exp extra
#PARSE(EXPRESSION)        typeid(#@REFS_UNAMBIG #@REST《 ● * #error(EXP_CLOSE_PARENS) #parser(IgnoreRest) 42 》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId( 
  #@《 ●RefTypePointer( 》(REST) #@REFS_UNAMBIG #@《 ●) 》(REST)
)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
 // These suffix parts make the rule parse as exp
#@EXP_SUFFIX《
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF *foo●ExpInfix(* #?INFIX【ExpPrefix(*)●#@ExpIdentifier】)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF*#@TYPE_AS_EXP●ExpInfix(* #?INFIX【ExpPrefix(*)●ExpReference(RefPrimitive)】)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF [foo , 2] * []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF [#@NO_EXP ,2] * #@NULL_EXP●ExpInfix(* #@NULL_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF *[foo: #@NO_EXP] * [foo]●ExpInfix(* ExpLiteralArray(#@ExpIdentifier))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF #?INFIX【[7/*INFIX FIX*/]●】  (1, 2)* []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF #?INFIX【[7/*INFIX FIX*/]●】  [foo .. 2] * []●ExpInfix(* ExpLiteralArray)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF [] ++ ●*】●
  ►#?AST_STRUCTURE_EXPECTED!【#@AMBIG_AS_REF [] ^^ 123 ●*】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to expression, decided in middle or end
#PARSE(EXPRESSION)        typeid( #@EXP_SUFFIX )
#AST_STRUCTURE_EXPECTED:  ExpTypeId( #@EXP_SUFFIX )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test op category mismatch
#PARSE(EXPRESSION)        typeid(foo / * #error(EXPRULE_exp) )
#AST_STRUCTURE_EXPECTED:  ExpTypeId(  ExpInfix(ExpReference(?) ExpPrefix() )  )

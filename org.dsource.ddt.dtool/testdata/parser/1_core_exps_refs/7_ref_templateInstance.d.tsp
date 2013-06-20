▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(bar, 123) dummy;
foo!() dummy;
foo!xxx dummy;
#STRUCTURE_EXPECTED:
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier Integer) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG《 #@TYPEREF_NBNE ● #@EXP_ASSIGN__NO_POSSIBLE_REFS 》

#@ARG_COMMA《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE ● #@EXP_ASSIGN__LITE】● 
  ►#?STRUCTURE_EXPECTED!【bar[foo*] ● RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】● 
  ►#?STRUCTURE_EXPECTED!【 #@NO_TOE ● #@NO_TOE 】● 
¤》

#@TPL_ARGLIST《
  ►#?STRUCTURE_EXPECTED!【!( #@CLOSEPARENS_OR_BREAK ● 】●
  ►#?STRUCTURE_EXPECTED!【!( #@NO_TOE , #@CLOSEPARENS_OR_BREAK ● #@NO_TOE】●
  ►#?STRUCTURE_EXPECTED!【!( #@ARG #@《 ●,》 #@CLOSEPARENS_OR_BREAK ● #@ARG】●
  
  ►#?STRUCTURE_EXPECTED!【!(#@ARG_COMMA , #@ARG #@《 ●,》 #@CLOSEPARENS_OR_BREAK● #@ARG_COMMA #@ARG】●
  ►#?STRUCTURE_EXPECTED!【!(#@ARG_COMMA , #@ARG_COMMA , #@ARG #@CLOSEPARENS_OR_BREAK● #@ARG_COMMA #@ARG_COMMA #@ARG】●

  ►#?STRUCTURE_EXPECTED!【! #@SINGLE_ARG ● #@SINGLE_ARG 】●
¤》
#@SINGLE_ARG《 
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_TplSingleArg)● #@NO_EXP 】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_PRIMARY_SIMPLE  ● #@EXP_PRIMARY_SIMPLE】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)                        #@TYPE_REFS__QUALIFIER_ForTplOrDot #@TPL_ARGLIST
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__QUALIFIER_ForTplOrDot #@TPL_ARGLIST)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ indexing cannot be template start
#PARSE(REFERENCE)    #@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER #@IGNORE_REST !this
#STRUCTURE_EXPECTED: #@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_REFS__QUALIFIER_ForDotOnly #@IGNORE_REST !this #?TYPE_REF_TPL_SIMPLE_ARG{#:DISCARD_CASE}
#STRUCTURE_EXPECTED: #@TYPE_REFS__QUALIFIER_ForDotOnly

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ SPECIAL case: in expression context we allow any kinf of qualified exp
#PARSE(Expression)   #@EXP_POSTFIX . foo ! this 
#STRUCTURE_EXPECTED: ExpReference( RefTemplateInstance( RefQualified(#@EXP_POSTFIX RefIdentifier) ExpThis) )


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ tpl single arg
#@TYPE_REFS__TPL_SINGLE_ARG《
  ►#?STRUCTURE_EXPECTED!【bar. foo!this●RefTemplateInstance(RefQualified(RefIdentifier ?) ExpThis)】●
  ►#?STRUCTURE_EXPECTED!【.bar.foo! arg●RefTemplateInstance(RefQualified(RefModuleQualified(?) ?) RefIdentifier)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #error(NO_TPL_SINGLE_ARG)【#@TYPE_REFS__TPL_SINGLE_ARG】 ! #@SINGLE_ARG
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG #@SINGLE_ARG)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    foo! #error(EXPRULE_TplSingleArg) .bar
#STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier #@NO_TOE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    foo!this . xpto
#STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier ExpThis) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ !in !is interaction
#PARSE(REFERENCE)    foo #@IGNORE_REST ! #@《in●is》
#STRUCTURE_EXPECTED: RefIdentifier
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ tpl single arg with normal arg
#PARSE(REFERENCE)    #@TYPE_REFS__TPL_SINGLE_ARG !(foo)
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break
#PARSE(REFERENCE)    foo!(var[] #error(EXP_CLOSE_PARENS) #@BREAK !(blah)
#STRUCTURE_EXPECTED: RefTemplateInstance(RefIdentifier RefTypeDynArray(*))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break 
// Single arg missing doesn't cause rule to break
#PARSE(REFERENCE)    foo! #error(EXPRULE_TplSingleArg) !(blah)
#STRUCTURE_EXPECTED: RefTemplateInstance(RefTemplateInstance(RefIdentifier #@NO_TOE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     foo!(var[] #@PARENS_BREAK
#STRUCTURE_EXPECTED:  RefTemplateInstance(RefIdentifier RefTypeDynArray(*))


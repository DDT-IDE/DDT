▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(bar, 123) dummy;
foo!() dummy;
foo!xxx dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier Integer) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG《 #NO_BROKEN_PARENS(flag) #@TYPE_REFS ● #@EXP_ASSIGN__NO_POSSIBLE_REFS ● 》
#@ARG_OR_MISS《 #NO_BROKEN_PARENS(flag) #@TYPE_REFS ● #@EXP_ASSIGN__NO_POSSIBLE_REFS ● #@NO_ROE》

#@ARG_COMMA《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?STRUCTURE_EXPECTED!【bar[foo*] , ● RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】● 
  ►#?STRUCTURE_EXPECTED!【 #@NO_ROE, ● #@NO_ROE 】● 
¤》

#@TPL_ARGLIST《
  ►#?STRUCTURE_EXPECTED!【!( #@ARG #@CLOSEPARENS_OR_BREAK ● #@ARG】●
  
  ►#?STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_OR_MISS #@CLOSEPARENS_OR_BREAK● #@ARG_COMMA #@ARG_OR_MISS】●
  ►#?STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_COMMA #@ARG_OR_MISS #@CLOSEPARENS_OR_BREAK● * * #@ARG_OR_MISS】●

  ►#?STRUCTURE_EXPECTED!【! #@SINGLE_ARG ● #@SINGLE_ARG 】●
¤》
#@SINGLE_ARG《 
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_TplArg)● #@NO_EXP 】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_PRIMARY_SIMPLE  ● #@EXP_PRIMARY_SIMPLE】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)                        #@TYPE_REFS__QUALIFIER_ANY #@TPL_ARGLIST
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__QUALIFIER_ANY #@TPL_ARGLIST)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ indexing cannot be template start
#PARSE(REFERENCE)    #@TYPE_REFS__INVALID_QUALIFIER #@IGNORE_REST !this
#STRUCTURE_EXPECTED: #@TYPE_REFS__INVALID_QUALIFIER
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)    #@TYPE_REFS__QUALIFIER_DOT_ONLY #@IGNORE_REST !this
#STRUCTURE_EXPECTED: #@TYPE_REFS__QUALIFIER_DOT_ONLY

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ tpl single arg
#PARSE(REFERENCE)    #error(NO_TPL_SINGLE_ARG)【#@TYPE_REFS__TPL_SINGLE_ARG】 ! #@SINGLE_ARG
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG #@SINGLE_ARG)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_REFS__TPL_SINGLE_ARG !(foo)
#STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    foo! #error(EXPRULE_TemplateSingleArgument) .bar
#STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier #@NO_ROE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    foo!this . xpto
#STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier ExpThis) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break
#PARSE(REFERENCE)    foo!(var[] #error(EXP_CLOSE_PARENS) #@BREAK !(blah)
#STRUCTURE_EXPECTED: RefTemplateInstance(RefIdentifier RefTypeDynArray(*))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break 
// Single arg missing doesn't cause rule to break
#PARSE(REFERENCE)    foo! #error(EXPRULE_TemplateSingleArgument) !(blah)
#STRUCTURE_EXPECTED: RefTemplateInstance(RefTemplateInstance(RefIdentifier #@NO_ROE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(var[] #error(EXP_CLOSE_PARENS) dummy #error(EXP_ID);
#STRUCTURE_EXPECTED: 
InvalidDeclaration(RefTemplateInstance(RefIdentifier RefTypeDynArray(*)))
InvalidDeclaration(RefIdentifier)
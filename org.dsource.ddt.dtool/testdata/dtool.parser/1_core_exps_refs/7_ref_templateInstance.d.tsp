▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(bar, 123) dummy;
foo!() dummy;
foo!xxx dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier Integer) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG《 #@TYPE_REFS ● #@EXP_ASSIGN__NO_POSSIBLE_REFS ● 》
#@ARG_OR_MISS《 #@TYPE_REFS ● #@EXP_ASSIGN__NO_POSSIBLE_REFS ● #@NO_ROE》

#@ARG_COMMA《
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?AST_STRUCTURE_EXPECTED!【bar[foo*] , ● RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】● 
  ►#?AST_STRUCTURE_EXPECTED!【 #@NO_ROE, ● #@NO_ROE 】● 
¤》

#@TPL_ARGLIST《
  ►#?AST_STRUCTURE_EXPECTED!【!( #@ARG #@PaCLOSE_OR_BREAK ● #@ARG】●
  
  ►#?AST_STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_OR_MISS #@PaCLOSE_OR_BREAK● #@ARG_COMMA #@ARG_OR_MISS】●
  ►#?AST_STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_OR_MISS, #@ARG_OR_MISS #@PaCLOSE_OR_BREAK● * * #@ARG_OR_MISS】●

  ►#?AST_STRUCTURE_EXPECTED!【! #@SINGLE_ARG ● #@SINGLE_ARG 】●
¤》
#@SINGLE_ARG《 
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXPRULE_TplArg)● #@NO_EXP 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_PRIMARY_SIMPLE  ● #@EXP_PRIMARY_SIMPLE】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)                            #@TYPE_REFS__QUALIFIER_ANY #@TPL_ARGLIST
#AST_STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__QUALIFIER_ANY #@TPL_ARGLIST)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ indexing cannot be template start
#PARSE(REFERENCE)        #@TYPE_REFS__INVALID_QUALIFIER #@PARSE_RULE_END !this
#AST_STRUCTURE_EXPECTED: #@TYPE_REFS__INVALID_QUALIFIER
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)        #@TYPE_REFS__QUALIFIER_DOT_ONLY #@PARSE_RULE_END !this
#AST_STRUCTURE_EXPECTED: #@TYPE_REFS__QUALIFIER_DOT_ONLY

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ tpl single arg
#PARSE(REFERENCE)        #error(NO_TPL_SINGLE_ARG)【#@TYPE_REFS__TPL_SINGLE_ARG】 ! #@SINGLE_ARG
#AST_STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG #@SINGLE_ARG)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #@TYPE_REFS__TPL_SINGLE_ARG !(foo)
#AST_STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS__TPL_SINGLE_ARG RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        foo! #error(EXPRULE_TemplateSingleArgument) .bar
#AST_STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier #@NO_ROE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        foo!this . xpto
#AST_STRUCTURE_EXPECTED: RefQualified(RefTemplateInstance(RefIdentifier ExpThis) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break
#PARSE(REFERENCE)         foo!(var[] #error(EXP_CLOSE_PARENS) #@BREAK !(blah)
#AST_STRUCTURE_EXPECTED:  RefTemplateInstance(RefIdentifier RefTypeDynArray(*))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break 
// Single arg missing doesn't cause rule to break
#PARSE(REFERENCE)         foo! #error(EXPRULE_TemplateSingleArgument) !(blah)
#AST_STRUCTURE_EXPECTED:  RefTemplateInstance(RefTemplateInstance(RefIdentifier #@NO_ROE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(var[] #error(EXP_CLOSE_PARENS) dummy #error(EXP_ID);
#AST_STRUCTURE_EXPECTED: 
InvalidDeclaration(RefTemplateInstance(RefIdentifier RefTypeDynArray(*)))
InvalidDeclaration(RefIdentifier)
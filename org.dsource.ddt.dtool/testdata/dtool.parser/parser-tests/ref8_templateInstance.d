▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(bar, 123) dummy;
foo!() dummy;
foo!xxx dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier Integer) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier) DefSymbol)
DefVariable(RefTemplateInstance(RefIdentifier RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG《 #@TYPE_REFS ● #@EXP_ASSIGN__NO_REFS__NO_AMBIGS ● 》
#@ARG_OR_MISS《 #@TYPE_REFS ● #@EXP_ASSIGN__NO_REFS__NO_AMBIGS ● #@NO_ROE》

#@ARG_COMMA《
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?AST_STRUCTURE_EXPECTED!【bar[foo*] , ● RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】● 
  ►#?AST_STRUCTURE_EXPECTED!【 #@NO_ROE, ● #@NO_ROE 】● 
¤》

#@TPL_ARGLIST《
  ►#?AST_STRUCTURE_EXPECTED!【!( #@ARG #@PaCLOSE_OR_NO ● #@ARG】●
  
  ►#?AST_STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_OR_MISS     #@PaCLOSE_OR_NO● #@ARG_COMMA #@ARG_OR_MISS】●
  ►#?AST_STRUCTURE_EXPECTED!【!(#@ARG_COMMA #@ARG_OR_MISS, #@ARG_OR_MISS #@PaCLOSE_OR_NO● * * #@ARG_OR_MISS】●

  ►#?AST_STRUCTURE_EXPECTED!【! #@SINGLE_ARG ● #@SINGLE_ARG 】●
¤》
#@SINGLE_ARG《 
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXPRULE_TplArg)● #@NO_EXP 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_PRIMARY_SIMPLE  ● #@EXP_PRIMARY_SIMPLE】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #@TYPE_REFS_QUALIFIER #@TPL_ARGLIST
#AST_STRUCTURE_EXPECTED: RefTemplateInstance(#@TYPE_REFS_QUALIFIER #@TPL_ARGLIST)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ indexing cannot be template start
#PARSE(REFERENCE)        #@TYPE_REFS_INVALID_QUALIFIER #parser(IgnoreRest) !this
#AST_STRUCTURE_EXPECTED: #@TYPE_REFS_INVALID_QUALIFIER

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
#PARSE(REFERENCE)
foo!(var[] #error(EXP_CLOSE_PARENS) #parser(IgnoreRest) !(blah)
#AST_STRUCTURE_EXPECTED:
RefTemplateInstance(RefIdentifier RefTypeDynArray(*))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Ensure ref rule break
#PARSE(REFERENCE)
foo! #error(EXPRULE_TemplateSingleArgument) /* Single arg missing doesn't cause rule to break */ !(blah)
#AST_STRUCTURE_EXPECTED:
RefTemplateInstance(RefTemplateInstance(RefIdentifier #@NO_ROE) RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo!(var[] #error(EXP_CLOSE_PARENS) dummy #error(EXP_ID);
#AST_STRUCTURE_EXPECTED: 
InvalidDeclaration(RefTemplateInstance(RefIdentifier RefTypeDynArray(*)))
InvalidDeclaration(RefIdentifier)
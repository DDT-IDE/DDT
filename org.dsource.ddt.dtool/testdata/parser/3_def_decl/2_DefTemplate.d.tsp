Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template params
#@MIXIN_PRF《#?STRUCTURE_EXPECTED!【mixin ● 】 ●/+ +/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   #@MIXIN_PRF template tpl(#@TPLPARAMS__SIMPLE_TESTER) #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLPARAMS__SIMPLE_TESTER #@DECLS_BLOCK__SAMPLE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (duplicate test case, here for clarity)
#PARSE(DECLARATION)   mixin #@NO_TYPEREF #error(EXP_SEMICOLON) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_TYPEREF)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@MIXIN_PRF template #error(EXP_ID) #@BREAK
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@MIXIN_PRF template tpl #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@MIXIN_PRF template tpl() #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@DECLS_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ args
#PARSE(DECLARATION)   template tpl( #@TPLPARAMS ) #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLPARAMS #@DECLS_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   template tpl( #@TPLPARAMS__SIMPLE_TESTER #error(EXP_CLOSE_PARENS) #@BREAK #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLPARAMS__SIMPLE_TESTER #@BREAK )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   template tpl ( #@TPLPARAMS__SIMPLE_TESTER ) #@TPL_CONSTRAINT #@DECLS_BLOCK
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLPARAMS__SIMPLE_TESTER #@TPL_CONSTRAINT #@DECLS_BLOCK )

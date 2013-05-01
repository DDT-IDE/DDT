Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template params
#@MIXIN_PRF《#?STRUCTURE_EXPECTED!【mixin ● 】 ●/+ +/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   #@MIXIN_PRF template tpl(#@TPL_PARAMS_SAMPLE1) #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@DECLS_BLOCK__SAMPLE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (duplicate test case, here for clarity)
#PARSE(DECLARATION)   mixin #@NO_REF #error(EXP_SEMICOLON) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_REF)
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
#PARSE(DECLARATION)   template tpl( #@TPLARGS ) #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLARGS #@DECLS_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   template tpl( #@TPL_PARAMS_SAMPLE1 #error(EXP_CLOSE_PARENS) #@BREAK #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@BREAK )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   template tpl ( #@TPL_PARAMS_SAMPLE1 ) #@TPL_CONSTRAINT #@DECLS_BLOCK
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@TPL_CONSTRAINT #@DECLS_BLOCK )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template params
#@MIXIN_PRF《#?AST_STRUCTURE_EXPECTED!【mixin ● 】 ●/+ +/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)       #@MIXIN_PRF template tpl(#@TPL_PARAMS_SAMPLE1) #@DECLS_BLOCK_SAMPLE
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@DECLS_BLOCK_SAMPLE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       mixin #error(EXP_OPEN_PARENS)¤【()】 #error(EXP_SEMICOLON)
#AST_STRUCTURE_EXPECTED:  DeclarationMixinString()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       #@MIXIN_PRF template #error(EXP_ID) #@BREAK
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       #@MIXIN_PRF template tpl #error(EXP_OPEN_PARENS)【】 #@BREAK
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       #@MIXIN_PRF template tpl() #@DECLS_BLOCK_SAMPLE
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@DECLS_BLOCK_SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ args
#PARSE(DECLARATION)       template tpl( #@TPLARGS ) #@DECLS_BLOCK_SAMPLE
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPLARGS #@DECLS_BLOCK_SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       template tpl( #@TPL_PARAMS_SAMPLE1 #error(EXP_CLOSE_PARENS) #@BREAK #@DECLS_BLOCK_SAMPLE
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@BREAK )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       template tpl ( #@TPL_PARAMS_SAMPLE1 ) #@TPL_CONSTRAINT #@DECLS_BLOCK
#AST_STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@TPL_PARAMS_SAMPLE1 #@TPL_CONSTRAINT #@DECLS_BLOCK )

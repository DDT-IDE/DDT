Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  duplicate case, for clarity
#PARSE(DECLARATION)   this #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED:  DefConstructor(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ duplicate case, for clarity
#PARSE(DECLARATION)   this ( #error(EXP_CLOSE_PARENS) #@BREAK
#STRUCTURE_EXPECTED:  DefConstructor(DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   this(this #error(EXP_CLOSE_PARENS) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationPostBlit()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   this(this) #@FN_BODY__SIMPLE_TESTER
#STRUCTURE_EXPECTED:  DeclarationPostBlit(#@FN_BODY__SIMPLE_TESTER)

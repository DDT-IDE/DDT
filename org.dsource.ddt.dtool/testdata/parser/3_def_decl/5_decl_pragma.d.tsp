Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
#PARSE(DECLARATION)  pragma(foo) #@NO_DECLorBLOCK #@BREAK
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol #@NO_DECLorBLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
#PARSE(DECLARATION)  pragma(foo) ;
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol DeclarationEmpty)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test expression list
#PARSE(DECLARATION)  pragma(foo, #@EXP_ARGS) : #@DECL_LIST_SAMPLE
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol #@EXP_ARGS DeclList( #@DECL_LIST_SAMPLE ))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
#PARSE(DECLARATION)  pragma(foo) #@NO_DECLorBLOCK #@BREAK
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol #@NO_DECLorBLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
#PARSE(DECLARATION)  pragma(foo) ;
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol DeclarationEmpty)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test expression list
#PARSE(DECLARATION)             pragma(foo,   #@EXPARGS_NE_ESEP) : #@DECL_LIST_SAMPLE
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol #@EXPARGS_NE_ESEP DeclList( #@DECL_LIST_SAMPLE ))

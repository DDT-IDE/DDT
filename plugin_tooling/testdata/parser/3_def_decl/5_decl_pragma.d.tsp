Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Note: most of pragma testing is done in base declAttrib file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
#PARSE(DECLARATION)  pragma(foo) #@NO_DECLorBLOCK 
#STRUCTURE_EXPECTED: DeclarationAttrib(AttribPragma(Symbol) #@NO_DECLorBLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
#PARSE(DECLARATION)  pragma(foo) ;
#STRUCTURE_EXPECTED: DeclarationAttrib(AttribPragma(Symbol) DeclarationEmpty)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test expression list
#PARSE(DECLARATION)             pragma(foo,   #@EXPARGS_NE_ESEP) : #@DECL_LIST_SAMPLE
#STRUCTURE_EXPECTED: DeclarationAttrib(AttribPragma(Symbol #@EXPARGS_NE_ESEP) DeclList( #@DECL_LIST_SAMPLE ))

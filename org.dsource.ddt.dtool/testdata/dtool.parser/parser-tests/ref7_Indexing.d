▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[int] dummy;
int[foo][Bar] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIdentifier RefPrimitive) DefSymbol)
DefVariable(RefIndexing(RefIndexing(RefPrimitive RefIdentifier) RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[1] dummy;
int[123] [4] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIdentifier ExpLiteralInteger) DefSymbol)
DefVariable(RefIndexing(RefIndexing(RefPrimitive ExpLiteralInteger) ExpLiteralInteger)  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[int] #error:EXP_ID ;
foo[4] #error:EXP_ID ;

#AST_SOURCE_EXPECTED:
foo[int] ;
foo[4] ;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefIndexing(RefIdentifier RefPrimitive))
InvalidDeclaration(RefIndexing(RefIdentifier ExpLiteralInteger))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPE_REFS[int] dummy1;
Foo[#@TYPE_REFS] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(#@TYPE_REFS RefPrimitive) DefSymbol)
DefVariable(RefIndexing(RefIdentifier #@TYPE_REFS) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@^TYPE_REFS[#@^TYPE_REFS] dummy1;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPE_REFS[#@EXP_ANY] dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(#@TYPE_REFS #@EXP_ANY) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo1[#@TYPE_REFS   #error:EXP_CLOSE_BRACKET public int dummy ;
foo2[#@TYPE_REFS   #error:EXP_CLOSE_BRACKET ;
foo3[#@TYPE_REFS   #error:EXP_CLOSE_BRACKET 

#AST_SOURCE_EXPECTED:
foo1[#@TYPE_REFS] public int dummy;
foo2[#@TYPE_REFS] ;
foo3[#@TYPE_REFS] 
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefIndexing(RefIdentifier #@TYPE_REFS))  ?(DefinitionVariable(RefPrimitive ?))
InvalidDeclaration(RefIndexing(RefIdentifier #@TYPE_REFS))  DeclarationEmpty
InvalidDeclaration(RefIndexing(RefIdentifier #@TYPE_REFS))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo1[#@EXP_ANY   #error:EXP_CLOSE_BRACKET public int dummy ;
foo2[#@EXP_ANY   #error:EXP_CLOSE_BRACKET ;
foo3[#@EXP_ANY   #error:EXP_CLOSE_BRACKET 

#AST_SOURCE_EXPECTED:
foo1[#@EXP_ANY]  public int dummy;
foo2[#@EXP_ANY]  ;
foo3[#@EXP_ANY] 
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefIndexing(RefIdentifier #@EXP_ANY))  ?(DefinitionVariable(RefPrimitive DefSymbol))
InvalidDeclaration(RefIndexing(RefIdentifier #@EXP_ANY))  DeclarationEmpty
InvalidDeclaration(RefIndexing(RefIdentifier #@EXP_ANY))  

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

#@SP_TYPE_REF[int] dummy1;
Foo[#@SP_TYPE_REF] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(#@SPSE_TYPE_REF(SP_TYPE_REF) RefPrimitive) DefSymbol)
DefVariable(RefIndexing(RefIdentifier #@SPSE_TYPE_REF(SP_TYPE_REF)) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@^SP_TYPE_REF[#@^SP_TYPE_REF] dummy1;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@SP_TYPE_REF[#@EXP_ANY] dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIndexing(#@SPSE_TYPE_REF(SP_TYPE_REF) #@EXP_ANY) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo1[#@SP_TYPE_REF   #error:EXP_CLOSE_BRACKET public int dummy ;
foo2[#@SP_TYPE_REF   #error:EXP_CLOSE_BRACKET ;
foo3[#@SP_TYPE_REF   #error:EXP_CLOSE_BRACKET 

#AST_SOURCE_EXPECTED:
foo1[#@SP_TYPE_REF] public int dummy;
foo2[#@SP_TYPE_REF] ;
foo3[#@SP_TYPE_REF] 
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefIndexing(RefIdentifier #@SPSE_TYPE_REF(SP_TYPE_REF)))  ?(DefinitionVariable(RefPrimitive ?))
InvalidDeclaration(RefIndexing(RefIdentifier #@SPSE_TYPE_REF(SP_TYPE_REF)))  DeclarationEmpty
InvalidDeclaration(RefIndexing(RefIdentifier #@SPSE_TYPE_REF(SP_TYPE_REF)))

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

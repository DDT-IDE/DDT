▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

Foo foo;
int xx;
Bar.foo foo = 2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIdentifier DefSymbol)
DefVariable(RefPrimitive DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SP_TYPE_REF fooA;
#@SP_TYPE_REF fooB = 55 ;
#@SP_TYPE_REF fooC = 1, foo2    , foo3 = 3;
Bar.Foo[]     fooD    , foo2 = 2 , foo3;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))
DefVariable(* DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))
DefVariable(* DefSymbol                   DefVarFragment(DefSymbol InitializerExp(?)) DefVarFragment(DefSymbol))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SP_TYPE_REF fooB = #@EXP_ASSIGN;
#@SP_TYPE_REF fooC = #@^EXP_ASSIGN, foo2    , foo3 = #@EXP_ASSIGN;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol InitializerExp(#@EXP_ASSIGN))
DefVariable(* DefSymbol InitializerExp(*) DefVarFragment(DefSymbol) DefVarFragment(? InitializerExp(#@EXP_ASSIGN)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PRX1!《#NO_PRX1(flag)●xxx,》
#@BREAK!《import ruleBREAK;》
#@BREAK_EXP!《DeclarationImport(*)》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors

#@SP_TYPE_REF #@PRX1 #error:EXP_IDENTIFIER #error:EXP_SEMICOLON  #@BREAK
#@SP_TYPE_REF #@PRX1 #error:EXP_IDENTIFIER ;

#@SP_TYPE_REF #@PRX1 fooA #error:EXP_SEMICOLON  #@BREAK
#@SP_TYPE_REF #@PRX1 fooB = #error:EXPRULE_INITIALIZER #error:EXP_SEMICOLON  #@BREAK
#@SP_TYPE_REF #@PRX1 fooD = #error:EXPRULE_INITIALIZER ;

#comment(NO_STDOUT)
#AST_SOURCE_EXPECTED:

#@SP_TYPE_REF #@PRX1 #?NO_PRX1{,;} #@BREAK
#@SP_TYPE_REF #@PRX1 ;

#@SP_TYPE_REF #@PRX1 fooA ;  #@BREAK
#@SP_TYPE_REF #@PRX1 fooB = ;  #@BREAK
#@SP_TYPE_REF #@PRX1 fooD =  ;

#AST_STRUCTURE_EXPECTED:
#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} #@BREAK_EXP
#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} 

DefVariable(* DefSymbol #?NO_PRX1【●                 DefVarFragment(DefSymbol)】 ) #@BREAK_EXP
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? InitializerExp(MissingExpression))】) #@BREAK_EXP
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? InitializerExp(MissingExpression))】)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors

#@SP_TYPE_REF #@PRX1 fooE = 112 #error:EXP_SEMICOLON  #@BREAK
#@SP_TYPE_REF #@PRX1 fooF = 112 = "666";

#comment(NO_STDOUT)
#AST_SOURCE_EXPECTED:
#@SP_TYPE_REF #@PRX1 fooE = 112 ;  #@BREAK
#@SP_TYPE_REF #@PRX1 fooF = 112 = "666";

#AST_STRUCTURE_EXPECTED:

DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? InitializerExp(Integer))】) 
#@BREAK_EXP
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(InfixExpression(Integer String))●
		DefVarFragment(? InitializerExp(InfixExpression(Integer String)))】)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors (with multiple exp)

#@SP_TYPE_REF #@PRX1 fooE = #@EXP_ASSIGN #error:EXP_SEMICOLON  #@BREAK
#@SP_TYPE_REF #@PRX1 fooF = #@EXP_CONDITIONAL = "666";

#AST_SOURCE_EXPECTED:

#@SP_TYPE_REF #@PRX1 fooE = #@EXP_ASSIGN ;  #@BREAK
#@SP_TYPE_REF #@PRX1 fooF = #@EXP_CONDITIONAL = "666";

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:

DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(#@EXP_ASSIGN)●DefVarFragment(? InitializerExp(#@EXP_ASSIGN))】) 
#@BREAK_EXP
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(InfixExpression(#@EXP_CONDITIONAL String))●
		DefVarFragment(? InitializerExp(InfixExpression(#@EXP_CONDITIONAL String)))】)


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls
#comment(TODO):
C style decls
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#AST_STRUCTURE_EXPECTED:


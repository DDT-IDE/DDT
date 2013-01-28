▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

Foo foo;
int xx;
Bar.foo foo = 2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIdentifier DefSymbol)
DefVariable(RefPrimitive DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@(SP_TYPE_REF) fooA;
#@(SP_TYPE_REF) fooB = 55;
#@(SP_TYPE_REF) fooC = 1, foo2    , foo3 = 3;
//Bar.Foo[]       fooD    , foo2 = 2, foo3;

#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))
DefVariable(* DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))
#TODO:
DefVariable(* DefSymbol InitializerExp(?) DefVarFragment(DefSymbol InitializerExp(?)) DefVarFragment(DefSymbol))


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors
#@PRX1!《#NO_PRX1(flag)●xxx,》
#@BREAK!《import ruleBREAK;》
#@BREAK_EXP!《DeclarationImport(*)》

#@(SP_TYPE_REF) #@(PRX1) #error:EXP_IDENTIFIER #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) #error:EXP_IDENTIFIER ;

#@(SP_TYPE_REF) #@(PRX1) fooA #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooB = #error:EXPRULE_INITIALIZER #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooD = #error:EXPRULE_INITIALIZER ;
#@(SP_TYPE_REF) #@(PRX1) fooE = 112 #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooF = 112 #error:EXP_SEMICOLON = 666;

#AST_SOURCE_EXPECTED:

#@(SP_TYPE_REF) #@(PRX1) ;  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) ;

#@(SP_TYPE_REF) #@(PRX1) fooA ;  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooB = ;  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooD =  ;
#@(SP_TYPE_REF) #@(PRX1) fooE = 112 ;  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooF = 112 ; = 666;

#AST_STRUCTURE_EXPECTED:

#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} #@(BREAK_EXP)
#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} 

DefVariable(* DefSymbol #?NO_PRX1{                  , DefVarFragment(DefSymbol) } ) #@(BREAK_EXP)
DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(?) , DefVarFragment(DefSymbol InitializerExp(MissingExpression)) }) #@(BREAK_EXP)
DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(?) , DefVarFragment(DefSymbol InitializerExp(MissingExpression)) })
DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(?) , DefVarFragment(DefSymbol InitializerExp(ExpLiteralInteger)) }) #@(BREAK_EXP)
DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(?) , DefVarFragment(DefSymbol InitializerExp(ExpLiteralInteger)) })
MiscDeclaration
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls
#TODO:
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#AST_STRUCTURE_EXPECTED:


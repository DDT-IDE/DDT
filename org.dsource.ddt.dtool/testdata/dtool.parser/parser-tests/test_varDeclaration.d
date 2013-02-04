▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

Foo foo;
int xx;
Bar.foo foo = 2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIdentifier DefSymbol)
DefVariable(RefPrimitive DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@(SP_TYPE_REF) fooA;
#@(SP_TYPE_REF) fooB = 55;
#@(SP_TYPE_REF) fooC = 1, foo2    , foo3 = 3;
Bar.Foo[]       fooD    , foo2 = 2, foo3;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol)
DefVariable(* DefSymbol InitializerExp(?))
DefVariable(* DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))
DefVariable(* DefSymbol                   DefVarFragment(DefSymbol InitializerExp(?)) DefVarFragment(DefSymbol))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@(SP_TYPE_REF) fooB = #@SP_EXP;
#@(SP_TYPE_REF) fooC = #@^SP_EXP, foo2    , foo3 = #@SP_EXP;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol InitializerExp(#@SPSE_EXP(SP_EXP)))
DefVariable(* DefSymbol InitializerExp(*) 
		DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(#@SPSE_EXP(SP_EXP))))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PRX1!《#NO_PRX1(flag)●xxx,》
#@BREAK!《import ruleBREAK;》
#@BREAK_EXP!《DeclarationImport(*)》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors

#@(SP_TYPE_REF) #@(PRX1) #error:EXP_IDENTIFIER #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) #error:EXP_IDENTIFIER ;

#@(SP_TYPE_REF) #@(PRX1) fooA #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooB = #error:EXPRULE_INITIALIZER #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooD = #error:EXPRULE_INITIALIZER ;
#@(SP_TYPE_REF) #@(PRX1) fooE = 112 #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooF = 112 #error:EXP_SEMICOLON #error:SE_decl = #error:SE_decl 666;

#comment(NO_STDOUT)
#AST_SOURCE_EXPECTED:

#@(SP_TYPE_REF) #@(PRX1) #?NO_PRX1{,;} #@(BREAK)
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
InvalidSyntaxElement InvalidSyntaxElement DeclarationEmpty

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors (with multiple exp)

#@(SP_TYPE_REF) #@(PRX1) fooE = #@SP_EXP #error:EXP_SEMICOLON  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooF = #@SP_EXP #error:EXP_SEMICOLON #error:SE_decl = #error:SE_decl 666;

#AST_SOURCE_EXPECTED:

#@(SP_TYPE_REF) #@(PRX1) fooE = #@SP_EXP ;  #@(BREAK)
#@(SP_TYPE_REF) #@(PRX1) fooF = #@SP_EXP ; = 666;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:

DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(#@SPSE_EXP(SP_EXP)) , 
		DefVarFragment(DefSymbol InitializerExp(#@SPSE_EXP(SP_EXP))) }) #@(BREAK_EXP)
DefVariable(* DefSymbol #?NO_PRX1{InitializerExp(#@SPSE_EXP(SP_EXP)) , 
		DefVarFragment(DefSymbol InitializerExp(#@SPSE_EXP(SP_EXP))) })
InvalidSyntaxElement InvalidSyntaxElement DeclarationEmpty

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls
#comment(TODO):
C style decls
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#AST_STRUCTURE_EXPECTED:


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases

Foo foo;
int xx;
Bar.foo foo = 2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefIdentifier DefSymbol)
DefVariable(RefPrimitive DefSymbol)
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(?))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS__NO_BROKEN fooA;
#@TYPE_REFS__NO_BROKEN fooB = 55 ;
#@TYPE_REFS__NO_BROKEN fooC = 1, foo2    , foo3 = 3;
Bar.Foo[]     fooD    , foo2 = 2 , foo3;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol)
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(?))
DefVariable(* DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))
DefVariable(* DefSymbol                   DefVarFragment(DefSymbol InitializerExp(?)) DefVarFragment(DefSymbol))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS__NO_BROKEN fooB = #@EXP_ASSIGN;
#@TYPE_REFS__NO_BROKEN fooC = #@^EXP_ASSIGN, foo2    , foo3 = #@EXP_ASSIGN;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol InitializerExp(#@EXP_ASSIGN))
DefVariable(* DefSymbol InitializerExp(*) DefVarFragment(DefSymbol) DefVarFragment(? InitializerExp(#@EXP_ASSIGN)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PRX1!《#NO_PRX1(flag)●xxx,》
#@SEPARATOR!《 #?AST_STRUCTURE_EXPECTED!【align int ruleBREAK;● DeclarationAlign(*)】 》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors

#@TYPE_REFS__NO_BROKEN #@PRX1 #error:EXP_IDENTIFIER #?NO_PRX1{,#error:EXP_SEMICOLON} #@SEPARATOR
#@TYPE_REFS__NO_BROKEN #@PRX1 #error:EXP_IDENTIFIER ;

#@TYPE_REFS__NO_BROKEN #@PRX1 fooA #error:EXP_SEMICOLON  #@SEPARATOR
#@TYPE_REFS__NO_BROKEN #@PRX1 fooB = #@NO_INIT #error:EXP_SEMICOLON  #@SEPARATOR
#@TYPE_REFS__NO_BROKEN #@PRX1 fooD = #@NO_INIT ;

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} #@SEPARATOR
#?NO_PRX1{InvalidDeclaration(*) ,  DefVariable(* DefSymbol DefVarFragment(?))} 

DefVariable(* DefSymbol #?NO_PRX1【●                 DefVarFragment(DefSymbol)】 ) #@SEPARATOR
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? #@NO_INIT)】) #@SEPARATOR
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? #@NO_INIT)】)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors

#@TYPE_REFS__NO_BROKEN #@PRX1 fooE = 112 #error:EXP_SEMICOLON  #@SEPARATOR
#@TYPE_REFS__NO_BROKEN #@PRX1 fooF = 112 = "666";

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(?)●DefVarFragment(? InitializerExp(Integer))】) 
#@SEPARATOR
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(ExpInfix(Integer String))●
		DefVarFragment(? InitializerExp(ExpInfix(Integer String)))】)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors (with multiple exp)

#@TYPE_REFS__NO_BROKEN #@PRX1 fooE = #EXP__NO_BREAKING_EXPS(flag) #@EXP_ASSIGN #error:EXP_SEMICOLON  #@SEPARATOR
#@TYPE_REFS__NO_BROKEN #@PRX1 fooF = #EXP__NO_BREAKING_EXPS(flag) #@EXP_CONDITIONAL = "666";

#comment(NO_STDOUT)
#AST_STRUCTURE_EXPECTED:

DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(#@EXP_ASSIGN)●DefVarFragment(? InitializerExp(#@EXP_ASSIGN))】) 
#@SEPARATOR
DefVariable(* DefSymbol #?NO_PRX1【InitializerExp(ExpInfix(#@EXP_CONDITIONAL String))●
		DefVarFragment(? InitializerExp(ExpInfix(#@EXP_CONDITIONAL String)))】)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls
#comment(TODO):
C style decls
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#AST_STRUCTURE_EXPECTED:


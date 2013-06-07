Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@DISCARD_INVALID《#?MIXIN_EXP{#:DISCARD_CASE} #?BLOCK_ST_LIKE_EXP{#:DISCARD_CASE} #?EXP__DECL_AMBIG{#:DISCARD_CASE}》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    #@EXP_NE #@DISCARD_INVALID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: StatementExpression(#@EXP_NE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     123 #@NO_SEMICOLON #@BREAK bar;
#STRUCTURE_EXPECTED:  StatementExpression( Integer )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ StatementExp vs. variable declaration
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ make sure it doesn't break the parsing of DefinitionVariable
#PARSE(STATEMENT)     foo[bar*] bar;
#STRUCTURE_EXPECTED:  DefinitionVariable(* DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  test ambiguous case (var decl takes precedence)
#PARSE(STATEMENT)     foo * bar;
#STRUCTURE_EXPECTED:  DefinitionVariable(RefTypePointer(?) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  test broken case
#PARSE(STATEMENT)     foo[bar #@NO_CloseBRACKET #@NO_SEMICOLON #@BREAK  bar;
#STRUCTURE_EXPECTED:  StatementExpression(*)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  test broken case
#PARSE(STATEMENT)     123[bar #@NO_CloseBRACKET #@NO_SEMICOLON #@BREAK  bar;
#STRUCTURE_EXPECTED:  StatementExpression(ExpIndex(* *))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ StatementExp vs. function declaration
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    Object* foo(Object) {  } #@IGNORE_REST ;
#STRUCTURE_EXPECTED: DefFunction(#@_FOO_PTR DefSymbol NamelessParameter(?)  BlockStatement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    Object* foo(#@TPLPARAMS__SIMPLE_TESTER) (Object) {  }
#STRUCTURE_EXPECTED: DefFunction(#@_FOO_PTR DefSymbol #@TPLPARAMS__SIMPLE_TESTER NamelessParameter(?)  BlockStatement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    Object* foo(Object) ;
#STRUCTURE_EXPECTED: DefFunction(#@_FOO_PTR DefSymbol NamelessParameter(?)  EmptyStatement)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ BUG here TODO 
#PARSE(STATEMENT)    Object * foo(Object++) ;
#STRUCTURE_EXPECTED: StatementExp(ExpInfix( #@ExpIdentifier  ExpCall(#@ExpIdentifier ExpPostfix(#@ExpIdentifier)) ))
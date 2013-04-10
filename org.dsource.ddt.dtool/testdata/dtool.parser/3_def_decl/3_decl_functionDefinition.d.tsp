▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
Foo fn();
int xx(Foo foo, .Bar.Baz baz) { }
int xx(...);

#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@EB)
DefFunction(? DefSymbol
  FunctionParameter(RefIdentifier DefSymbol) FunctionParameter(RefQualified(* *) DefSymbol) BlockStatement
)
DefFunction(RefPrimitive DefSymbol CStyleVarArgsParameter #@EB)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
inout(xpto) fn( #@FN_PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) fn( #@FN_PARAMS_SAMPLE ) in {int foo; } out(result) { } body { int foo; }
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE InOutFunctionBody(* FunctionBodyOutBlock(Symbol *) *))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases with template params
inout(xpto) func(#@TPL_PARAMS_SAMPLE1) ( #@FN_PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) func(#@TPL_PARAMS_SAMPLE1) ( #@FN_PARAMS_SAMPLE ) if( #@EXP_ANY__LITE ) body { int foo; }
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS_SAMPLE1 #@FN_PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS_SAMPLE1 #@FN_PARAMS_SAMPLE #@EXP_ANY__LITE FunctionBody(*) )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ return type reference
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@TYPE_REFS__NO_BROKEN func(#@FN_PARAMS_SAMPLE) #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:
DefFunction(#@TYPE_REFS__NO_BROKEN DefSymbol #@FN_PARAMS_SAMPLE #@SAMPLE_BLOCK_ST )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ auto ref
#PARSE(DECLARATION)  auto func(#@FN_PARAMS_SAMPLE) #@SAMPLE_BLOCK_ST 
#STRUCTURE_EXPECTED: DefFunction(AutoReturnReference DefSymbol #@FN_PARAMS_SAMPLE #@SAMPLE_BLOCK_ST )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ 0 args

#@FN_AFTER_PARAM《
  ►#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK { }●】●
  ►#?STRUCTURE_EXPECTED!【 ) #@SAMPLE_BLOCK_ST   ● #@SAMPLE_BLOCK_ST】●
  ►#?STRUCTURE_EXPECTED!【 ) #error(EXPRULE_Body) #@BREAK● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  0 param
#PARSE(DECLARATION)   const(foo) func0( #@FN_AFTER_PARAM
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  1 param
#PARSE(DECLARATION)   const(foo) func1( #FN_PARAMETER_TEST(test) #@FN_PARAM_SINGLE ) #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_PARAM_SINGLE #@SAMPLE_BLOCK_ST)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 2+ params
#PARSE(DECLARATION)   const(foo) func2( #@FN_PARAMS ) #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS #@SAMPLE_BLOCK_ST )


Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Template args
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template 0 args
#PARSE(DECLARATION)   const(foo) tplFunc() ( #@FN_AFTER_PARAM
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   Foo tplFunc( #@TPL_PARAMS_SAMPLE1 ) #error(EXP_OPEN_PARENS)【】 #@BREAK #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPL_PARAMS_SAMPLE1 #@BREAK #@SAMPLE_BLOCK_ST )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   Foo tplFunc( #@TPLARGS ) ( #@FN_PARAMS_SAMPLE ) #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPLARGS #@FN_PARAMS_SAMPLE #@SAMPLE_BLOCK_ST )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function  Attributes

#@NO_BODY《#error(EXPRULE_Body) #@BREAK》

#@FN_ATTRIBS_END《
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB ;                    ● #@EB】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #@SAMPLE_BLOCK_ST    ● #@SAMPLE_BLOCK_ST】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #error(EXPRULE_Body) #@BREAK● 】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB body {} ● FunctionBody(*)】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB in {} body {} ● InOutFunctionBody(* *)】●  
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB out(foo) {} body {} ● InOutFunctionBody(* *)】●
  
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #@NO_BODY #@BREAK @invalidProperty  { }● 】●
  ►#?STRUCTURE_EXPECTED!【#@NO_BODY #@BREAK ref               { }● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   inout(Foo) funcAttrib( #@FN_PARAMS_SAMPLE ) #@FN_ATTRIBS_END
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE #@FN_ATTRIBS_END )
Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Template constraint
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   Foo funcTplConstraint( ) ( #@FN_PARAMS_SAMPLE )  #@TPL_CONSTRAINT #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:  DefFunction(? DefSymbol #@FN_PARAMS_SAMPLE #@TPL_CONSTRAINT #@SAMPLE_BLOCK_ST  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  /* needs tpl args to parse from if constraint */
#PARSE(DECLARATION)   Foo funcTplConstraint ( #@FN_PARAMS_SAMPLE ) #@NO_BODY #@BREAK  #@TPL_CONSTRAINT #@SAMPLE_BLOCK_ST
#STRUCTURE_EXPECTED:  DefFunction(RefIdentifier DefSymbol #@FN_PARAMS_SAMPLE #@NO_BODY #@BREAK  )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Body
#@BLK《                   ►#?STRUCTURE_EXPECTED!【 { /*TODO statement*/ } ● BlockStatement 】● ¤》
#@NO_BLOCK《       ►#?STRUCTURE_EXPECTED!【 #error(EXPRULE_Block) ● BlockStatement 】● ¤》
#@NO_PARENS《     ►#?STRUCTURE_EXPECTED!【 #error(EXP_OPEN_PARENS)【】 #@BREAK● #@BREAK $ 】● ¤》

#@IN_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【 in #@BLK● #@BLK】●  
 ►#?STRUCTURE_EXPECTED!【 in #@NO_BLOCK● #@NO_BLOCK 】●
¤》
#@OUT_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【 out(foo) #@BLK●      FunctionBodyOutBlock(Symbol #@BLK )】●  
 ►#?STRUCTURE_EXPECTED!【 out(foo) #@NO_BLOCK● FunctionBodyOutBlock(Symbol #@NO_BLOCK )】●
 
 ►#?STRUCTURE_EXPECTED!【 out(foo #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【 out(#@NO_ID #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【 out#@NO_PARENS #@BREAK● FunctionBodyOutBlock(   $ )】●
¤》

#@AFTER_PARAM《  /**/ ● nothrow 》
#@OUT_BLOCK《►#?STRUCTURE_EXPECTED!【 out(foo) #@BLK●      FunctionBodyOutBlock(Symbol #@BLK )】●¤》
 
#@FN_BODY_END《
 ►#?STRUCTURE_EXPECTED!【 #@NO_BODY         ● #@NO_BODY】●
 ►#?STRUCTURE_EXPECTED!【 ;                 ● #@EB】●
 ►#?STRUCTURE_EXPECTED!【 #@SAMPLE_BLOCK_ST ● #@SAMPLE_BLOCK_ST】●
 ►#?STRUCTURE_EXPECTED!【 body #@BLK        ● FunctionBody( #@BLK )】●
 ►#?STRUCTURE_EXPECTED!【 body #@NO_BLOCK /**/● FunctionBody( #@NO_BLOCK )】●
 
 ►#?STRUCTURE_EXPECTED!【  #@IN_BLOCKS body #@BLK             ●InOutFunctionBody( #@IN_BLOCKS #@BLK )】●
 ►#?STRUCTURE_EXPECTED!【  #@IN_BLOCKS #@OUT_BLOCKS body #@BLK●InOutFunctionBody( #@IN_BLOCKS #@OUT_BLOCKS #@BLK )】●
 ►#?STRUCTURE_EXPECTED!【  #@OUT_BLOCKS body #@BLK            ●InOutFunctionBody( #@OUT_BLOCKS #@BLK )】●
 ►#?STRUCTURE_EXPECTED!【  #@OUT_BLOCKS #@IN_BLOCKS body #@BLK●InOutFunctionBody( #@OUT_BLOCKS #@IN_BLOCKS #@BLK )】●
 
 ►#?STRUCTURE_EXPECTED!【  in #@BLK   #@NO_BODY  #@BLK              ●InOutFunctionBody( #@BLK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【  in #@BLK   #@NO_BODY  in #@BLK body #@BLK●InOutFunctionBody( #@BLK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@BLK            ●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@OUT_BLOCK #@BLK●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   inout(xpto) funcBody( #@FN_PARAMS_SAMPLE ) #@AFTER_PARAM #@FN_BODY_END
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE #@FN_BODY_END )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
Foo fn();
int xx(Foo foo, .Bar.Baz baz) { }
int xx(...);

#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@EMPTY_BODY)
DefFunction(? DefSymbol
  FunctionParameter(RefIdentifier DefSymbol) FunctionParameter(RefQualified(* *) DefSymbol) BlockStatement
)
DefFunction(RefPrimitive DefSymbol CStyleVarArgsParameter #@EMPTY_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
inout(xpto) fn( #@FN_PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) fn( #@FN_PARAMS_SAMPLE ) in {int foo; } out(result) { } body { int foo; }
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@FN_PARAMS_SAMPLE InOutFunctionBody(* FunctionBodyOutBlock(Symbol *) *))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases with template params
inout(xpto) func(#@TPL_PARAMS__SAMPLE) ( #@FN_PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) func(#@TPL_PARAMS__SAMPLE) ( #@FN_PARAMS_SAMPLE ) if( #@EXP_ANY__LITE ) body { int foo; }
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS__SAMPLE #@FN_PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS__SAMPLE #@FN_PARAMS_SAMPLE #@EXP_ANY__LITE FunctionBody(*) )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ return type reference
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@TYPE_REFS__NO_BROKEN func(#@FN_PARAMS_SAMPLE) #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefFunction(#@TYPE_REFS__NO_BROKEN DefSymbol #@FN_PARAMS_SAMPLE #@ST_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ auto ref
#PARSE(DECLARATION)  auto func(#@FN_PARAMS_SAMPLE) #@ST_BLOCK__SAMPLE 
#STRUCTURE_EXPECTED: DefFunction(AutoReference DefSymbol #@FN_PARAMS_SAMPLE #@ST_BLOCK__SAMPLE )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ 0 args

#@FN_AFTER_PARAM《
  ►#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK { }●】●
  ►#?STRUCTURE_EXPECTED!【 ) #@ST_BLOCK__SAMPLE   ● #@ST_BLOCK__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【 ) #error(EXPRULE_FnBody) #@BREAK● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  0 param
#PARSE(DECLARATION)   const(foo) func0( #@FN_AFTER_PARAM
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  1+ param
#PARSE(DECLARATION)   const(foo) func1( #FN_PARAMETER_TEST(test) #@FN_PARAMS ) #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_PARAMS #@ST_BLOCK__SAMPLE)

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Template args
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template 0 args
#PARSE(DECLARATION)   const(foo) tplFunc() ( #@FN_AFTER_PARAM
#STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   Foo tplFunc( #@TPL_PARAMS__SAMPLE ) #error(EXP_OPEN_PARENS)【】 #@BREAK #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPL_PARAMS__SAMPLE #@BREAK #@ST_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   Foo tplFunc( #@TPLPARAMS #?FN_UNAMBIG_ARG【#:DISCARD_CASE】 ) ( #@FN_PARAMS_SAMPLE ) #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPLPARAMS #@FN_PARAMS_SAMPLE #@ST_BLOCK__SAMPLE )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function  Attributes

#@FN_ATTRIBS_END《
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB ;                     ● #@EMPTY_BODY】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #@ST_BLOCK__SAMPLE    ● #@ST_BLOCK__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #error(EXPRULE_FnBody) #@BREAK● 】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB body {} ● FunctionBody(*)】●
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB in {} body {} ● InOutFunctionBody(* *)】●  
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB out(foo) {} body {} ● InOutFunctionBody(* *)】●
  
  ►#?STRUCTURE_EXPECTED!【#@FN_ATTRIB #@NO_BODY #@BREAK @invalidProperty  { }● 】●
  ►#?STRUCTURE_EXPECTED!【#@NO_BODY #@BREAK ref               { }● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   inout(Foo) funcAttrib( #@FN_PARAMS_SAMPLE ) #@FN_ATTRIBS_END
#STRUCTURE_EXPECTED:  DefFunction(* DefSymbol #@FN_PARAMS_SAMPLE #@FN_ATTRIBS_END )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Template constraint
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   Foo funcTplConstraint( ) ( #@FN_PARAMS_SAMPLE )  #@TPL_CONSTRAINT #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefFunction(? DefSymbol #@FN_PARAMS_SAMPLE #@TPL_CONSTRAINT #@ST_BLOCK__SAMPLE  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   Foo funcTplConstraint( ) ( #@FN_PARAMS_SAMPLE ) #@FN_ATTRIB__SAMPLE #@TPL_CONSTRAINT__SAMPLES #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefFunction(? DefSymbol #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE #@TPL_CONSTRAINT__SAMPLES #@ST_BLOCK__SAMPLE  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  /* needs tpl args to parse from if constraint */
#PARSE(DECLARATION)   Foo funcTplConstraint ( #@FN_PARAMS_SAMPLE ) #@NO_BODY #@BREAK  #@TPL_CONSTRAINT #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefFunction(RefIdentifier DefSymbol #@FN_PARAMS_SAMPLE #@NO_BODY #@BREAK  )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Body

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   inout(xpto) funcBody( #@FN_PARAMS_SAMPLE ) #@FN_ATTRIB__SAMPLE #@FNDEF_BODY
#STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol   #@FN_PARAMS_SAMPLE   #@FN_ATTRIB__SAMPLE #@FNDEF_BODY )

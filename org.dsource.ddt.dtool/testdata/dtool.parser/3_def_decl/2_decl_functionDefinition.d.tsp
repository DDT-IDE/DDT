▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
Foo fn();
int xx(Foo foo, .Bar.Baz baz) { }
int xx(...);

#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@EB)
DefFunction(? DefSymbol
  FunctionParameter(RefIdentifier DefSymbol) FunctionParameter(RefQualified(* *) DefSymbol) BlockStatement
)
DefFunction(RefPrimitive DefSymbol CStyleVarArgsParameter #@EB)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PARAMS_SAMPLE《#?AST_STRUCTURE_EXPECTED!【 .Bar.Baz baz, const Foo.Foobar foo = 2● 
  FunctionParameter(RefQualified(RefModuleQualified(?) RefIdentifier) DefSymbol)
  FunctionParameter(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)
】》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
inout(xpto) fn( #@PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) fn( #@PARAMS_SAMPLE ) in {int foo; } out(result) { } body { int foo; }
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@PARAMS_SAMPLE InOutFunctionBody(* FunctionBodyOutBlock(Symbol *) *))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with template params
#@TPL_PARAMS_SAMPLE1《#?AST_STRUCTURE_EXPECTED!【
  ►T : int = bar, ambigA = ambigB, int R : 10 = 1, alias A1 : 12 + 2 = foo, alias A2 : int = 2, TUPLE ..., this V●
  
  TemplateTypeParam(DefSymbol RefPrimitive RefIdentifier)
  TemplateTypeParam(DefSymbol RefIdentifier)
  TemplateValueParam(RefPrimitive DefSymbol Integer Integer)
  TemplateAliasParam(DefSymbol ExpInfix(? ?) RefIdentifier)
  TemplateAliasParam(DefSymbol RefPrimitive Integer)
  TemplateTupleParam(DefSymbol)
  TemplateThisParam(DefSymbol)
】》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with template
inout(xpto) func(#@TPL_PARAMS_SAMPLE1) ( #@PARAMS_SAMPLE ) nothrow body { int foo; }
inout(xpto) func(#@TPL_PARAMS_SAMPLE1) ( #@PARAMS_SAMPLE ) if( #@EXP_ANY__LITE ) body { int foo; }
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS_SAMPLE1 #@PARAMS_SAMPLE FunctionBody(*) )
DefFunction( RefTypeModifier(?) DefSymbol #@TPL_PARAMS_SAMPLE1 #@PARAMS_SAMPLE #@EXP_ANY__LITE FunctionBody(*) )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ test return type
#PARSE(DECLARATION)       #@TYPE_REFS func(#@PARAMS_SAMPLE) #@SAMPLE_BLOCK_ST
#AST_STRUCTURE_EXPECTED:
DefFunction(#@TYPE_REFS DefSymbol #@PARAMS_SAMPLE #@SAMPLE_BLOCK_ST )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ 0 args

#@FN_AFTER_PARAM《
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK { }●】●
  ►#?AST_STRUCTURE_EXPECTED!【 ) #@SAMPLE_BLOCK_ST   ● #@SAMPLE_BLOCK_ST】●
  ►#?AST_STRUCTURE_EXPECTED!【 ) #error(EXPRULE_Body) #@BREAK● 】●
¤》
▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
#PARSE(DECLARATION)       const(foo) func0( #@FN_AFTER_PARAM
#AST_STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ 1 args
#@BREAK_Pr《 #?AST_STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK● $ 】  》

#@PARAM_ATTRIB《auto●#@TYPE_MODIFIERS●final●in●lazy●out●ref●scope●in lazy ref ●const inout●const lazy shared》
#@PARAM_ATTRIB_ORNO《/*NONE*/●#@PARAM_ATTRIB #FN_ONLY(flag)》
#@PARAM_ATTRIB_SAMP《/*NONE*/●const #FN_ONLY(flag)●in #FN_ONLY(flag)●lazy shared #FN_ONLY(flag)》

// We want the "Foo" case to be tested even in LITE mode as it's a very relevant case 
#@TYPE_REFS_X《#@TYPE_REFS●#?AST_STRUCTURE_EXPECTED!【Foo #TYPE_REF_CAN_PARSE_AS_ID(flag)● RefIdentifier】》

#@FN_ARG_BASE《
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS foo ●          FunctionParameter(#@TYPE_REFS DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo ... #FN_ONLY(flag)●FunctionParameter(RefIdentifier DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo = #@EXP_ASSIGN●    FunctionParameter(RefIdentifier DefSymbol #@EXP_ASSIGN)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS_X     #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP #@TYPE_REFS_X ... #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_ASSIGN__NO_POSSIBLE_REFS #FN_ONLY(flag)●NamelessParameter(RefIdentifier #@EXP_ASSIGN__NO_POSSIBLE_REFS)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_REFS    ●                         NamelessParameter(RefIdentifier #@EXP_REFS)】●
  ►#?AST_STRUCTURE_EXPECTED!【... #FN_ONLY(flag)● CStyleVarArgsParameter】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_MODIFIERS (long) #FN_ONLY(flag)●NamelessParameter(RefTypeModifier(RefPrimitive))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr ... #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr = 2 #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】●
  
  ►#?AST_STRUCTURE_EXPECTED!【const int foo ... #@BREAK_Pr = 2 #FN_ONLY(flag)●FunctionParameter(RefPrimitive DefSymbol) $】●
  ►#?AST_STRUCTURE_EXPECTED!【int[#error(EXP_CLOSE_BRACKET) #@BREAK_Pr ... ●NamelessParameter(RefTypeDynArray(*)) $】●
¤》
function/template interactions - param default convert
#@FN_ARG1《
  ►#@FN_ARG_BASE●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo=bar[] ●FunctionParameter(RefIdentifier DefSymbol ExpSlice(#@ExpIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo=bar* #@NO_EXP●FunctionParameter(RefIdentifier DefSymbol ExpInfix(#@ExpIdentifier #@NO_EXP))】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo=#@TYPE_AS_EXP /* Test that TYPE_AS_EXP doesn't cause parsing as TPL*/ ● 
                            FunctionParameter(RefIdentifier DefSymbol #@TYPE_AS_EXP)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       const(foo) func1( #FN_PARAMETER_TEST(test) #@FN_ARG1 ) #@SAMPLE_BLOCK_ST
#AST_STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_ARG1 #@SAMPLE_BLOCK_ST)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 2+ args
#@ARG_START《
  ►#?AST_STRUCTURE_EXPECTED!【const in char foo        ,●FunctionParameter(RefPrimitive DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【const in char            ,●NamelessParameter(RefPrimitive )】●
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_REF                 ,●NamelessParameter(#@NO_REF)】●
  ►#?AST_STRUCTURE_EXPECTED!【const Foo foo = #@NO_EXP ,●FunctionParameter(RefIdentifier DefSymbol #@NO_EXP)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAMS_SAMPLE , ● #@PARAMS_SAMPLE】●
  
  ►#?AST_STRUCTURE_EXPECTED!【const in char ... ,●NamelessParameter(RefPrimitive) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)      const(foo) func2( #@ARG_START #@PARAMS_SAMPLE #@FN_AFTER_PARAM
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@ARG_START #@PARAMS_SAMPLE #@FN_AFTER_PARAM )


▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Template 0 args
#PARSE(DECLARATION)  const(foo) tplFunc0() ( #@FN_AFTER_PARAM
#AST_STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_AFTER_PARAM )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       Foo tplFuncS( #@TPL_PARAMS_SAMPLE1 ) #error(EXP_OPEN_PARENS)【】 #@BREAK #@SAMPLE_BLOCK_ST
#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPL_PARAMS_SAMPLE1 #@BREAK #@SAMPLE_BLOCK_ST )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Template args

#@TYPE_REFS_ORNO_SAMPLE《#@TYPE_REFS__LITE●#@NO_REF》

#@EXP_COND_ORNO《#@EXP_COND●#@NO_EXP》
#@EXP_COND_ORNO_SAMPLE《#@EXP_COND__LITE●#@NO_EXP》

#@TYPE_OR_EXP《#@TYPE_REFS●#@EXP_COND__NO_POSSIBLE_REFS●#@NO_ROE》
#@TYPE_OR_EXP_SAMPLE《#@TYPE_REFS__LITE●#@EXP_COND__LITE●#@NO_ROE》


#@TPLARG_1《
  ►#?AST_STRUCTURE_EXPECTED!【foo●TemplateTypeParam(DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo : #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo = #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo : #@TYPE_REFS_ORNO_SAMPLE = #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO_SAMPLE #@TYPE_REFS_ORNO)】●

  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS foo●TemplateValueParam(#@TYPE_REFS DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo[ #error(EXP_CLOSE_BRACKET)●TemplateValueParam(RefTypeDynArray(?) DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO_SAMPLE = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO_SAMPLE #@EXP_COND_ORNO)】●

  ►#?AST_STRUCTURE_EXPECTED!【alias foo●TemplateAliasParam(DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo : #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo = #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo : #@TYPE_OR_EXP_SAMPLE = #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP_SAMPLE #@TYPE_OR_EXP)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【TUPLE ...●TemplateTupleParam(DefSymbol)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【this THIS●TemplateThisParam(DefSymbol)】●
¤》

#@TPLARGS《
  ►#@TPLARG_1●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_REF  , #@TPL_PARAMS_SAMPLE1● TemplateTypeParam(DefSymbol) #@TPL_PARAMS_SAMPLE1】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TPL_PARAMS_SAMPLE1, #@TPLARG_1● #@TPL_PARAMS_SAMPLE1 #@TPLARG_1】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TPLARG_1, #@TPL_PARAMS_SAMPLE1● #@TPLARG_1 #@TPL_PARAMS_SAMPLE1】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)       Foo tplFunc( #@TPLARGS ) ( #@PARAMS_SAMPLE ) #@SAMPLE_BLOCK_ST
#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@TPLARGS #@PARAMS_SAMPLE #@SAMPLE_BLOCK_ST )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function  Attributes
#@FN_ATTRIB《#@TYPE_MODIFIERS●nothrow●pure●@property●@safe●@trusted●@system●@disable● 
►@property @safe●nothrow @property pure @safe @disable》

#@NO_BODY《#error(EXPRULE_Body) #@BREAK》

#@FN_ATTRIBS_END《
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB ;                    ● #@EB】●
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB #@SAMPLE_BLOCK_ST    ● #@SAMPLE_BLOCK_ST】●
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB #error(EXPRULE_Body) #@BREAK● 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB body {} ● FunctionBody(*)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB in {} body {} ● InOutFunctionBody(* *)】●  
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB out(foo) {} body {} ● InOutFunctionBody(* *)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@FN_ATTRIB #@NO_BODY #@BREAK @invalidProperty  { }● 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_BODY #@BREAK ref               { }● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)      inout(Foo) funcAttrib( #@PARAMS_SAMPLE ) #@FN_ATTRIBS_END
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@PARAMS_SAMPLE #@FN_ATTRIBS_END )
Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Template constraint
#@NO_PARENSEXP《#?AST_STRUCTURE_EXPECTED!【#error(EXP_OPEN_PARENS)【】● MissingParenthesesExpression】》
#@FN_FROM_CONSTRAINT《
  ►#?AST_STRUCTURE_EXPECTED!【if #@NO_PARENSEXP #@SAMPLE_BLOCK_ST● #@NO_PARENSEXP #@SAMPLE_BLOCK_ST】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@NO_EXP) #@SAMPLE_BLOCK_ST  ● #@NO_EXP #@SAMPLE_BLOCK_ST 】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@EXP_ANY) #@SAMPLE_BLOCK_ST ● #@EXP_ANY #@SAMPLE_BLOCK_ST 】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@EXP_ANY__LITE #error(EXP_CLOSE_PARENS) #@BREAK { }● #@EXP_ANY__LITE】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       Foo funcTplConstraint( ) ( #@PARAMS_SAMPLE )  #@FN_FROM_CONSTRAINT
#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@PARAMS_SAMPLE #@FN_FROM_CONSTRAINT  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  /* needs tpl args to parse from if constraint */
#PARSE(DECLARATION)      Foo funcTplConstraint ( #@PARAMS_SAMPLE ) #@NO_BODY #@BREAK  #@FN_FROM_CONSTRAINT
#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@PARAMS_SAMPLE #@NO_BODY #@BREAK  )

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Body
#@BLK《                   ►#?AST_STRUCTURE_EXPECTED!【 { /*TODO statement*/ } ● BlockStatement 】● ¤》
#@NO_BLOCK《       ►#?AST_STRUCTURE_EXPECTED!【 #error(EXPRULE_Block) ● BlockStatement 】● ¤》
#@NO_PARENS《     ►#?AST_STRUCTURE_EXPECTED!【 #error(EXP_OPEN_PARENS)【】 #@BREAK● #@BREAK $ 】● ¤》

#@IN_BLOCKS《
 ►#?AST_STRUCTURE_EXPECTED!【 in #@BLK● #@BLK】●  
 ►#?AST_STRUCTURE_EXPECTED!【 in #@NO_BLOCK● #@NO_BLOCK 】●
¤》
#@OUT_BLOCKS《
 ►#?AST_STRUCTURE_EXPECTED!【 out(foo) #@BLK●      FunctionBodyOutBlock(Symbol #@BLK )】●  
 ►#?AST_STRUCTURE_EXPECTED!【 out(foo) #@NO_BLOCK● FunctionBodyOutBlock(Symbol #@NO_BLOCK )】●
 
 ►#?AST_STRUCTURE_EXPECTED!【 out(foo #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?AST_STRUCTURE_EXPECTED!【 out(#@NO_ID #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?AST_STRUCTURE_EXPECTED!【 out#@NO_PARENS #@BREAK● FunctionBodyOutBlock(   $ )】●
¤》

#@AFTER_PARAM《  /**/ ● nothrow 》
#@OUT_BLOCK《►#?AST_STRUCTURE_EXPECTED!【 out(foo) #@BLK●      FunctionBodyOutBlock(Symbol #@BLK )】●¤》
 
#@FN_BODY_END《
 ►#?AST_STRUCTURE_EXPECTED!【 #@NO_BODY         ● #@NO_BODY】●
 ►#?AST_STRUCTURE_EXPECTED!【 ;                 ● #@EB】●
 ►#?AST_STRUCTURE_EXPECTED!【 #@SAMPLE_BLOCK_ST ● #@SAMPLE_BLOCK_ST】●
 ►#?AST_STRUCTURE_EXPECTED!【 body #@BLK        ● FunctionBody( #@BLK )】●
 ►#?AST_STRUCTURE_EXPECTED!【 body #@NO_BLOCK /**/● FunctionBody( #@NO_BLOCK )】●
 
 ►#?AST_STRUCTURE_EXPECTED!【  #@IN_BLOCKS body #@BLK             ●InOutFunctionBody( #@IN_BLOCKS #@BLK )】●
 ►#?AST_STRUCTURE_EXPECTED!【  #@IN_BLOCKS #@OUT_BLOCKS body #@BLK●InOutFunctionBody( #@IN_BLOCKS #@OUT_BLOCKS #@BLK )】●
 ►#?AST_STRUCTURE_EXPECTED!【  #@OUT_BLOCKS body #@BLK            ●InOutFunctionBody( #@OUT_BLOCKS #@BLK )】●
 ►#?AST_STRUCTURE_EXPECTED!【  #@OUT_BLOCKS #@IN_BLOCKS body #@BLK●InOutFunctionBody( #@OUT_BLOCKS #@IN_BLOCKS #@BLK )】●
 
 ►#?AST_STRUCTURE_EXPECTED!【  in #@BLK   #@NO_BODY  #@BLK              ●InOutFunctionBody( #@BLK #@NO_BODY )】●
 ►#?AST_STRUCTURE_EXPECTED!【  in #@BLK   #@NO_BODY  in #@BLK body #@BLK●InOutFunctionBody( #@BLK #@NO_BODY )】●
 ►#?AST_STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@BLK            ●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
 ►#?AST_STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@OUT_BLOCK #@BLK●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       inout(xpto) funcBody( #@PARAMS_SAMPLE ) #@AFTER_PARAM #@FN_BODY_END
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@PARAMS_SAMPLE #@FN_BODY_END )

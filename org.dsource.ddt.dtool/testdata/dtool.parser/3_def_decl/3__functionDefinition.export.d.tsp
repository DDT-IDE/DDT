Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FN_PARAMS_SAMPLE《#?STRUCTURE_EXPECTED!【 .Bar.Baz baz, const Foo.Foobar foo = 2● 
  FunctionParameter(RefQualified(RefModuleQualified(?) RefIdentifier) DefSymbol)
  FunctionParameter(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)
】》
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Parameters
#@BREAK_Pr《 #?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK● $ 】  》

#@PARAM_ATTRIB《auto●#@TYPE_MODIFIERS●final●in●lazy●out●ref●scope●in lazy ref ●const inout●const lazy shared》
#@PARAM_ATTRIB_ORNO《/*NONE*/●#@PARAM_ATTRIB #FN_ONLY(flag)》
#@PARAM_ATTRIB_SAMP《/*NONE*/●const #FN_ONLY(flag)●in #FN_ONLY(flag)●lazy shared #FN_ONLY(flag)》

// We want the "Foo" case to be tested even in LITE mode as it's a very relevant case 
#@TYPE_REFS_X《#@TYPE_REFS__NO_BROKEN●#?STRUCTURE_EXPECTED!【Foo #TYPE_REF_CAN_PARSE_AS_ID(flag)● RefIdentifier】》

#@FN_PARAM_DISCARD《#?STRUCTURE_EXPECTED!【/*DISCARD*/●】》

#@FN_PARAM_BASE《
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS__NO_BROKEN foo ●FunctionParameter(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo ... #FN_ONLY(flag) ●FunctionParameter(RefIdentifier DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo = #@EXP_ASSIGN●    FunctionParameter(RefIdentifier DefSymbol #@EXP_ASSIGN)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS_X     #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP #@TYPE_REFS_X ... #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_ASSIGN__NO_POSSIBLE_REFS #FN_ONLY(flag)●NamelessParameter(RefIdentifier #@EXP_ASSIGN__NO_POSSIBLE_REFS)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_REFS    ●                         NamelessParameter(RefIdentifier #@EXP_REFS)】●
  ►#?STRUCTURE_EXPECTED!【... #FN_ONLY(flag)● CStyleVarArgsParameter】●
  
  ►#?STRUCTURE_EXPECTED!【#@TYPE_MODIFIERS (long) #FN_ONLY(flag)●NamelessParameter(RefTypeModifier(RefPrimitive))】●

 ►#?FN_PARAM__NO_BROKEN【#@FN_PARAM_DISCARD●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr ... #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】】●
 ►#?FN_PARAM__NO_BROKEN【#@FN_PARAM_DISCARD●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr = 2 #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】】●
  
 ►#?FN_PARAM__NO_BROKEN【#@FN_PARAM_DISCARD● 
  ►#?STRUCTURE_EXPECTED!【const int foo ... #@BREAK_Pr = 2 #FN_ONLY(flag)●FunctionParameter(RefPrimitive DefSymbol) $】】●
 ►#?FN_PARAM__NO_BROKEN【#@FN_PARAM_DISCARD●
  ►#?STRUCTURE_EXPECTED!【int[#error(EXP_CLOSE_BRACKET) #@BREAK_Pr ... ●NamelessParameter(RefTypeDynArray(*)) $】】●
¤》
function/template interactions - param default convert
#@FN_PARAM_SINGLE《
  ►#@FN_PARAM_BASE●
  ►#?STRUCTURE_EXPECTED!【Foo foo=bar[] ●FunctionParameter(RefIdentifier DefSymbol ExpSlice(#@ExpIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【Foo foo=bar* #@NO_EXP●FunctionParameter(RefIdentifier DefSymbol ExpInfix(#@ExpIdentifier #@NO_EXP))】●
  ►#?STRUCTURE_EXPECTED!【Foo foo=#@TYPE_AS_EXP /* Test that TYPE_AS_EXP doesn't cause parsing as TPL*/ ● 
                            FunctionParameter(RefIdentifier DefSymbol #@TYPE_AS_EXP)】●
¤》

// TODO VARIADIC MUST BE LAST ERROR
#@FN_PARAMS《
  ►#@FN_PARAM_SINGLE●

  ►#?STRUCTURE_EXPECTED!【#@FN_PARAMS_SAMPLE ,      #@FN_PARAMS_SAMPLE● #@FN_PARAMS_SAMPLE #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【#@NO_REF           , #@FN_PARAMS_SAMPLE●NamelessParameter(#@NO_REF) #@FN_PARAMS_SAMPLE】●

  ►#?STRUCTURE_EXPECTED!【const in char foo        #FN_ONLY(flag), #@FN_PARAMS_SAMPLE●FunctionParameter(RefPrimitive DefSymbol) #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【const in char            #FN_ONLY(flag), #@FN_PARAMS_SAMPLE●NamelessParameter(RefPrimitive ) #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【const Foo foo = #@NO_EXP #FN_ONLY(flag), #@FN_PARAMS_SAMPLE●FunctionParameter(RefIdentifier DefSymbol #@NO_EXP) #@FN_PARAMS_SAMPLE】●
  
  ►#?STRUCTURE_EXPECTED!【const in char ...        #FN_ONLY(flag), #@FN_PARAMS_SAMPLE●NamelessParameter(RefPrimitive) #@FN_PARAMS_SAMPLE】● 
¤》

#@FN_PARAMS__NO_BROKEN《#FN_PARAM__NO_BROKEN(flag)#@FN_PARAMS》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Function attributes
#@FN_ATTRIB《#?STRUCTURE_EXPECTED!【
►#@《#@TYPE_MODIFIERS●nothrow●pure●
  ►@property●@safe●@trusted●@system●@disable● 
  ►@property @safe●nothrow @property pure @safe @disable》
● 】》

#@FN_ATTRIB__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【 /*no fn attrib*/ ● 】●  
 ►#?STRUCTURE_EXPECTED!【 nothrow ● 】●  
 ►#?STRUCTURE_EXPECTED!【 @safe const● 】●
¤》

Ⓗ▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃ Function Body

#@NO_BODY《#error(EXPRULE_Body) #@BREAK》
#@EMPTY_BODY《#?STRUCTURE_EXPECTED!【; ● EmptyBodyStatement 】》

#@IN_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【in #@BLK_ST● #@BLK_ST】●  
 ►#?STRUCTURE_EXPECTED!【in #@NO_BLOCK_ST● #@NO_BLOCK_ST 】●
¤》
#@OUT_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【out(foo) #@BLK_ST●      FunctionBodyOutBlock(Symbol #@BLK_ST )】●  
 ►#?STRUCTURE_EXPECTED!【out(foo) #@NO_BLOCK_ST● FunctionBodyOutBlock(Symbol #@NO_BLOCK_ST )】●
 
 ►#?STRUCTURE_EXPECTED!【out(foo #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【out(#@NO_ID #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【out #error(EXP_OPEN_PARENS)【】 #@BREAK● FunctionBodyOutBlock(   $ )】●
¤》

#@OUT_BLOCK《►#?STRUCTURE_EXPECTED!【 out(foo) #@BLK_ST●      FunctionBodyOutBlock(Symbol #@BLK_ST )】●¤》

#@FNDEF_BODY《
 ►#@NO_BODY●
 ►#@EMPTY_BODY●
 ►#@FN_BODY●
¤》

#@FN_BLOCK_BODY《
 ►#@SAMPLE_BLOCK_ST●
 ►#@BROKEN_BLOCK_ST●
¤》
#@FN_BLOCK_BODY__SAMPLE《#@SAMPLE_BLOCK_ST》

#@FN_BODY《
 ►#@SAMPLE_BLOCK_ST●
 ►#@BROKEN_BLOCK_ST●
 
 ►#?STRUCTURE_EXPECTED!【{ #error(EXP_CLOSE_BRACE) #@BREAK ● BlockStatement $】●
 ►#?STRUCTURE_EXPECTED!【body #@BLK_ST        ● FunctionBody( #@BLK_ST )】●
 ►#?STRUCTURE_EXPECTED!【body #@NO_BLOCK_ST /**/● FunctionBody( #@NO_BLOCK_ST )】●
 
 ►#?STRUCTURE_EXPECTED!【#@IN_BLOCKS body #@BLK_ST             ●InOutFunctionBody( #@IN_BLOCKS #@BLK_ST )】●
 ►#?STRUCTURE_EXPECTED!【#@IN_BLOCKS #@OUT_BLOCKS body #@BLK_ST●InOutFunctionBody( #@IN_BLOCKS #@OUT_BLOCKS #@BLK_ST )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCKS body #@BLK_ST            ●InOutFunctionBody( #@OUT_BLOCKS #@BLK_ST )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCKS #@IN_BLOCKS body #@BLK_ST●InOutFunctionBody( #@OUT_BLOCKS #@IN_BLOCKS #@BLK_ST )】●
 
 ►#?STRUCTURE_EXPECTED!【in #@BLK_ST   #@NO_BODY  #@BLK_ST                 ●InOutFunctionBody( #@BLK_ST #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【in #@BLK_ST   #@NO_BODY  in #@BLK_ST body #@BLK_ST●InOutFunctionBody( #@BLK_ST #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@BLK_ST            ●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCK #@NO_BODY  #@OUT_BLOCK #@BLK_ST●InOutFunctionBody( #@OUT_BLOCK #@NO_BODY )】●
¤》

#@FN_BODY__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【  #@IN_BLOCKS body #@BLK_ST             ●InOutFunctionBody( #@IN_BLOCKS #@BLK_ST )】●
¤》
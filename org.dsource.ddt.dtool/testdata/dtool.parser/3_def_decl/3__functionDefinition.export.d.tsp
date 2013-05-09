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
#@TYPE_REFS_X《#@TYPE_REFS__NO_BROKEN●#?STRUCTURE_EXPECTED!【Foo #TYPEREF_CanParseAsId(flag)● RefIdentifier】》

#@FN_PARAM_BASE《
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS__NO_BROKEN foo ●FunctionParameter(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo ... #FN_ONLY(flag) ●FunctionParameter(RefIdentifier DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo = #@EXP_ASSIGN●    FunctionParameter(RefIdentifier DefSymbol #@EXP_ASSIGN)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS_X     #?TYPEREF_CanParseAsId!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP #@TYPE_REFS_X ... #?TYPEREF_CanParseAsId!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_ASSIGN__NO_POSSIBLE_REFS #FN_ONLY(flag)●NamelessParameter(RefIdentifier #@EXP_ASSIGN__NO_POSSIBLE_REFS)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_REFS    ●                         NamelessParameter(RefIdentifier #@EXP_REFS)】●
  ►#?STRUCTURE_EXPECTED!【... #FN_ONLY(flag)● CStyleVarArgsParameter】●
  
  ►#?STRUCTURE_EXPECTED!【#@TYPE_MODIFIERS (long) #FN_ONLY(flag)●NamelessParameter(RefTypeModifier(RefPrimitive))】●

 ►#?FN_PARAM__NO_BROKEN【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_TYPEREF #@BREAK_Pr ... #FN_ONLY(flag)●NamelessParameter(#@NO_TYPEREF) $】】●
 ►#?FN_PARAM__NO_BROKEN【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_TYPEREF #@BREAK_Pr = 2 #FN_ONLY(flag)●NamelessParameter(#@NO_TYPEREF) $】】●
  
 ►#?FN_PARAM__NO_BROKEN【#:DISCARD_CASE● 
  ►#?STRUCTURE_EXPECTED!【const int foo ... #@BREAK_Pr = 2 #FN_ONLY(flag)●FunctionParameter(RefPrimitive DefSymbol) $】】●
 ►#?FN_PARAM__NO_BROKEN【#:DISCARD_CASE●
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
  ►#?STRUCTURE_EXPECTED!【#@NO_TYPEREF           , #@FN_PARAMS_SAMPLE●NamelessParameter(#@NO_TYPEREF) #@FN_PARAMS_SAMPLE】●

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

#@NO_BODY《#error(EXPRULE_FnBody) #@BREAK》
#@EMPTY_BODY《#?STRUCTURE_EXPECTED!【; ● EmptyStatement 】》

#@Sp_ST_BLOCK《#@ST_BLOCK__SAMPLE》

#@IN_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【in #@Sp_ST_BLOCK● #@Sp_ST_BLOCK】●  
 ►#?STRUCTURE_EXPECTED!【in #@NO_ST_BLOCK● #@NO_ST_BLOCK 】●
¤》
#@OUT_BLOCKS《
 ►#?STRUCTURE_EXPECTED!【out(foo) #@Sp_ST_BLOCK● FunctionBodyOutBlock(Symbol #@Sp_ST_BLOCK )】●  
 ►#?STRUCTURE_EXPECTED!【out(foo) #@NO_ST_BLOCK● FunctionBodyOutBlock(Symbol #@NO_ST_BLOCK )】●
 
 ►#?STRUCTURE_EXPECTED!【out(foo #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【out(#@NO_ID #@BREAK_Pr● FunctionBodyOutBlock(Symbol $ )】●
 ►#?STRUCTURE_EXPECTED!【out #error(EXP_OPEN_PARENS)【】 #@BREAK● FunctionBodyOutBlock(   $ )】●
¤》

#@FNDEF_BODY《
 ►#@NO_BODY●
 ►#@EMPTY_BODY●
 ►#@FN_BODY●
¤》

#@FN_BODY《
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
 
 ►#?STRUCTURE_EXPECTED!【body #@Sp_ST_BLOCK     ● FunctionBody( #@Sp_ST_BLOCK )】●
 ►#?STRUCTURE_EXPECTED!【body #@NO_ST_BLOCK /**/● FunctionBody( #@NO_ST_BLOCK )】●
 
 ►#?STRUCTURE_EXPECTED!【#@IN_BLOCKS body #@Sp_ST_BLOCK             ●InOutFunctionBody( #@IN_BLOCKS #@Sp_ST_BLOCK )】●
 ►#?STRUCTURE_EXPECTED!【#@IN_BLOCKS #@OUT_BLOCKS body #@Sp_ST_BLOCK●InOutFunctionBody( #@IN_BLOCKS #@OUT_BLOCKS #@Sp_ST_BLOCK )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCKS body #@Sp_ST_BLOCK            ●InOutFunctionBody( #@OUT_BLOCKS #@Sp_ST_BLOCK )】●
 ►#?STRUCTURE_EXPECTED!【#@OUT_BLOCKS #@IN_BLOCKS body #@Sp_ST_BLOCK●InOutFunctionBody( #@OUT_BLOCKS #@IN_BLOCKS #@Sp_ST_BLOCK )】●
 
 ►#?STRUCTURE_EXPECTED!【in     #@Sp_ST_BLOCK #@NO_BODY     #@Sp_ST_BLOCK  ●InOutFunctionBody( #@Sp_ST_BLOCK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【in     #@Sp_ST_BLOCK #@NO_BODY  in #@Sp_ST_BLOCK  ●InOutFunctionBody( #@Sp_ST_BLOCK #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【out(f) #@Sp_ST_BLOCK #@NO_BODY         #@Sp_ST_BLOCK ●InOutFunctionBody( FunctionBodyOutBlock(Symbol #@Sp_ST_BLOCK) #@NO_BODY )】●
 ►#?STRUCTURE_EXPECTED!【out(f) #@Sp_ST_BLOCK #@NO_BODY  out(f) #@Sp_ST_BLOCK ●InOutFunctionBody( FunctionBodyOutBlock(Symbol #@Sp_ST_BLOCK) #@NO_BODY )】●
¤》
#@FN_BODY__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【  #@IN_BLOCKS body #@Sp_ST_BLOCK             ●InOutFunctionBody( #@IN_BLOCKS #@Sp_ST_BLOCK )】●
¤》

#@FN_BODY__SIMPLE_TESTER《
 ►#@FN_BODY__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
 ►#@NO_BODY●
¤》
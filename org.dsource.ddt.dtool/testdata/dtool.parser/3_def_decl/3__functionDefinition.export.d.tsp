Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FN_PARAMS_SAMPLE《#?STRUCTURE_EXPECTED!【 .Bar.Baz baz, const Foo.Foobar foo = 2● 
  FunctionParameter(RefQualified(RefModuleQualified(?) RefIdentifier) DefSymbol)
  FunctionParameter(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)
】》
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  1 param
#@BREAK_Pr《 #?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS) #@BREAK● $ 】  》

#@PARAM_ATTRIB《auto●#@TYPE_MODIFIERS●final●in●lazy●out●ref●scope●in lazy ref ●const inout●const lazy shared》
#@PARAM_ATTRIB_ORNO《/*NONE*/●#@PARAM_ATTRIB #FN_ONLY(flag)》
#@PARAM_ATTRIB_SAMP《/*NONE*/●const #FN_ONLY(flag)●in #FN_ONLY(flag)●lazy shared #FN_ONLY(flag)》

// We want the "Foo" case to be tested even in LITE mode as it's a very relevant case 
#@TYPE_REFS_X《#@TYPE_REFS__NO_BROKEN●#?STRUCTURE_EXPECTED!【Foo #TYPE_REF_CAN_PARSE_AS_ID(flag)● RefIdentifier】》

#@FN_PARAM_BASE《
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS__NO_BROKEN foo ●          FunctionParameter(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo ... #FN_ONLY(flag)●FunctionParameter(RefIdentifier DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo foo = #@EXP_ASSIGN●    FunctionParameter(RefIdentifier DefSymbol #@EXP_ASSIGN)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_ORNO #@TYPE_REFS_X     #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP #@TYPE_REFS_X ... #?TYPE_REF_CAN_PARSE_AS_ID!【#FN_ONLY(flag)】●NamelessParameter(#@TYPE_REFS_X)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_ASSIGN__NO_POSSIBLE_REFS #FN_ONLY(flag)●NamelessParameter(RefIdentifier #@EXP_ASSIGN__NO_POSSIBLE_REFS)】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_SAMP Foo = #@EXP_REFS    ●                         NamelessParameter(RefIdentifier #@EXP_REFS)】●
  ►#?STRUCTURE_EXPECTED!【... #FN_ONLY(flag)● CStyleVarArgsParameter】●
  
  ►#?STRUCTURE_EXPECTED!【#@TYPE_MODIFIERS (long) #FN_ONLY(flag)●NamelessParameter(RefTypeModifier(RefPrimitive))】●
  
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr ... #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】●
  ►#?STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@NO_REF #@BREAK_Pr = 2 #FN_ONLY(flag)●NamelessParameter(#@NO_REF) $】●
  
  ►#?STRUCTURE_EXPECTED!【const int foo ... #@BREAK_Pr = 2 #FN_ONLY(flag)●FunctionParameter(RefPrimitive DefSymbol) $】●
  ►#?STRUCTURE_EXPECTED!【int[#error(EXP_CLOSE_BRACKET) #@BREAK_Pr ... ●NamelessParameter(RefTypeDynArray(*)) $】●
¤》
function/template interactions - param default convert
#@FN_PARAM_SINGLE《
  ►#@FN_PARAM_BASE●
  ►#?STRUCTURE_EXPECTED!【Foo foo=bar[] ●FunctionParameter(RefIdentifier DefSymbol ExpSlice(#@ExpIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【Foo foo=bar* #@NO_EXP●FunctionParameter(RefIdentifier DefSymbol ExpInfix(#@ExpIdentifier #@NO_EXP))】●
  ►#?STRUCTURE_EXPECTED!【Foo foo=#@TYPE_AS_EXP /* Test that TYPE_AS_EXP doesn't cause parsing as TPL*/ ● 
                            FunctionParameter(RefIdentifier DefSymbol #@TYPE_AS_EXP)】●
¤》
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  2+ params
// TODO VARIADIC MUST BE LAST ERROR
#@FN_PARAMS《
  ►#?STRUCTURE_EXPECTED!【#@FN_PARAMS_SAMPLE ,      #@FN_PARAMS_SAMPLE● #@FN_PARAMS_SAMPLE #@FN_PARAMS_SAMPLE】●
  
  ►#?STRUCTURE_EXPECTED!【const in char foo       , #@FN_PARAMS_SAMPLE●FunctionParameter(RefPrimitive DefSymbol) #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【const in char           , #@FN_PARAMS_SAMPLE●NamelessParameter(RefPrimitive ) #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【#@NO_REF                , #@FN_PARAMS_SAMPLE●NamelessParameter(#@NO_REF) #@FN_PARAMS_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【const Foo foo = #@NO_EXP, #@FN_PARAMS_SAMPLE●FunctionParameter(RefIdentifier DefSymbol #@NO_EXP) #@FN_PARAMS_SAMPLE】●
  
  ►#?STRUCTURE_EXPECTED!【const in char ...       , #@FN_PARAMS_SAMPLE●NamelessParameter(RefPrimitive) #@FN_PARAMS_SAMPLE】● 
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Function attributes
#@FN_ATTRIB《#@TYPE_MODIFIERS●nothrow●pure●@property●@safe●@trusted●@system●@disable● 
►@property @safe●nothrow @property pure @safe @disable》
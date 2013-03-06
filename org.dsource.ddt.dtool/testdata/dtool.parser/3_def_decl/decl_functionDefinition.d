▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
Foo foo();
int xx(Foo foo, .Bar.Baz baz);
int xx(...);

#AST_STRUCTURE_EXPECTED:
DefFunction(RefIdentifier DefSymbol #@EB)
DefFunction(? DefSymbol FunctionParameter(RefIdentifier DefSymbol) FunctionParameter(RefQualified(* *) DefSymbol) #@EB)
DefFunction(RefPrimitive DefSymbol CStyleVarArgsParameter #@EB)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PARAMS_SAMPLE《
  ►#?AST_STRUCTURE_EXPECTED!【 .Bar.Baz baz, const Foo.Foobar foo = 2, auto Foo foo● 
  FunctionParameter(RefQualified(RefModuleQualified(?) RefIdentifier) DefSymbol)
  FunctionParameter(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)
  FunctionParameter(RefIdentifier DefSymbol)
】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS foo(#@PARAMS_SAMPLE);

#AST_STRUCTURE_EXPECTED:
DefFunction(#@TYPE_REFS DefSymbol #@PARAMS_SAMPLE #@EB)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 0 args

#@FN_SAMPLE_AFTER_PARAM《
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS)●】●
  ►#?AST_STRUCTURE_EXPECTED!【 ) ;                    ● #@EB】●
  ►#?AST_STRUCTURE_EXPECTED!【 ) {}                   ● BlockStatement】●
  ►#?AST_STRUCTURE_EXPECTED!【 ) #error(EXPRULE_Body)● 】●
¤》
const(foo) foo( #@FN_SAMPLE_AFTER_PARAM

#AST_STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@FN_SAMPLE_AFTER_PARAM )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 1 args

#@PARAM_ATTRIB_X《auto●#@TYPE_MODIFIERS●final●in●lazy●out●ref●scope●in lazy ref ●const inout●const lazy shared》
#@PARAM_ATTRIB《/*NONE*/●#@PARAM_ATTRIB_X》
#@BREAK《#parser(IgnoreRest)》
#@BREAK_Pr《#error(EXP_CLOSE_PARENS) #parser(IgnoreRest)》

#@ARG_LAST《
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@TYPE_REFS foo ●FunctionParameter(#@TYPE_REFS DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB #@TYPE_REFS     ●NamelessParameter(#@TYPE_REFS )】●
  ►#?AST_STRUCTURE_EXPECTED!【immutable Bar.foobar3 foo ...  ●FunctionParameter(RefQualified(* *) DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【out inout   #@TYPE_REFS ...    ●NamelessParameter(#@TYPE_REFS )】●
  ►#?AST_STRUCTURE_EXPECTED!【final Foo foo = #@EXP_ASSIGN   ●FunctionParameter(RefIdentifier DefSymbol #@EXP_ASSIGN)】●
  ►#?AST_STRUCTURE_EXPECTED!【final Foo = #@EXP_ASSIGN ●NamelessParameter(RefIdentifier           #@EXP_ASSIGN)】●
  ►#?AST_STRUCTURE_EXPECTED!【...●  CStyleVarArgsParameter】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_MODIFIERS (long)  ●NamelessParameter(RefTypeModifier(RefPrimitive))】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_X #@NO_REF #@BREAK_Pr ...●NamelessParameter(#@NO_REF) $】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAM_ATTRIB_X #@NO_REF #@BREAK_Pr = 2●NamelessParameter(#@NO_REF) $】●
  
  ►#?AST_STRUCTURE_EXPECTED!【const int foo ... #@BREAK_Pr = 2 ●FunctionParameter(RefPrimitive DefSymbol) $】●
  ►#?AST_STRUCTURE_EXPECTED!【int[#error(EXP_CLOSE_BRACKET) #@BREAK_Pr ... ●NamelessParameter(RefTypeDynArray(*)) $】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       const(foo) foo( #@ARG_LAST #@FN_SAMPLE_AFTER_PARAM
#AST_STRUCTURE_EXPECTED:
DefFunction(RefTypeModifier(RefIdentifier) DefSymbol #@ARG_LAST #@FN_SAMPLE_AFTER_PARAM)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 2+ args
#@ARG_START《
  ►#?AST_STRUCTURE_EXPECTED!【const in char foo        ,●FunctionParameter(RefPrimitive DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【const in char            ,●NamelessParameter(RefPrimitive )】●
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_REF                 ,●NamelessParameter(#@NO_REF)】●
  ►#?AST_STRUCTURE_EXPECTED!【const Foo foo = #@NO_EXP ,●FunctionParameter(RefIdentifier DefSymbol #@NO_EXP)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PARAMS_SAMPLE , ● #@PARAMS_SAMPLE】●
  
  ►#?AST_STRUCTURE_EXPECTED!【const in char ... #@BREAK_Pr ,●NamelessParameter(RefPrimitive) $】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       const(foo) foo( #@ARG_START #@PARAMS_SAMPLE #@FN_SAMPLE_AFTER_PARAM
#AST_STRUCTURE_EXPECTED:
DefFunction( RefTypeModifier(?) DefSymbol #@ARG_START #@PARAMS_SAMPLE #@FN_SAMPLE_AFTER_PARAM )


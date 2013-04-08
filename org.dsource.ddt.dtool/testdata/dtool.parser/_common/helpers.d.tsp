Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ExpIdentifier【ExpReference(RefIdentifier)】
  #@NO_EXP《#?AST_STRUCTURE_EXPECTED!【/*MISSING:Exp*/ #error(EXPRULE_exp)●MissingExpression】》
 #@NO_EXPx《#?AST_STRUCTURE_EXPECTED!【/*MISSING:Exp*/ #error(EXPRULE_exp)●】》 //DEPRECATED TODO, should be NO_EXP
#@NULL_EXP《#?AST_STRUCTURE_EXPECTED!【/*No exp here*/ ● 】》

#@NO_ID《#?AST_STRUCTURE_EXPECTED!【/*MISSING:ID*/ #error(EXP_ID)●RefIdentifier】》
#@NO_DEFID《#?AST_STRUCTURE_EXPECTED!【/*MISSING:DefID*/ #error(EXP_ID)●DefSymbol】》

#@NO_ROE《#?AST_STRUCTURE_EXPECTED!【/*MISSING:RoE*/ #error(EXPRULE_RoE)● MissingExpression】》
#@NO_REF《#?AST_STRUCTURE_EXPECTED!【/*MISSING:Ref*/ #error(EXPRULE_ref)● RefIdentifier】》
#@NO_INIT《#?AST_STRUCTURE_EXPECTED!【/*MISSING:Init*/ #error(EXPRULE_Initializer)● InitializerExp(MissingExpression)】》

#@TYPE_REFS_ORNO《#@TYPE_REFS●#@NO_REF》

#@PaCLOSE_OR_NO【)●#error(EXP_CLOSE_PARENS) /*No close parens */ 】
#@BkCLOSE_OR_NO【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ 】
#@BrCLOSE_OR_NO【}●#error(EXP_CLOSE_BRACE) /*No close brace */ 】

#@PaCLOSE_OR_BREAK【)●#error(EXP_CLOSE_PARENS) /*No close parens */ #@BREAK】
#@BkCLOSE_OR_BREAK【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ #@BREAK】
#@BrCLOSE_OR_BREAK【}●#error(EXP_CLOSE_BRACE) /*No close brace */ #@BREAK】


#@BREAK《 #?AST_STRUCTURE_EXPECTED!【#parser(IgnoreRest) #RULE_BROKEN(test)● $ 】  》
#@PARSE_RULE_END《 #?AST_STRUCTURE_EXPECTED!【#parser(IgnoreRest) ● $ 】  》

#@SEMICOLON_OR_NO【;●#error(EXP_SEMICOLON)】
#@INVALID_DECL__NO_SEMICOLON【】

#@TYPE_MODIFIERS《const●immutable●shared●inout》

#@EB《#?AST_STRUCTURE_EXPECTED!【; ● EmptyBodyStatement 】》

#@ARG_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_EXP , ● #@NO_EXP】● 
  ►#?AST_STRUCTURE_EXPECTED!【/* NO EXTRA ARG */ ● 】●
¤》

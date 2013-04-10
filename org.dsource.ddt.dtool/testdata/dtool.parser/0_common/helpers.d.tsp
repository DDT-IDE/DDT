Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ExpIdentifier【ExpReference(RefIdentifier)】
  #@NO_EXP《#?STRUCTURE_EXPECTED!【/*MISSING:Exp*/ #error(EXPRULE_exp)●MissingExpression】》
 #@NO_EXPx《#?STRUCTURE_EXPECTED!【/*MISSING:Exp*/ #error(EXPRULE_exp)●】》 //DEPRECATED TODO, should be NO_EXP
#@NULL_EXP《#?STRUCTURE_EXPECTED!【/*No exp here*/ ● 】》

#@NO_ID《#?STRUCTURE_EXPECTED!【/*MISSING:ID*/ #error(EXP_ID)●RefIdentifier】》
#@NO_DEFID《#?STRUCTURE_EXPECTED!【/*MISSING:DefID*/ #error(EXP_ID)●DefSymbol】》

#@NO_ROE《#?STRUCTURE_EXPECTED!【/*MISSING:RoE*/ #error(EXPRULE_RoE)● MissingExpression】》
#@NO_REF《#?STRUCTURE_EXPECTED!【/*MISSING:Ref*/ #error(EXPRULE_ref)● RefIdentifier】》
#@NO_INIT《#?STRUCTURE_EXPECTED!【/*MISSING:Init*/ #error(EXPRULE_Initializer)● InitializerExp(MissingExpression)】》

#@PaCLOSE_OR_NO【)●#error(EXP_CLOSE_PARENS) /*No close parens */ 】
#@BkCLOSE_OR_NO【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ 】
#@BrCLOSE_OR_NO【}●#error(EXP_CLOSE_BRACE) /*No close brace */ 】

#@PaCLOSE_OR_BREAK【)●#error(EXP_CLOSE_PARENS) /*No close parens */ #@BREAK】
#@BkCLOSE_OR_BREAK【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ #@BREAK】
#@BRACKET_CLOSE_OR_BREAK【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ #@BREAK】
#@BrCLOSE_OR_BREAK【}●#error(EXP_CLOSE_BRACE) /*No close brace */ #@BREAK】


#@BREAK《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) #RULE_BROKEN(test)● $ 】  》
#@IGNORE_REST《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) ● $ 】  》

#@SEMICOLON_OR_NO【;●#error(EXP_SEMICOLON)】
#@INVALID_DECL__NO_SEMICOLON【】

#@TYPE_MODIFIERS《const●immutable●shared●inout》

#@EB《#?STRUCTURE_EXPECTED!【; ● EmptyBodyStatement 】》


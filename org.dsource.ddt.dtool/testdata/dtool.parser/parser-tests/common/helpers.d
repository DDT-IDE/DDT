Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ExpIdentifier【ExpReference(RefIdentifier)】
  #@NO_EXP《#?AST_STRUCTURE_EXPECTED!【/*MISSING_EXP*/ #error(EXPRULE_exp)●MissingExpression】》
#@NULL_EXP《#?AST_STRUCTURE_EXPECTED!【/*MISSING_EXP*/ #error(EXPRULE_exp)● 】》


#@NO_ROE《#?AST_STRUCTURE_EXPECTED!【/*MISSING_RoE*/ #error(EXPRULE_RoE)●MissingExpression】》

#@MISSING_REF《#?AST_STRUCTURE_EXPECTED!【/*MISSING_REF*/ #error(EXPRULE_ref)● RefIdentifier】》

#@PaCLOSE_OR_NO【)●#error(EXP_CLOSE_PARENS) /*No close parens */ 】
#@BkCLOSE_OR_NO【]●#error(EXP_CLOSE_BRACKET) /*No close sq bracket */ 】
#@BrCLOSE_OR_NO【}●#error(EXP_CLOSE_BRACE) /*No close brace */ 】

#@ARG_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_EXP , ● #@NO_EXP】● 
  ►#?AST_STRUCTURE_EXPECTED!【/* NO EXTRA ARG */ ● 】●
¤》
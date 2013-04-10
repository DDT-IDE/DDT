▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
const(blah) dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeModifier(RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#NO_BROKEN_PARENS(flag) #@TYPE_REFS●
  ►#@NO_REF●
¤》
#@ARG_CONTENT_CLEAN《
  ►#@TYPE_REFS● 
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_MODIFIERS•( #@ARG_CONTENT_OR_NO ) dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeModifier(#@ARG_CONTENT_OR_NO) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_MODIFIERS•( int ).foo dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefQualified( RefTypeModifier( RefPrimitive ) RefIdentifier ) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_MODIFIERS ( #@ARG_CONTENT_CLEAN #error:EXP_CLOSE_PARENS   declBroken #error:EXP_ID ;
#STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeModifier(#@ARG_CONTENT_CLEAN))     InvalidDeclaration(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_MODIFIERS ( #@NO_REF #error:EXP_CLOSE_PARENS
#STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeModifier(#@NO_REF) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_MODIFIERS #error:EXPRULE_decl
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib()

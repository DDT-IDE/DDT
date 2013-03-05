▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
const(blah) dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeModifier(RefIdentifier) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#@TYPE_REFS●
  ►#@NO_REF●
¤》
#@ARG_CONTENT_CLEAN《
  ►#@TYPE_REFS● 
¤》
#@BASE《const●immutable●shared●inout》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@BASE•( #@ARG_CONTENT_OR_NO ) dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeModifier(#@ARG_CONTENT_OR_NO) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@BASE•( int ).foo dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified( RefTypeModifier( RefPrimitive ) RefIdentifier ) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@BASE ( #@ARG_CONTENT_CLEAN #error:EXP_CLOSE_PARENS   declBroken #error:EXP_ID ;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeModifier(#@ARG_CONTENT_CLEAN))     InvalidDeclaration(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@BASE ( #@NO_REF #error:EXP_CLOSE_PARENS
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeModifier(#@NO_REF) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@BASE #error:EXPRULE_decl
#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib()
#AST_SOURCE_EXPECTED:
#@BASE

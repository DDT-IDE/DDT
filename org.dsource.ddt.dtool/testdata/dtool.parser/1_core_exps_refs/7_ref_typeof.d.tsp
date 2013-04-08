▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof(blah) dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeof(#@ExpIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof(return) dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeof(ExpRefReturn) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#@EXP_ANY●
  ►#@NO_EXP●
  ►#?AST_STRUCTURE_EXPECTED!【 #error(TYPE_AS_EXP_VALUE)《typeof(foo)》●ExpReference(RefTypeof(*))】●
  ►#?AST_STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》
#@ARG_CONTENT_CLEAN《
  ►#@EXP_NO_PENDING● 
  ►#?AST_STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( #@ARG_CONTENT_OR_NO ) dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeof(#@ARG_CONTENT_OR_NO) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( 123 ).foo dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified( RefTypeof( Integer ) RefIdentifier ) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof ( #@ARG_CONTENT_CLEAN #error:EXP_CLOSE_PARENS   declBroken #error:EXP_ID ;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeof(#@ARG_CONTENT_CLEAN))     InvalidDeclaration(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( #@NO_EXP #error:EXP_CLOSE_PARENS
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeof(#@NO_EXP) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof #error(EXP_OPEN_PARENS)【】
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeof)

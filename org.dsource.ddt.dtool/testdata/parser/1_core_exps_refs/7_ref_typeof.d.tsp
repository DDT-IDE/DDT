▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof(blah) dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeof(#@ExpIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof(return) dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeof(ExpRefReturn) DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#@EXP_ANY●
  ►#?STRUCTURE_EXPECTED!【 typeof(foo)●ExpReference(RefTypeof(*))】●
  ►#?STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》
  TODO:
  ►#?STRUCTURE_EXPECTED!【 #error(TYPE_AS_EXP_VALUE)《typeof(foo)》●ExpReference(RefTypeof(*))】●
#@ARG_CONTENT_CLEAN《
  ►#@EXP_NO_PENDING● 
  ►#?STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( #@ARG_CONTENT_OR_NO ) dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeof(#@ARG_CONTENT_OR_NO) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( 123 ).foo dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefQualified( RefTypeof( Integer ) RefIdentifier ) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof ( #@ARG_CONTENT_CLEAN #error:EXP_CLOSE_PARENS  #error(-none-)¤【;】  declBroken #error:EXP_ID ;
#STRUCTURE_EXPECTED:
IncompleteDeclaration(RefTypeof(#@ARG_CONTENT_CLEAN))     IncompleteDeclaration(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof( #@NO_EXP #error:EXP_CLOSE_PARENS #error(-none-)¤【;】
#STRUCTURE_EXPECTED:
IncompleteDeclaration(RefTypeof(#@NO_EXP) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
typeof #error(EXP_OPEN_PARENS)【】 #error(-none-)¤【;】
#STRUCTURE_EXPECTED:
IncompleteDeclaration(RefTypeof)

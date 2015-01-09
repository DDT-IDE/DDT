▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     typeof(blah)
#STRUCTURE_EXPECTED:  RefTypeof(#@ExpIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     typeof(return)
#STRUCTURE_EXPECTED:  RefTypeof(ExpRefReturn)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#@EXP_ANY●
  ►#?STRUCTURE_EXPECTED!【 typeof(foo)●ExpReference(RefTypeof(*))】●
  ►#?STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》

#@ARG_CONTENT_NO_PENDING《
  ►#@EXP_NO_PENDING● 
  ►#?STRUCTURE_EXPECTED!【 return ● ExpRefReturn】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    typeof   (#@ARG_CONTENT_OR_NO)
#STRUCTURE_EXPECTED: RefTypeof(#@ARG_CONTENT_OR_NO)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    typeof( 123 ).foo
#STRUCTURE_EXPECTED: RefQualified( RefTypeof( Integer ) RefIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    typeof   (#@ARG_CONTENT_NO_PENDING #@PARENS_BREAK  
#STRUCTURE_EXPECTED: RefTypeof(#@ARG_CONTENT_NO_PENDING)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    typeof( #@NO_EXP #@PARENS_BREAK  
#STRUCTURE_EXPECTED: RefTypeof(#@NO_EXP)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    typeof #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: RefTypeof

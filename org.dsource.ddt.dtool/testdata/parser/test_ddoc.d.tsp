Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DDOC_SAMPLE《/+NotDDOC+/#DDOC_TEST(test)【/++ StartDDOC +/】》

#@DDOC《 ●/++/●/**/#DDOC_TEST(test)【/** StartDDOC */】  》
#@DDOCPOST《 ●#?RULE_BROKEN【/// Post DDoc NOT included ||
/*-*/● #DDOC_TEST(test)【/// Post DDOC included |||
】/*-*/】》


#@DEFUNITS_NB《
 ►int * defunit ; ●
 ►int* defunit(int foo) ; ●
 ►this(int foo) ; #DDOC_TEST_TARGET__OVERRIDE(test)¤【this】 ●
 
 ►struct defunit { }●
 ►union defunit ;●
 ►class defunit { } ●
 ►interface defunit(int foo) ;●
 
 ►enum defunit {A, B, C} ●
 ►template defunit(T) { } ●
 ►mixin tpl!(foo) defunit; ●
 
 ►alias int defunit;●
 ►alias int defunit(int foo);●
¤》

#@DEFUNITS《
 ►#@DEFUNITS_NB●
 ►int * defunit #@SEMICOLON_BREAK ●
 ►int* defunit(int foo) #error(EXPRULE_FnBody) #@BREAK ●
 ►int* defunit(int #@PARENS_BREAK ●

 ►struct defunit { #@BRACE_BREAK●
 ►alias int defunit(int foo #error(EXP_CLOSE_PARENS) #@SEMICOLON_BREAK●
¤》

TODO DefVarFragment, DefinitionAliasFragment, AliasVarDeclFragment

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)
#PARSE(DECLARATION)   #@DDOC #@DEFUNITS #@DDOCPOST
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)
#PARSE(DECLARATION)   #@DDOC #@^DDOC #@DEFUNITS
/// Post DDOC not part of defunit
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)  #DDOC_TEST_TARGET(test)¤【defunit】 
int* defunitOther; /// Post DDOC other 
#@DDOC_SAMPLE #@DDOC #@DEFUNITS_NB #@DDOCPOST
/** other DDOC*/
int* defunitOther;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)  
#@DDOC_SAMPLE #@DDOC module defunit; #@DDOCPOST
/++ other DDOC+/
/** other DDOC*/
int* defunitOther;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#comment(PRINT_SEP)   // without module declaration #DDOC_TEST_TARGET(test)¤【defunit】 
#@DDOC_SAMPLE #@DDOC int defunit; #@DDOCPOST
/** other DDOC*/
int* defunitOther;

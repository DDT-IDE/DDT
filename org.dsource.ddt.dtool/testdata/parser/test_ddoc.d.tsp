Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂


#@DDOC_ATTRIBS《
 ►●
 ►extern(D)●
 ►pragma(foo, 123)●
 ►public●
 ►static @safe●
¤》

#@DDOC_SAMPLE《/+NotDDOC+/#DDOC_TEST(test)【/++ StartDDOC +/】》

#@DDOC_PRE《 ●/++/●/**/#DDOC_TEST(test)【/** StartDDOC */】  》
#@DDOC_POST《 ●#?RULE_BROKEN【/// Post DDoc NOT included ||
/*-*/● #DDOC_TEST(test)【/// Post DDOC included |||
】/*-*/】》


#@DEFUNITS_NB《
 ►#@DDOC_ATTRIBS int * defunit ; ●
 ►#@DDOC_ATTRIBS int* defunit(int foo) ; ●
 ►#@DDOC_ATTRIBS this(int foo) ; #DDOC_TEST_TARGET__OVERRIDE(test)¤【this】 ●
 
 ►#@DDOC_ATTRIBS struct defunit { }●
 ►#@DDOC_ATTRIBS union defunit ;●
 ►#@DDOC_ATTRIBS class defunit { } ●
 ►#@DDOC_ATTRIBS interface defunit(int foo) ;●
 
 ►#@DDOC_ATTRIBS enum defunit {A, B, C} ●
 ►#@DDOC_ATTRIBS template defunit(T) { } ●
 ►#@DDOC_ATTRIBS mixin tpl!(foo) defunit; ●
 
 ►#@DDOC_ATTRIBS alias int defunit;●
 ►#@DDOC_ATTRIBS alias int defunit(int foo);●
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
#comment(PRINT_SEP) #DDOC_TEST_TARGET(test) 
#PARSE(DECLARATION)   #@DDOC_PRE  #@DEFUNITS #@DDOC_POST
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP) #DDOC_TEST_TARGET(test) 
#PARSE(DECLARATION)   #@DDOC_PRE  #@^DDOC_PRE #@DEFUNITS
/// Post DDOC not part of defunit
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)  #DDOC_TEST_TARGET(test)¤【defunit】
int* defunitOther; /// Post DDOC other 
#@DDOC_SAMPLE #@DDOC_PRE #@DEFUNITS_NB #@DDOC_POST
/** other DDOC*/
int* defunitOther;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#comment(PRINT_SEP)  #DDOC_TEST_TARGET(test)
#@DDOC_SAMPLE #@DDOC_PRE module defunit; #@DDOC_POST
/++ other DDOC+/
/** other DDOC*/
int* defunitOther;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#comment(PRINT_SEP)   // without module declaration #DDOC_TEST_TARGET(test)¤【defunit】 
#@DDOC_SAMPLE #@DDOC_PRE int defunit; #@DDOC_POST
/** other DDOC*/
int* defunitOther;

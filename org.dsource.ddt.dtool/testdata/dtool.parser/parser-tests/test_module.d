//#SOURCE_TESTS 28 #
//#SPLIT_SOURCE_TEST _____________________
module foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.bar.foo;
//#SPLIT_SOURCE_TEST ___________ empty
//#SPLIT_SOURCE_TEST ___________

//#SPLIT_SOURCE_TEST _____________________ various error cases
module#{, ,#NL, foo., foo.bar.}#{,;,:}#EOF
#@parser:AllowExtraErrors
#//AST_EXPECTED:
module ;
//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID{;}
#//AST_EXPECTED:
module ;
//#SPLIT_SOURCE_TEST _____________________
module 
int #@error:EXP_COMMA{foo};
#//AST_EXPECTED:
module ;

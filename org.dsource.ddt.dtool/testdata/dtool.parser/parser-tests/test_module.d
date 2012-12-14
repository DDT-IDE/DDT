//#SOURCE_TESTS 39 #
//#SPLIT_SOURCE_TEST _____________________
module foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.bar.foo;
//#SPLIT_SOURCE_TEST ___________ empty
//#SPLIT_SOURCE_TEST ___________

//#SPLIT_SOURCE_TEST _____________________ various error cases (15)
module#{, ,#NL}#{,;,:}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module ;
//#SPLIT_SOURCE_TEST _____________________ (6)
module#{ ,   }foo.bar.#{,;,:}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module foo.bar.;
//#SPLIT_SOURCE_TEST _____________________ (6)
module#{ ,   }foo.#{,;,:}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module foo.;



//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID{;}
#//AST_EXPECTED:
module ;

//#SPLIT_SOURCE_TEST _____________________
#@error:SE_ID{module}#{,#NL}
#//AST_EXPECTED:
module ;

//#SPLIT_SOURCE_TEST _____________________
module #@error:SE_SEMICOLON{foo}
#//AST_EXPECTED:
module foo;

//#SPLIT_SOURCE_TEST _____________________ TODO
#@error:SE_ID{module} 
//import blah foo;
#//AST_EXPECTED:
module ;
//blah foo;
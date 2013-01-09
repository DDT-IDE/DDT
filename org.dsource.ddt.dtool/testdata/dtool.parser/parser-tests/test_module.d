//#SOURCE_TESTS 33 #
//#SPLIT_SOURCE_TEST ___________ empty
//#SPLIT_SOURCE_TEST _____________________
module foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.bar.foo;

//#SPLIT_SOURCE_TEST _____________________ various error cases (10)
module#{, ,#NL}#{,;}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module ;
//#SPLIT_SOURCE_TEST _____________________ (4)
module#{ ,   }foo.bar.#{,;}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module foo.bar.;
//#SPLIT_SOURCE_TEST _____________________ (4)
module#{ ,   }foo.#{,;}#EOF
#@parser:AllowAnyErrors
#//AST_EXPECTED:
module foo.;



//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID ;
#//AST_EXPECTED:
module ;

//#SPLIT_SOURCE_TEST _____________________
module pack.#@error:EXP_ID ;
#//AST_EXPECTED:
module pack.;

//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID#@error:EXP_SEMICOLON .foo;
#//AST_EXPECTED:
module ;
.foo;

//#SPLIT_SOURCE_TEST _____________________
module#@error:EXP_ID#@error:EXP_SEMICOLON#{,#NL}
#//AST_EXPECTED:
module ;

//#SPLIT_SOURCE_TEST _____________________
module foo #@error:EXP_SEMICOLON
#//AST_EXPECTED:
module foo;

//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID#@error:EXP_SEMICOLON import #@error:EXP_ID ;
#//AST_EXPECTED:
module ; import ;

//#SPLIT_SOURCE_TEST _____________________
module #@error:EXP_ID#@error:EXP_SEMICOLON import foo;
#//AST_EXPECTED:
module ; import foo;


//#SPLIT_SOURCE_TEST _____________________________________ declDefs errors (basic)
module pack.bar.foo;
#@error:SE_decl{)} ;
import foo;
#//AST_EXPECTED:
module pack.bar.foo;
);
import foo;
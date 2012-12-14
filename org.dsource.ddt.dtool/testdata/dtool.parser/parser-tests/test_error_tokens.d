//#SOURCE_TESTS 2 #
//#SPLIT_SOURCE_TEST _____________________
#@error:UT{}
#//AST_EXPECTED:

//#SPLIT_SOURCE_TEST _____________________
module #@error:UT{}foo#@error:UT{}; 
#//AST_EXPECTED:
module foo;

//#SOURCE_TESTS 2 #
//#SPLIT_SOURCE_TEST _____________________
#@error:ITC{}
#//AST_EXPECTED:

//#SPLIT_SOURCE_TEST _____________________
module #@error:ITC{}foo#@error:ITC{}; 
#//AST_EXPECTED:
module foo;

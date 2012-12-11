//#SOURCE_TESTS 21 #
//#SPLIT_SOURCE_TEST _____________________
module foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.foo;
//#SPLIT_SOURCE_TEST _____________________
module pack.bar.foo;
//#SPLIT_SOURCE_TEST _____________________
//#SPLIT_SOURCE_TEST _____________________

//#SPLIT_SOURCE_TEST _____________________
module//#PARSERTEST#
module ;
//#SPLIT_SOURCE_TEST _____________________
module#{, ,#NL}#{,;,:}//#PARSERTEST#
module ;
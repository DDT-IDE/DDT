▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int Var;

void func() { // Test invalid locations
	x =#REFSEARCH¤【!invalid_token】#FINDFAIL= 2;
	/*A Comment#REFSEARCH¤【!invalid_token】#FINDFAIL */
	
	foo #REFSEARCH¤【!invalid_qualified】.bar;
	
	foo[]#REFSEARCH¤【!other_ref】 foo;
	foo!#REFSEARCH¤【!other_ref】 (123)#REFSEARCH¤【!other_ref】;
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int foo; //Test node boundaries (This test requires that a InvalidSyntaxElement node be created for each line)
foo#FIND¤【foo】$; 
$#FIND¤【foo】foo;
auto x = foo#FIND¤【foo】$;
$#FIND¤【foo】foo bar;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
//test relex  for refsearch (internal performance test)
void func#marker(FUNC) ( #REFSEARCH¤【relexStartPos=FUNC】 int a) #marker(BODY)【】{
	#REFSEARCH¤【relexStartPos=BODY】
	int bar;
	int foo;#marker(ST_A) 
	#REFSEARCH¤【relexStartPos=ST_A】
	#marker(ST)#REFSEARCH¤【relexStartPos=ST】;
}#marker(FUNC_END)


#REFSEARCH¤【relexStartPos=FUNC_END】

/** DDOC */
#marker(CLASS_START)class #REFSEARCH¤【relexStartPos=CLASS_START】 Foo {
	int foo;
}
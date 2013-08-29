Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module tests; // Test identifier-embedded refsearch
int Var;
int Var2;
int Foo;

V#REFSEARCH¤【Var▪Var2】;
V#REFSEARCH¤【Var▪Var2 >>2】xx;
Va#REFSEARCH¤【Var▪Var2】;
Va#REFSEARCH¤【Var▪Var2 >>2】xx;
Vx#REFSEARCH¤【>>2】xx;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#REFSEARCH¤【Var▪implicitName】; // Edge case, refsearch at beginning of file
#PROJECT(implicitName@)
int Var;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
V#@《#REFSEARCH¤【Var】●#REFSEARCH¤【Var>>1】x》; // Edge case, refsearch at beginning of file

int Var;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests; // Test locations that used to be invalid (changed behavior)
int Var; int foo;

void _ignore() {
	x =#REFSEARCH¤【!invalid_token】#FINDFAIL= 2;
	/*A Comment#REFSEARCH¤【!invalid_token】#FINDFAIL */
	
	foo[]#FINDFAIL#REFSEARCH¤【tests▪Var▪foo】 foo;
	foo[#FINDFAIL#REFSEARCH¤【tests▪Var▪foo】] foo;
	foo!#FINDFAIL#REFSEARCH¤【tests▪Var▪foo】(123)#FINDFAIL#REFSEARCH¤【tests▪Var▪foo】;
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
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
//test relex  for refsearch (internal value that is important for performance)
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
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module _ignore; // Test various pick location situations with identifiers
int Var; int Var2;
int Foo;

#REFSEARCH¤【Var▪Var2▪Foo】;
#REFSEARCH¤【Var▪Var2▪Foo►2】xx;
V#REFSEARCH¤【Var▪Var2】;
V#REFSEARCH¤【Var▪Var2►2】xx;
Va#REFSEARCH¤【Var▪Var2】;
Va#REFSEARCH¤【Var▪Var2►2】xx;
Vx#REFSEARCH¤【►2】xx;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@《#REFSEARCH¤【Var▪implicitName】●#REFSEARCH¤【Var▪implicitName►2】xx》; // Edge case, refsearch at beginning of file
int Var; #PROJECT(implicitName@)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
V#@《#REFSEARCH¤【Var】●#REFSEARCH¤【Var►2】xx》; // Edge case, refsearch at beginning of file
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
char inFoo; char inFoo2; // Test completion under keywords
void intMaker(); int other;

in#FIND¤【/int】#REFSEARCH¤【inFoo▪inFoo2▪/int▪intMaker ►1】t; //keyword "int"
int#FIND¤【/int】#REFSEARCH¤【/int▪intMaker】; // keyword "int"
in#FINDFAIL#REFSEARCH¤【inFoo▪inFoo2▪/int▪intMaker】; //keyword "in"

void _ignore() {
	int whileFoo; char whileFoo2;
	while#FINDFAIL#REFSEARCH¤【whileFoo▪whileFoo2】; // keyword "while"
	whi#FINDFAIL#REFSEARCH¤【whileFoo▪whileFoo2 ►2】le; // keyword "while"
	#FINDFAIL#REFSEARCH¤【whileFoo▪whileFoo2▪inFoo▪inFoo2▪intMaker▪other►5】while; // keyword "while"
}

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // Test completion under symbol definition;
int Var;
int Foo#FINDFAIL#REFSEARCH¤【Foo】 #FINDFAIL#REFSEARCH¤【Var▪Foo】;
class#FINDFAIL#REFSEARCH¤【】 #FINDFAIL#REFSEARCH¤【Var▪Foo】{}
class#FINDFAIL#REFSEARCH¤【】 #FINDFAIL#REFSEARCH¤【Var▪Foo】#comment(EOF):
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int foo; //Test node boundaries (This test requires that a InvalidSyntaxElement node be created for each line)
foo#FIND¤【foo】$; 
$#FIND¤【foo】foo;
auto x = foo#FIND¤【foo】$;
$#FIND¤【foo】foo bar;

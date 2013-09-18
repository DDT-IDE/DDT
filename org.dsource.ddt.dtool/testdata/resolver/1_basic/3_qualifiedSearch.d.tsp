Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module tests; // Test qualified refsearch
class Foo {
	int Var;
	int Var2;
	int member;
	int intro;
}
int other;

	Foo.Var#FIND¤【Var】#FIND¤【tests/Foo.Var】;
	
	Foo.V#REFSEARCH¤【Var▪Var2】;
	Foo.V#REFSEARCH¤【Var▪Var2►2】xx;
	Foo.Va#REFSEARCH¤【Var▪Var2】;
	Foo.Va#REFSEARCH¤【Var▪Var2►2】xx;
	Foo.Vx#REFSEARCH¤【►2】xx;
	Foo.Var#REFSEARCH¤【Var▪Var2】;
	
	Foo#FIND¤【Foo】#REFSEARCH¤【Foo】.Var;
	Foo #REFSEARCH¤【!invalid_qualified】#FINDFAIL.Var;
	Foo #REFSEARCH¤【!invalid_qualified】.Var;
	
	Foo.#REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES】#FINDMISSING;
	Foo.#REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES】#FINDMISSING  ;
	Foo. #REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES】#FINDMISSING;
	Foo. #REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES】#FINDMISSING  ;
	
	// Test keywords as qualified identifier - in keyword
	Foo. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES►2】in;
	Foo. i#FINDFAIL#REFSEARCH¤【intro▪init►1】n;
	Foo. in#FINDFAIL#REFSEARCH¤【intro▪init】;
	
	// Test keywords as qualified identifier - int keyword
	Foo. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES►3】int;
	Foo. i#FIND¤【/int】#REFSEARCH¤【intro▪init►2】nt;
	Foo. int#FIND¤【/int】#REFSEARCH¤【intro】;	
	
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module tests; // Test qualified with indirect (exp) qualifier
class Foo {
	int Var;
	int Var2;
	int member;
	int intro;
}
int other;

auto _dummy = (Foo).#REFSEARCH¤【Var▪Var2▪member▪intro▪#@TYPE_PROPERTIES】#FINDMISSING;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests; // test module qualified

	int Var;
	int Var2;
	int member;
	int intro;

invariant() {
	int other;
	#REFSEARCH¤【Var▪Var2▪member▪intro▪other▪tests】 // test self check
	
	.Var#REFSEARCH¤【Var▪Var2】#FIND¤【tests/Var】;
	
	.#REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING;
	.#REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING  ;
	. #REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING;
	. #REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING  ;
	
	// Test keywords as qualified identifier - in keyword
	. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►2】in;
	. i#FINDFAIL#REFSEARCH¤【intro►1】n;
	. in#FINDFAIL#REFSEARCH¤【intro】;
	
	// Test keywords as qualified identifier - int keyword
	. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►3】int;
	. i#FIND¤【/int】#REFSEARCH¤【intro►2】nt;
	. int#FIND¤【/int】#REFSEARCH¤【intro】;	
}
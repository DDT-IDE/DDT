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

#@__CONTEXT_START__
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
	
	Foo.#REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING;
	Foo.#REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING  ;
	Foo. #REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING;
	Foo. #REFSEARCH¤【Var▪Var2▪member▪intro】#FINDMISSING  ;
	
	// Test keywords as qualified identifier
	Foo. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►2】in;
	Foo. i#FINDFAIL#REFSEARCH¤【intro►1】n;
	Foo. in#FINDFAIL#REFSEARCH¤【intro】;
	
	Foo. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►3】int;
	Foo. i#FIND¤【/int】#REFSEARCH¤【intro►2】nt;
	Foo. int#FIND¤【/int】#REFSEARCH¤【intro】;	
	
#@__CONTEXT_END__
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
	
	// Test keywords as qualified identifier
	. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►2】in;
	. i#FINDFAIL#REFSEARCH¤【intro►1】n;
	. in#FINDFAIL#REFSEARCH¤【intro】;
	
	. #FINDMISSING#REFSEARCH¤【Var▪Var2▪member▪intro►3】int;
	. i#FIND¤【/int】#REFSEARCH¤【intro►2】nt;
	. int#FIND¤【/int】#REFSEARCH¤【intro】;	
	
}
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module tests; // Test qualified refsearch
int Var;
int Var2;
int Foo;

#@__CONTEXT_START__
	tests.Var#FIND¤【tests/Var】#FIND¤【Var】;
	
	tests.V#REFSEARCH¤【Var▪Var2】;
	tests.V#REFSEARCH¤【Var▪Var2】xx;
	tests.Va#REFSEARCH¤【Var▪Var2】;
	tests.Va#REFSEARCH¤【Var▪Var2】xx;
	tests.Vx#REFSEARCH¤【】xx;
	tests.Var#REFSEARCH¤【Var▪Var2】;
	
	tests#FIND¤【tests/】#REFSEARCH¤【tests】.Var;
	tests #REFSEARCH¤【!invalid_qualified】#FINDFAIL.Var;
	tests #REFSEARCH¤【!invalid_qualified】.Var;
	
	tests.#REFSEARCH¤【Var▪Var2▪Foo】#FINDMISSING;
	tests.#REFSEARCH¤【Var▪Var2▪Foo】#FINDMISSING  ;
	tests. #REFSEARCH¤【Var▪Var2▪Foo】#FINDMISSING;
	tests. #REFSEARCH¤【Var▪Var2▪Foo】#FINDMISSING  ;
	
#@__CONTEXT_END__
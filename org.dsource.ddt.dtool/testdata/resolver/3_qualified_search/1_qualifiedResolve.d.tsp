Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;

int fA;
class Foo {
	int fooA;
	int fooB;
	Xpto xpto;
	
	static class Inner { int innerx; }
}

.Foo#REFSEARCH¤【Foo】;
Foo.foo#REFSEARCH¤【fooA▪fooB】;

invariant() {
	int fB;
	
	.Foo#REFSEARCH¤【Foo】;
	Foo.foo#REFSEARCH¤【fooA▪fooB】;
	
	.#REFSEARCH¤【Foo▪fA】;
	. #REFSEARCH¤【Foo▪fA】;
	.#REFSEARCH¤【Foo▪fA】  ;
	. #REFSEARCH¤【Foo▪fA】  ;
	
	Foo.#REFSEARCH¤【fooA▪fooB▪xpto▪Inner】;
	Foo.  #REFSEARCH¤【fooA▪fooB▪xpto▪Inner】;
	Foo.#REFSEARCH¤【fooA▪fooB▪xpto▪Inner】  ;
	Foo.  #REFSEARCH¤【fooA▪fooB▪xpto▪Inner】  ;
}
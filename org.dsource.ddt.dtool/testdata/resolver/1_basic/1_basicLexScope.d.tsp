Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int Var;

Var#FIND¤【tests/Var】#FIND¤【Var】;

#REFSEARCH¤【tests▪Var▪Var2▪Class】
static #REFSEARCH¤【tests▪Var▪Var2▪Class】 if(true) {
}

class Class(T #REFSEARCH¤【tests▪Var▪Var2▪Class▪T】, U : T) #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U】 { 
  
  #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】
  int #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】 member;
  if #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】 (true) {
  }
}

int Var2;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int voidLike;
int foo;

class /*Missing DefId*/ {}

void func() {
	voi#REFSEARCH¤【voidLike】; // Test statements forward reference visibitity (or lack of)
	
	int voidName2;
	
	voi#REFSEARCH¤【voidLike▪voidName2】;
	void#FIND¤【】#REFSEARCH¤【voidLike▪voidName2】; // Test primitive ref TODO find
}

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int foo;

alias align(123 ;
class /*Missing DefId*/ {}
Foo;

#REFSEARCH¤【tests▪foo】

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ TODO
void#FIND¤【void】; // Test primitive ref

int other;

#REFSEARCH¤【bool▪byte▪ubyte▪short▪ushort▪int▪uint▪long▪ulong▪char▪wchar▪dchar▪float▪double▪real▪void▪
    ifloat▪idouble▪ireal▪cfloat▪cdouble▪creal▪other
】;
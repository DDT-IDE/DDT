Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int other;

#REFSEARCH¤【/bool▪/byte▪/ubyte▪/short▪/ushort▪/int▪/uint▪/long▪/ulong▪/char▪/wchar▪/dchar▪/float▪/double▪/real▪/void▪
    /ifloat▪/idouble▪/ireal▪/cfloat▪/cdouble▪/creal▪other
】;
u#REFSEARCH¤【/ubyte▪/ushort▪/uint▪/ulong】;
void#FIND¤【/void】#REFSEARCH¤【/void】; // Test primitive resolving

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
	voi#REFSEARCH¤【voidLike▪/void】; // Test statements forward reference visibitity (or lack of)
	
	int voidName2;
	
	voi#REFSEARCH¤【voidLike▪voidName2▪/void】;
	void#FIND¤【/void】#REFSEARCH¤【voidLike▪voidName2▪/void】;
}

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int foo;

alias align(123 ;
class /*Missing DefId*/ {}
Foo;

#REFSEARCH¤【tests▪foo】

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int Var;

Var#FIND¤【tests/Var】#FIND¤【Var】;

#REFSEARCH¤【tests▪Var▪Var2▪Class】
static #REFSEARCH¤【tests▪Var▪Var2▪Class】 if(true) {
}

class Class(T #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】, U : T) #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】 { 
  // TODO the previous ones should not find 'member'
  
  #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】
  int #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】 member;
  if #REFSEARCH¤【tests▪Var▪Var2▪Class▪T▪U▪member】 (true) {
  }
}

int Var2;

V#REFSEARCH¤【Var▪Var2】;
V#REFSEARCH¤【Var▪Var2】xx;
Va#REFSEARCH¤【Var▪Var2】;
Va#REFSEARCH¤【Var▪Var2】xx;
Vx#REFSEARCH¤【】xx;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
V#REFSEARCH¤【Var】#@《x●》; // Edge case, refsearch at beginning of file

int Var;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
int voidLike;
int foo;

void func() {
	voi#REFSEARCH¤【voidLike】; // Test statements forward reference visibitity (or lack of)
	
	int voidName2;
	
	void#FIND¤【】; // Test primitive ref
	
	voi#REFSEARCH¤【voidLike▪voidName2】;
	void#REFSEARCH¤【voidLike▪voidName2】; // Test primitive ref
}

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
TODO:
void#FIND¤【void】; // Test primitive ref

	#REFSEARCH¤【bool▪byte▪ubyte▪short▪ushort▪int▪uint▪long▪ulong▪char▪wchar▪dchar▪float▪double▪real▪void▪
    ifloat▪idouble▪ireal▪cfloat▪cdouble▪creal▪voidLike▪voidName2
】;
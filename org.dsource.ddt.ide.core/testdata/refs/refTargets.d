module refTargets;
refTargets.Class dummy0;

int Var; 
int dummy = Var;

typedef char[] Typedef;
Typedef dummy8;

alias char[] Alias;
Alias dummy9;

int Function(int Parameter) 
in {
} out(Result) {
 	Result++;
} body {
	Parameter++;
}
auto dummy7 = &Function;


class Class { static int member1; int member2; }
Class aClass;
int dummy2 = Class.member1 + aClass.member2;

interface Interface { static int member1; int member2; }
Interface aInterface;
int dummy3 = Interface.member1 + aInterface.member2;

struct Struct { static int member1; int member2; }
Struct aStruct;
int dummy4 = Struct.member1 + aStruct.member2;

union Union { static int member1; int member2; }
Union aUnion;
int dummy5 = Union.member1 + aUnion.member2;

enum Enum { member1, member2, }
Enum aEnum;
int dummy6 = Enum.member1 + Enum.member2;


template Template(
	TemplateParamType, 
	int TemplateParamValue, 
	alias TemplateParamAlias,
	TemplateParamTuple...) 
{
	alias TemplateParamType dummyT0;    
	int dummyT1 = TemplateParamValue;
	int dummyT2 = TemplateParamAlias;
	int dummyT3 = func(TemplateParamTuple);
}
Template!(int, 10, dummy, 1, two, 3) dummyT;

int Mvar1, Mvar2, Mvar3;
int dummy11 = Mvar1 + Mvar2 + Mvar3;

static if(is(Foo Type : Object)) {
	Type dummy20;
}

class DummyClass(T) : T {
	T foo;
}

struct DummyStruct(T) {
	T foo;
}


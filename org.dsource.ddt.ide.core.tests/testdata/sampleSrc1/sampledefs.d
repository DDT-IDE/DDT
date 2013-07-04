module sampledefs;
// A sample file with all kinds of DefUnits
// Model Element test constraints: names of elements must not have a common prefix 

import pack.sample : ImportSelectiveAlias = SampleClassB;

import ImportAliasingDefUnit = pack.sample;

static import pack.sample;


int Variable, Variable2;

/** DDOC */
static const int VarExtended, VarExtended2; /// Post DDoc

// TODO: more tests with extenden range (DDOC and attributes prefix)

auto AutoVar = 66, AutoVar2;

void Function(int fooParam) {
	int fooLocalVar;
}

static AutoFunction(int fooParam) {
	int fooLocalVar;
}

struct Struct { }

union Union { }

class Class { }

interface Interface { }

template Template() {} 

mixin foo!() Mixin;

enum Enum { EnumMemberA, EnumMemberB }

enum { EnumDeclMemberA, EnumDeclMemberB }

alias TargetFoo AliasVarDecl;
alias TargetFoo AliasFunctionDecl(int param);
alias AliasFrag = int, AliasFrag2 = char;


void OtherFunction(int foo) {
	int LocalVar;
	class LocalClass { }
}

// Nested elements:

class OtherClass  {
	int fieldA;
	
	/*this*/ this(int ctorParam) { 
		auto x = ctor/+CC-ctor@+/; 
	}
	/*~this*/ ~this() {}
	
	/*new*/ new() {}
	/*delete*/ delete() {}
	
	void methodB() { }
}

template OtherTemplate(
	TypeParam,
	int ValueParam,
	alias AliasParam,
	TupleParam...
) { 
	
	class TplNestedClass  {
		static /*static this*/ this() {}
		static /*static ~this*/ ~this() {}
		
		void tplFunc(asdf.qwer parameter) {
			static if(is(T IfTypeDefUnit : Foo)) {
				/+@CC1+/
			}
			
			{
				Enum e;
				e = Enum.E/+@CC2+/;
			}
		}
	}
}

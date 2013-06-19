module sampledefs;
// A sample file with all kinds of DefUnits
// Model Element test constraints: names of elements must not have a common prefix 

import pack.sample : ImportSelectiveAlias = SampleClassB;

import ImportAliasingDefUnit = pack.sample;

static import pack.sample;

int variable;

void functionFoo(int fooParam) {
	int fooLocalVar;
}

class Class  {
	int fieldA;
	
	/*this*/ this(int ctorParam) { 
		auto x = ctor/+CC-ctor@+/; 
	}
	/*~this*/ ~this() {}
	
	/*new*/ new() {}
	/*delete*/ delete() {}
	
	void methodB() { }
}

interface Interface { }

struct Struct { }

union Union { }

enum Enum { EnumMemberA, EnumMemberB }

typedef TargetBar Typedef; // TODO remove

alias TargetFoo Alias;

template Template(
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

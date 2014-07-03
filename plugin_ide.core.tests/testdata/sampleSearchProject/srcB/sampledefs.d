module sampledefs;
// A sample file with all kinds of DefUnits

import pack.sample : ImportSelectiveAlias = SampleClassB;

import ImportAliasingDefUnit = pack.sample;

static import pack.sample;

int variable;

class Class  {
	int fieldA;
	
	/*this*/ this(int param) {}
	/*~this*/ ~this() {}
	
	/*new*/ new() {}
	/*delete*/ delete() {}
	
	void methodB() { }
}

interface Interface { }

struct Struct { }

union Union { }

enum Enum { EnumMemberA, EnumMemberB }

typedef TargetBar Typedef;

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
		
		void func(int paramA, asdf.qwer paramB) {
			static if(is(T IfTypeDefUnit : Foo)) {

	/+-------- Test refs from non FQ context --------+/
	auto xxx1 = /*sampledefs.Template*/Template;
	auto xxx2 = /*sampledefs.Template.TplNestedClass*/TplNestedClass;
	auto xxx3 = /*sampledefs.Template.TplNestedClass.func*/func();
	auto xxx4 = /*sampledefs.Template.TplNestedClass.func.parameter*/parameter;
	auto xxx5 = /*sampledefs.Template.TplNestedClass.func.IfTypeDefUnit*/IfTypeDefUnit;

	/+----------------+/
			}
		}
	}
}

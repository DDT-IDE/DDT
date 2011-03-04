module sampledefs;
// A sample file with all kinds of DefUnits

alias Class Alias;

enum Enum { EnumMemberA, EnumMemberB }

interface Interface { }

struct Struct { }

typedef Struct Typedef;

union Union { }

int variable;

import pack.sample : ImportSelectiveAlias = SampleClassB;

import ImportAliasingDefUnit = pack.sample;


template Template(
	TypeParam,
	int ValueParam,
	alias AliasParam,
	TupleParam...
) { 

	class Class  {
		this() {}
		~this() {}
		
		void func(asdf.qwer parameter) {
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

static import pack.sample;






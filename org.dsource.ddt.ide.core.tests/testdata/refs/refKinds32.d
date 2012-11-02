/** Test for not found entities **/
                                      
class Foo {
	int foox;
	
	static class Inner { 
		int innerx;
	}
}
template Tpl(T) {
	alias int TplType;
}

/*** Same scope root ref ***/

// Entity Qualified
Foo.InnerMISS inner;  // miss 2nd
Tpl!(int).TplTypeMISS tpltype; // miss 2nd
int dummy3 = Foo.fooxMISS;  // miss 2nd
int dummy32 = Foo.InnerMISS.innerx; // miss 2nd
int dummy32 = Foo.Inner.innerxMISS; // miss 3rd

class Dummy : FooMISS {
	int dummy = MISS;
}

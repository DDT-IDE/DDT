int mz;
class Fzz {
	int foox;
	Xpto xpto;
	
	static class Inner { 
		int innerx;
	}
}
class Zpto {
	char[] zzr;
}
template Zpl(T) {
	int num;
	alias int TplType;
}

/*** Same scope root ref ***/

// Entity Identifier
Foo foo; // (type)
int dummy1 = mx; // (exp)

// ModuleRoot
.Foo foo; // (type)
int dummy2 = .mx; // (exp)

// Entity Template Instance
Tpl!(int) tpl; //  
int dummy4 = Tpl!(int); // 

// Entity Qualified
Foo.Inner inner;  // 2, identifier
.Foo.Inner inner;  // 2, module root
Tpl!(int).TplType tpltype; // 2, tpl instance
int dummy3 = Foo.foox; // 2, identifier (exp)
int dummy31 = .Foo.foox; // 2, module root (exp)  
int dummy32 = Foo.Inner.innerx; // 2, identifier (exp)

// Base Class reference
class FooBar : Foo { } 

/** Test file for defunits not found (at first scope) */
import nonexist; // Make sure it doesn't crash on imports
import pack.nonexist2;

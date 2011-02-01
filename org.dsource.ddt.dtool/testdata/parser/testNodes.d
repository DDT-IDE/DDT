module foo;


// ==============   Pragmas   ==============
 
pragma(msg, "msg2");
 
void func() {
	pragma(msg, "msg");
}
 

// ==============   imports  ==============

import std.stdio;

this() {

}


// ==============   Mixin declaration   ==============
// TODO

// ==============   Enums   ==============

enum asdf { A, B, C}
enum asdf : char { A, B, C}
//enum { A, B, C}
//enum : long{ A, B, C}


// ==============   ClassDeclaration   ==============

class Foo {
	int x;
}

class FooBar : Foo {
	void func() {
		int a;
		a++;
	}
}

// ==============   InterfaceDeclaration (struct or union)   ==============

interface D {
    int foo();
}

// ==============   AggregateDeclaration (struct or union)   ==============


struct X { int a; int b; int c; int d = 7;}
union X { int a; int b; int c; int d = 7;}


// ==============   Declaration   ==============
// below

// ==============   Constructor   ==============
this() {
	writeln("Foo");
}

// ==============   Destructor   ==============
~this() {
	writeln("Foo");
}

// ==============   Invariant   ==============
// ??

// ==============   UnitTest   ==============
// ??

// ==============   StaticConstructor   ==============
static this() { writeln("Foo"); }

// ==============   StaticDestructor   ==============
static ~this() { writeln("Foo"); }



// ==============   Declaration   ==============

// ----- Var ----

int a = 2;
//int a, b, c;
Foo foo;

void func() {
	int a;
	a++;
	Foo foo;
}

// ----- Var ----

template Tpl(A, B : int, C : int = void, int N) {

}




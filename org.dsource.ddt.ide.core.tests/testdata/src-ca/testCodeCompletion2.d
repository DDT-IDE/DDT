module testCodeCompletion2;

//import std.stdio;
//import std.c.stdio;

class Foo {
	int foovar;
	int foox;
	
	char baz;
}

int fooOfModule;
struct foo_t {};
int ix;

class FooBar : Foo {
	intum foobarvar;
	
	void func();

	void test1() {
		f/+@CC1+/ // non qualified ; recovery
	}
	
	void test2() {
		Foo.f/+@CC2+/ // qualified ; recovery
	}
	
	void test3() {
		.f/+@CC3+/ // qualified ; and . recovery
	}
	
}

import pack.mod3;
import nonexistantmodule.blah; // Test this in the face of non-existant
import nonexistantmodule;

invariant () {
	Foo./+@CC4+/ // qualified ; and . recovery
}

/// Test code completion recovery
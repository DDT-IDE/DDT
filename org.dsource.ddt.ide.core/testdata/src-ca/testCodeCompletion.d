/// Test code completion lookup
module testCodeCompletion;

//import std.stdio;
//import std.c.stdio;

class Foo {
	int foovar;
	int foox;
	
	char baz;
}

version(Debug) {
	int fooOfModule;
	version(Debug2) {
		int frak;
	}
}

alias Foo fooalias;
struct foo_t {};
int ix;

void func(char b, List!(Foo) b);
void func();

class FooBar : Foo {
	intum foobarvar;
	char ix; // a duplicate
	
	void func(int a, List!(Foo) a);
	
	void test(int fParam) {
		/+CC1@+/; // 0 char prefix
		/+CC2@+/f; // 1 char prefix 
		/+CC3@+/foo; // 3 char prefix
		
		/+CC4@+/fo; // 2 char prefix, with common prefix
		
		f/+@CC.I+/; // test interactive CA (moving cursor left and right)
		
		/+CC6@+/Foo ; // Qualified 0 char prefix
		/+CC6b@+/Foo.f; // Qualified 1 char prefix
		
		/+CC7@+/ ; // Module Qualified 0 char prefix
		/+CC7b@+/.f; // Module Qualified 1 char prefix
		
		/+CC8@+/Xpto.xptofoo.f; // Module Qualified 
		int fDecoy;
		
		/+CC9@+/foo == bar; // Test inside token
		/+CC10+/Foo  .  f; // Test inside spaces of refQualified
	}
}

class Xpto {
	static Foo!(int, Foo) xptofoo;
	static FooBar xptofoobar;
} 


import pack.mod3;
import nonexistantmodule.blah; // Test this in the face of non-existant
import nonexistantmodule;

import pack.mod3; // Put a duplicate

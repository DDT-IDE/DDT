/// Test code completion lookup duplicates and forwards
module testCodeCompletion;

//import std.stdio;
//import std.c.stdio;

class Foo {
	int foovar;
	int foox;
	
	char baz;
	int ix;
	
	char func(int a, List!(Foo) a);
	char func();
	
}

alias Foo fooalias;
struct foo_t {};
int ix;

void func(int aaa, List!(Foo) aaa);
void func(int bbb, List!(Foo) bbb); // same as previous, but still shows up
char func(char a, List!(Foo) a); // immediate param overload
void func(int a, List!(Bar) a); // complex param overload 
void func();


class FooBar : Foo {
	intum foobarvar;
	char ix; // a duplicate
	
	void test(int fParam) {
		int foolocal1; 

		.f/+@CC1+/; 

		{
			int foolocalinner;
			f/+@CC2+/;
			
			int foolocalinner2;
		}    

		
		
		int foolocal2; 
	}
	
	int func(int a, List!(Foo) a);
	int func(int bbb, List!(Foo) bbb); // same as previous
	char func(char a, List!(Foo) a);
	int func(int a, List!(Bar) a); 
	int func();
}

class Xpto {
	static Foo!(int, Foo) xptofoo;
	static FooBar xptofoobar;
} 


import pack.mod3;
import nonexistantmodule.blah; // Test this in the face of non-existant
import nonexistantmodule;

import pack.mod3; // Put a duplicate

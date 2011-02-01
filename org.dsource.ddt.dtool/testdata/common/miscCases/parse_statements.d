

// Test what kind of nodes/grammar is allowed as statements


void func() {

	//import std.stdio;
	
	alias int myint;
  
	void myFunc();
  
	void myFunc2() {
  		return;
	}
  
	struct mystruct {
	}
  
	class MyClass {
	}
  
//	template mytpl(T) {
//	}
  


	Complex!(T) addI(T)(T x) {
		return x + Complex!(T)(0.0, 1.0);
	}
	
	void myFunc(T)();
  
	void myFunc2(T)() {
		T foo;
  		return;
	}	
	struct mystruct(T) {
		T foo;
	}
	
	class MyClass(T) {
		T foo;
	}
  
}
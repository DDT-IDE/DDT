
int /+T1@+/mx;                                          
                                          
int mxref = /+R1@+/mx; // same scope (to module)

class Foo {
	int mxref = /+R2@+/mx; // 1 outer scope (to module)
	int /+T3@+/foox;
	
	int func(int /+T5@+/a) in { 
		int mx;	int foox; // decoys
	} out(mx) {
		int mx;	int foox; // decoys
	} body	{
		{ 
			int mx;	int foox; // decoys
		}
		/+R3@+/mx++; // 2 outer scope (to module)
		/+R4@+/foox++; // 1 outer scope (to class)
		/+R5@+/a++; // 1 outer scope (to function param)
	}	
}

class FooBar : Foo, IFooBar {

	void func(int a) {
		/+R6@+/foox++; // 1 super scope (to class)
		/+R7@+/ibarx++; // 2 super scope (to interface)
		/+R8@+/ifoobarx++; // 1 super scope (to interface)
		
		int /+T8b@+/foox; // decoy
		/+R8b@+/foox++;
	}

}

interface IBar {
	static int /+T7@+/ibarx;
}

interface IFooBar : IBar {
	static int /+T8@+/ifoobarx = /+R9@+/ibarx; // 1 super scope (to interface)
}

void func(int a, int dummy = /+R10@+/a) {
}
int /+T10@+/a;

const int /+TEa@+/B = 2;

enum Enum { A = /+REa@+/B, /+TEb@+/B, C = /+REb@+/B };


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int mx#marker(MX);

class Foo {
	int mxref = #FIND¤【@MX】mx; // 1 outer scope (to module)
	int foox#marker(Foo.foox);
	
	int func(int a) in { 
		int mx;	int foox; // decoys
	} out(mx) {
		int mx;	int foox; // decoys
	} body	{
		{ 
			int mx;	int foox; // decoys
		}
		#FIND¤【@MX】mx++; // 2 outer scope (to module)

		#FIND¤【@Foo.foox】foox; // 1 outer scope (to class)
		f#FIND¤【@Foo.foox】oox; // 1 outer scope (to class)
	}
}

class FooBar : Foo, IFooBar {

	void func(int a) {
		#FIND¤【@Foo.foox】foox; // outer:FooBar
		#FIND¤【@ibarx】ibarx++; // outer:IFooBar:IBar
		#FIND¤【@ifoobarx】ifoobarx++; // outer:IFooBar
		
		int foox#marker(func_foox); // decoy
		#FIND¤【@func_foox】foox++;
	}

}

interface IBar {
	static int ibarx#marker(ibarx);
}

interface IFooBar : IBar {
	static int ifoobarx#marker(ifoobarx);
}

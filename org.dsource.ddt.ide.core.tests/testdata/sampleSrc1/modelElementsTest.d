
void Function(int foo) {
	int LocalVar;
	class LocalClass { }
}

class Class  {
	int fieldA;
	protected static immutable int fieldB;
	const protected final int fieldC;
	
	/*this*/ this(int ctorParam) { 
		auto x = ctor/+CC-ctor@+/; 
	}
	/*~this*/ ~this() {}
	
	/*new*/ new() {}
	/*delete*/ delete() {}
	
	override void methodA() { }
	const static final immutable void methodB() { }
}

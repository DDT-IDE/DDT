/**
 * Module doc
 */
/+@TEST@+/ module ddoc;

/** Struct1 doc */
struct Struct1 { }

/++ 
 + Struct2 doc 
 +/
/* Struct2-xx */
struct Struct2 { }

union Union { }

/** variable */
int variable;  /// var-dxx

/** variable2 */
int variable2;  // var2-xx


/** 
 * func1 Read the file.
 * Returns: The contents of the file.
 */
public void func1(int parameter) {
}

/** func2 blah. */
extern void func2(int parameter) {
}

/** func3 blah. */
static void func3(int parameter) {
}

/** func4 blah. */
debug void func4(int parameter) {
}

/** funcXX blah. */
debug public synchronized const void funcXX(int parameter) {
}



class Class  {
	this() {}
	~this() {}
		
	/** func doc */
	void func(int parameter) {
	}
}

// TESTS: a lot more to do here.
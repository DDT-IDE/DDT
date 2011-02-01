/**
 * Module doc
 */
/+@TEST@+/ module ddoc;

/** Struct1 doc */
struct Struct1 { }

/++ 
 + Struct2 doc 
 +/
/* Struct2-b */
struct Struct2 { }

union Union { }

/** variable */
int variable;  /// var-b


/** 
 * func1 Read the file.
 * Returns: The contents of the file.
 */
void func1(int parameter) {
}


/** variable2 */
int variable2;  // var2-b

class Class  {
	this() {}
	~this() {}
		
	/** func doc */
	void func(int parameter) {
	}
}

// TESTS: a lot more to do here.
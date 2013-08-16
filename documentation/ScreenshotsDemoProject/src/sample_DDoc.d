module sample_DDoc;

import std.regex, std.string;

/**
This is my sample function.
This a $(I sample) DDoc.

Example:
----
assert(myFunc() == 0);
----
 */
int myFunc(int a, Object foo = null) {
	// ..
	return 0;
}

void test() {
	
	myFunc();
	
}





// TODO $(D ) macro
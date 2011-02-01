/** DOC for module */
module test;

import pack.sample;
import std.stdio;

/* This is a normal ocmment */
alias pack.sample.TList[1] Erase;
 
/** This is a DDoc commment
 * This func does stuff
 */
string func(int a, int[] as...) {

	auto asd = 2 + 2; // Here is another normal comment
	otherfunc(1234, "Here is a String", true).asd;
	
	/* simple common */
	Foo!(int*, Foo) xptofoo;
	 
	pack.sample[4].TList[1]++;
	
	foreach(a; asdf) {
		if(true) {
			return "Another String";
		}
	}
}
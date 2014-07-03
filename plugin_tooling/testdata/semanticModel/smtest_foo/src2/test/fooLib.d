module test.fooLib;

import std.stdio;
import basic_lib_foo;

void fooLibFunction_test(string str)
{
	barLibFunction("FooLib " ~ str);
	writeln("FooLib", str);
}

module test.fooLib;

import std.stdio;
import bar_lib;

void fooLibFunction_test(string str)
{
	barLibFunction("FooLib " ~ str);
	writeln("FooLib", str);
}


import std.stdio;
import basic_lib_foo;

void fooLibFunction(string str)
{
	barLibFunction("FooLib " ~ str);
	writeln("FooLib", str);
}

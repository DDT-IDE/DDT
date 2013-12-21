
import std.stdio;
import bar_lib;

void fooLibFunction(string str)
{
	barLibFunction("FooLib " ~ str);
	writeln("FooLib", str);
}

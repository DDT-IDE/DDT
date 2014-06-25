
import std.stdio;

void barLibFunction(string str)
{
	writeln("FooLib", str);
}


import basic_pack.bar/*MARKER*/;
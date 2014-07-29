
import std.stdio;

void barLibFunction(string defA/*DEF*/)
{
	writeln("FooLib", defA/*defA_REF1*/);
}


import basic_pack.bar/*MARKER*/;
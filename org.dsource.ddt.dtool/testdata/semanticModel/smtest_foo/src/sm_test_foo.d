
import std.stdio;
import basic_lib_foo;

import mod_nested_import_only;

void func(string str)
{
	barLibFunction("FooLib " ~ str);
	writeln("FooLib", str);
	
	nested_module_Function("blah");
}

module mod_nested_import_only;

import std.stdio;
import basic_lib_foo;

void nested_module_Function(string str)
{
	writeln("NestedMOdule", str);
}

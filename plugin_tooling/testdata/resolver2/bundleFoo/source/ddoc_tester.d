import std.stdio;

import basic_pack.bar;

/** Some DDOC */
void fooFunc(string defA)
{
	
	auto xxx = fooFunc/*fooFunc_ref*/();
	
	auto a1 = "string";
	auto a2 = int;
	auto aError;
	
	Bar bar;
	auto a3 = bar;
	
	auto multiple1 = "string", multiple2 = int;
	
	enum e1 = "string";
	enum e2 = int;
	
	enum em1 = "string", em2 = int;
	
}

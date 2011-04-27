#!/usr/bin/dmd -run
module coloring_preview;

import std.stdio;

/*
 * A multiline comment.
 */
 
int a = 1234; // Single line comment
char b = 'z'; /// Single line doc comment

/+ 
void oldfunc() {
	/+ A nested comment +/
	int a = 2; // Another nested comment
}
+/

/** A documentation comment.
 * TODO: put more D Doc operators here here 
 */
public class Foo {
	
	private int mData;
	
    @property int data() { return mData; }	// read property
	
	@safe
	pure bool func(immutable int[] anArray) nothrow {
		if(anArray is null) return false;
		return (anArray.length == mData);
	}
}

string str = example("normal \"string\" ", x"0123456789ABCDEF", "utf8"c, "utf16"w, "utf32"d);
string wysiwyg1 = `WYSIWYG 
 "string\" `; 
string wysiwyg2 = r"WYSIWYG 
\string\ ";

string strdelim = q"(foo(xxx))" ~ q"[foo{]";  
string strdelim2 = writefln(q"EOS
This is a multi-line
heredoc "string"
EOS"
);

string tokenString = q{ foo(a + 2, "blah"); };
string tokenString2 = q{  
/** A documentation comment. */
void func() {
	int a = 2; // Single line commment
	if(true) return;
}
};
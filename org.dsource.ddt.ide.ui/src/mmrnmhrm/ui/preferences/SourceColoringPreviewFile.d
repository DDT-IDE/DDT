#!/usr/bin/dmd -run
module ColoringPreview;

import std.stdio;

int a = 1; // Single line comment
int a = 1; /// Single line doc comment

/*
 * A multiline comment.
 */

/** A documentation comment.
 * TODO: put more D Doc here 
 */
void func() {
	int a = 2; // Single line commment
	if(true) return;
}

/+ 
void func() {
	int a = 2; // Single line commment
	/+ A nested comment +/
}
+/

string str = "normal \"string\" " ~ h"0123456789ABCDEF" ~ "utf8"c ~ "utf16"w ~ "utf32"d;
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

string tokenString = q{ foo(a + 2, "blah"); }
string tokenString2 = q{  
/** A documentation comment. */
void func() {
	int a = 2; // Single line commment
	if(true) return;
}
}



module coloring_preview;
import std.stdio;

/** A documentation comment.
 * TODO: put more D Doc operators here here 
 */
public class Foo {
	
	@safe
	pure bool func(immutable int[] anArray) nothrow {
		int a = 1234; // Single line comment
		char b = 'z'; /// Single line doc comment
		string str = "a string";
		
		if(anArray is null) 
			return false;
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
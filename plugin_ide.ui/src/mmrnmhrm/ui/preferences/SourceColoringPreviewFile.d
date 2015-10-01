import std.stdio;

/** 
 * A multi-line documentation comment. 
 * TODO: put some sample D Doc operators here
 */
public class Foo {
	
	@safe
	pure bool func(immutable int[] anArray) nothrow {
		int a = 1234; // Single line comment
		string str = "Hello" /// Single line doc comment
			~ 'z' ~ q"(foo(xxx))"; 
		
		if(anArray is null) 
			return false;
		return (anArray.length == mData);
	}
	
}

string strdelim = writefln(q"EOS
This is a multi-line
heredoc "string"
EOS"
);

string tokenString = q{  
/** A documentation comment. */
void func() {
	int a = 2; // Single line commment
	if(true) return;
}
};
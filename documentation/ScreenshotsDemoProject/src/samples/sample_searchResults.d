module sample_searchResults;

import std.stdio;

struct MyStruct {
   int data;
}
class MyClass {
   int data;
}

void searchResults() {
   // play with a MyStruct object
   MyStruct s1;
   MyStruct s2 = s1;
   ++s2.data;
   writeln(s1.data); // prints 0
   // play with a MyClass object
   MyClass c1 = new MyClass;
   MyClass c2 = c1;
   ++c2.data;
   writeln(c1.data); // prints 1
}
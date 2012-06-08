

@Safe
auto func(T arg2) {
}

@Trusted
immutable func(T arg2) {
}


// These ones are not supported yet
//shared shared shared void func(T arg2) { }
//shared @Foo shared void func(T arg2) { }
 

@Foo @Bar void func(T arg2) {
}

@Foo public class FooBar {
}

@Foo shared Object foo;


struct StructFoo
{
	
    @property f2() { return 123; }
    
    void f3() @property { return 123; }
    
}

size_t length() @property { return 1; }

@property Value[Key] rehash() @property
{
    return null;
}
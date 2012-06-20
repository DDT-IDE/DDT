typeof(Foo) foo;
typeof(Foo!(T)) foo;
typeof(Foo!(int[T]).blah) foo;
//typeof(int[]) foo;
//typeof(int) foo;
//typeof(void) foo;

void func() {
	typeof(Foo) foo;
	typeof(Foo!(T)) foo;
	typeof(Foo!(int[T]).blah) foo;
	//typeof(int[]) foo;
	//typeof(int) foo;
	//typeof(void) foo;
}

/+__ INVALID _____________________+/
typeof(Foo
/+__ INVALID _____________________+/
typeof(
/+__ INVALID _____________________+/
typeof
/+__ INVALID _____________________+/
typeof()
/+__ INVALID _____________________+/
typeof)
/+__ INVALID _____________________+/
typeof(int[]) foo;
typeof(int) foo;
typeof(void) foo;

module declAttrib;

public:

void func() {
if(true) {}
}

public {
}

pragma(foo);

pragma(foo) {
}

pragma(foo) {
	class Foo {}
}

/** Doc */
pragma(lib, "gdi32.lib");

/** Doc */
pragma(lib, "gdi32.lib") {
}

/** Doc */
pragma(lib, "gdi32.lib") {
	class Foo {}
}
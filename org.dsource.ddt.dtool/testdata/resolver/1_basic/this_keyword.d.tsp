Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;

static this() {
}
static ~this() {
}

class Foo {

	this(int foo ) {
	}
	~this() {
	}
	
	#REFSEARCH¤【tests▪Foo】 // Test that "this" doesn't show up
}

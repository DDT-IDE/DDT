module lib_tpl.mod;

import lib_foo.mod;


template Tpl(T...) {
	T[0] a;
	T[1] b;

	int x;
}


void func() {
	mixin Tpl!(int, lib_foo.mod.Foo);
	x = 2; 
}

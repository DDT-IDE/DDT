mixin foo;
mixin foo!();
mixin foo!(T, ASD);

mixin foo mymixin;
mixin foo!() mymixin;
mixin foo!(T, ASD) mymixin;

void func() {
	mixin foo;
	mixin foo!();
	mixin foo!(T, ASD);
	
	mixin foo mymixin;
	mixin foo!() mymixin;
	mixin foo!(T, ASD) mymixin;
}

/+__ INVALID _____________________+/
mixin;
/+__ INVALID _____________________+/
mixin
/+__ INVALID _____________________+/
mixin foo
/+__ INVALID _____________________+/
mixin foo mymixin
/+__ INVALID _____________________+/
mixin !() mymixin;
/+__ INVALID _____________________+/
mixin !();


S x = { 1 , 2 };
S x2 = { a:1, b:2 };
auto x2 = { a:1, b:2 };

void func() {
	S x = { 1 , 2 };
	S x = { a:1, b:2 };
	auto x = { 1, b:2};
	auto x = { 1, b:2, 3};
}


/+__ INVALID _____________________+/
S s = {  a:1, b: };

/+__ INVALID _____________________+/
void func() {
	s = { a:1, b:2 };
}
/+__ INVALID _____________________+/

S s = {  a:1, b: };

void func() {
	s = { a:1, b:2 };
}


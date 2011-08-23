void func() {
	auto x = new (immutable Foo)(10);
}

void func() {
	auto x = new (20);
}

version(case2) {
	auto x = new (20);
}



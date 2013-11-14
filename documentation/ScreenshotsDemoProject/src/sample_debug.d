module sample_debug;

struct MyStruct {
	int data;
	string data2;
   
	void writeContents() {
		import std.stdio;
		writeln("x.data: ", data, "x.data2: ", this.data2);
	}
}

void main() {
	myFunc(12);
	myFunc(10);
	myFunc(7);
}

void myFunc(int param) {
	auto x = new MyStruct();
	x.data = param;
	x.data2 = "SAMPLE DATA";
	
	x.writeContents();
}
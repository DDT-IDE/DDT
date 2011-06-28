import pack.mod1;
//import pack.mod2, pack.sample, pack.subpack.mod3; 

int dummy1 = mod1Var;
Mod1Class dummy2;
ClassThatDoesNotExist dummy3; // An invalid reference

void func() {
	mod1Var++;
	Mod1Class mod1class;
	.mod1Var++;
	.Mod1Class mod1class;
}


void func2() {
	SampleClass.foo++;
	
	pack2.foopublic.foopublicImportVar++;
}

/*MultipleSelection*/SampleRefsClass xxx;

class /*Class1*/SampleRefsClass {
}
class /*Class2*/SampleRefsClass(T:int) {
}
class /*Class3*/SampleRefsClass(T:Object) {
}

private class Parent {
	auto xxx = /*MultipleSelection2*/func(2);
	
	void /*func1*/func() {
	}
	void /*func2*/func(int a) {
	}
	void /*Class3*/func(Object a) {
	}
}
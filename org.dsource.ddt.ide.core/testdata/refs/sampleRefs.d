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

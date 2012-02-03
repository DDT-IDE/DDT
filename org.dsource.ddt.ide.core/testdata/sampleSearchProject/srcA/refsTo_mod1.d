module refsTo_mod1;

import pack.mod1;

int a = /*pack.mod1.mod1Var*/mod1Var;

void func() {
	/*pack.mod1.Mod1Class*/Mod1Class foo1;
}


void func2() {
	/*pack.mod1.Mod1Class*/Mod1Class foo2;
	
	/*pack.mod1.mod1Var*/mod1Var++;
}

void func3() {
	/*pack.mod1.mod1Func*/mod1Func();
}

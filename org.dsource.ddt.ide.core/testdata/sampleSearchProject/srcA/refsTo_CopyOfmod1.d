module refsTo_CopyOfmod1;

// search1_decoy actually uses CopyOfmod1 instead of mod1
// this tests for homonym classes
import pack.CopyOfmod1;

void func() {
	Mod1Class foo1;
}


void func2(int a) {
	Mod1Class foo2;
}
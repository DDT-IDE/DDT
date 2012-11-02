/*** Content import test                                              ***/

import pack.mod1;
import pack.mod2, pack.sample, pack.subpack.mod3; 

SampleClass mod1class;

void func() {
	SampleClass.foo++;

	foopublicImportVar++;
	fooprivateImportVar++;  // fail

	pack2.foopublic.foopublicImportVar++;
	pack2.fooprivate.fooprivateImportVar++;  // fail
}

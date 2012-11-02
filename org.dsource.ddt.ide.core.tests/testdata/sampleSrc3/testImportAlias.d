/*** Aliasing import test                                             ***/

import mod1alias = pack.mod1;
/++/import pack.mod2, samplealias = pack.sample, pack.subpack.mod3; 
// The following is not suppported (aliasing and selective) :
//  import pack.mod2, samplealias = pack.sample : SampleClassB, sampleVarB; 
import mod2alias = pack.mod2;

alias pack.sample modref; // fail

samplealias.SampleClass mod1class;

void func() {
	samplealias.SampleClass.foo++;

	samplealias.foopublicImportVar++;
	samplealias.fooprivateImportVar++;  // fail

	samplealias.pack2.foopublic.foopublicImportVar++; // Ugly D
	samplealias.pack2.fooprivate.fooprivateImportVar++;  // fail or ugly D?
}

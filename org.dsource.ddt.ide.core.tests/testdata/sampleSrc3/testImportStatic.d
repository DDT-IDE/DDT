/*** Static import test                                              ***/

/++/static import pack.mod1;static import pack.mod2, pack.sample, pack.subpack.mod3; 
/++/import pack.mod1; import pack.mod2, pack.sample, pack.subpack.mod3; 

alias pack.sample modref;
pack.sample.SampleClass sampleclass;

void func() {
	pack.sample.SampleClass.foo++;

	pack.sample.foopublicImportVar++;
	pack.sample.fooprivateImportVar++;  // fail

	pack.sample.pack2.foopublic.foopublicImportVar++;  /// ugly D!
	pack.sample.pack2.fooprivate.fooprivateImportVar++;  // fail
}

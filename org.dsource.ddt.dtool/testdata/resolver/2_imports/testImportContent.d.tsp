Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
/*** Content import test ***/ #PROJECT(importsProject)

import pack.mod1#FIND¤【pack.mod1/】;
import pack.mod2#FIND¤【pack.mod2/】, pack.sample#FIND¤【pack.sample/】, pack.subpack.mod3#FIND¤【pack.subpack.mod3/】; 

SampleClass#FIND¤【pack.sample/SampleClass】 mod1class;

void func() {
	SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】++;

	foopublicImportVar#FIND¤【pack2.foopublic/foopublicImportVar】++;
	fooprivateImportVar#FIND¤【】+;  // fail

	pack2.foopublic.foopublicImportVar#FIND¤【pack2.foopublic/foopublicImportVar】++;
	pack2.fooprivate.fooprivateImportVar#FIND¤【】++;  // fail
}

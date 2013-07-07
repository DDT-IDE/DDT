Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@STATIC_IMPORT_TEST【
	pack.#REFSEARCH¤【mod2▪mod1▪sample▪subpack】;

	pack.sample#FIND¤【pack.sample/】;
	pack.sample.SampleClass#FIND¤【pack.sample/SampleClass】;
	pack.sample.SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】++;
	
	pack.sample.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++; // Ugly D behavior?
	pack.sample.pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】; // Ugly D 

	pack.sample.fooprivateImportVar#FIND¤【】;
	pack.sample.pack2.private_imported#FIND¤【】;
	pack.sample.pack2.private_imported.fooprivateImportVar#FIND¤【】;
】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

static import pack.mod1#FIND¤【pack.mod1/】;
static import pack.mod2#FIND¤【pack.mod2/】, pack.sample, pack.subpack.mod3#FIND¤【pack.subpack.mod3/】;

#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪pack】
	#@STATIC_IMPORT_TEST
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import pack.mod1#FIND¤【pack.mod1/】;
import pack.mod2#FIND¤【pack.mod2/】, pack.sample#FIND¤【pack.sample/】, pack.subpack.mod3#FIND¤【pack.subpack.mod3/】; 


#@__CONTEXT_START__
	pack.#REFSEARCH¤【mod1▪mod2▪sample▪subpack】;
	#REFSEARCH¤【importTest▪pack▪#@PACK_SAMPLE_CONTENTS▪mod1Var▪Mod1Class▪othervar▪Other▪Other▪OtherFoo】;
	
	SampleClass#FIND¤【pack.sample/SampleClass】;
	SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】;
	
	pack.sample.sample#FIND¤【】;
	
	foopublicImportVar#FIND¤【pack2.foopublic/foopublicImportVar】;
	pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++;
	
	fooprivateImportVar#FIND¤【】;  // fail
	pack2.private_imported#FIND¤【】++;  // fail
	pack2.private_imported.fooprivateImportVar#FIND¤【】++;  // fail
	
	#@STATIC_IMPORT_TEST
#@__CONTEXT_END__


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import pack.#@SPACE_Opt#FINDFAIL#REFSEARCH¤【mod1▪mod2▪mod3▪sample▪sample2▪sample3▪subpack】#@SPACE_Opt2;
import#@SPACE_Opt#FINDFAIL#REFSEARCH¤【pack▪pack2】#@SPACE_Opt2;

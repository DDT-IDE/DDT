Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import mod1alias = pack.mod1#FIND¤【pack.mod1/】;
import mod2alias = pack.mod2#FIND¤【pack.mod2/】;
import pack.mod2, sampleAlias = pack.sample#FIND¤【pack.sample/】, pack.subpack.mod3; 


#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪mod1alias▪mod2alias▪sampleAlias▪pack】
	pack.#REFSEARCH¤【mod2】;
	pack.sample#FIND¤【】;
	
	sampleAlias#FIND¤【importAliasTest/sampleAlias】;
	sampleAlias.SampleClass#FIND¤【pack.sample/SampleClass】;
	sampleAlias.SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】++;

	sampleAlias.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++;
	sampleAlias.pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++; // Ugly D
	
	sampleAlias.fooprivateImportVar#FIND¤【】++;  // fail
	sampleAlias.pack2.private_imported#FIND¤【】++;  // fail
	sampleAlias.pack2.private_imported.fooprivateImportVar#FIND¤【】++;  // fail
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import sampleAlias3 =#@SPACE_Opt#FINDFAIL#REFSEARCH¤【pack▪pack2】#@SPACE_Opt2;
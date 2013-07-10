Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import mod1alias = pack.mod1#FIND¤【pack.mod1/】;
import mod2alias = pack.mod2#FIND¤【pack.mod2/】;
import pack.mod2, sampleAlias = pack.sample#FIND¤【pack.sample/】; 


#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪mod1alias▪mod2alias▪#@PACK_MOD2_MBRS▪pack▪sampleAlias】
	pack.#REFSEARCH¤【mod2】;
	pack.sample#FIND¤【】;
	
	sampleAlias#FIND¤【importTest/sampleAlias】;
	sampleAlias.#REFSEARCH¤【#@PACK_SAMPLE_MBRS】;
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

import sampleAlias3 =#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES】#@SPACE_Opt2;
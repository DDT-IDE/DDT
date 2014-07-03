Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import mod1alias = pack.mod1;
import mod2alias =/*syntax error*/;
import pack.mod2, sampleAlias = pack.sample; 


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
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ resolving as pick location
module importTest; #PROJECT(importsProject)

import modalias = modA#FIND¤【modA/】#REFSEARCH¤【modA/】;
import pack.mod2, modalias = modA#FIND¤【modA/】#REFSEARCH¤【modA/】;

import modalias = pack.mod1#FIND¤【pack.mod1/】#REFSEARCH¤【pack.mod1/】;
import modalias = pack.sample#FIND¤【pack.sample/】#REFSEARCH¤【pack.sample/▪pack.sample2/▪pack.sample3/】;
import modalias = pack_NONEXISTANT.#FINDMISSING#REFSEARCH¤【】;

import modalias = pack.subpack.mod3#FIND¤【pack.subpack.mod3/】#REFSEARCH¤【pack.subpack.mod3/】;
import modalias = pack_NONEXISTANT.subpack.#FINDMISSING#REFSEARCH¤【】;
import modalias = pack.subpack_NONEXISTANT.#FINDMISSING#REFSEARCH¤【】;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest;
import modalias#FINDFAIL#REFSEARCH¤【modalias】 = modA;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax errors
module importTest; #PROJECT(importsProject)

import sampleAlias =#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES】#@SPACE_Opt2;
import sampleAlias = pack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_PACK_MODULES】#@SPACE_Opt2;
import sampleAlias = pack.subpack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【pack.subpack.mod3/▪pack.subpack.mod4/】#@SPACE_Opt2;
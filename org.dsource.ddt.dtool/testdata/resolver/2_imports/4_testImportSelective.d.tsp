Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests; #PROJECT(importsProject)

import pack.mod2, pack.sample#FIND¤【pack.sample/】 : 
 	SampleClass#FIND¤【pack.sample/SampleClass】, 
 	sampleVar#FIND¤【pack.sample/sampleVar】, 
 	SampleClassBAlias = SampleClassB#FIND¤【pack.sample/SampleClassB】, 
 	sampleVarBAlias = sampleVarB#FIND¤【pack.sample/sampleVarB】,
 	MissingAlias = noSuchElement#FIND¤【】,
 	;
 	
#@__CONTEXT_START__
	pack.sample#FIND¤【】; // No FQN with selective
	pack.#REFSEARCH¤【mod2】;
	
	#REFSEARCH¤【importTests▪pack▪mod2▪SampleClass▪sampleVar▪SampleClassBAlias▪sampleVarBAlias】
	
	SampleClass#FIND¤【pack.sample/SampleClass】;
	SampleVar#FIND¤【pack.sample/sampleVar】;
	
	SampleClassBAlias#FIND¤【SampleClassBAlias】;
	SampleClassB#FIND¤【】; // fail
	
	sampleVarBAlias#FIND¤【sampleVarBAlias】 _dummy;
	sampleVarB#FIND¤【】 _dummy;
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests;  // Allow re-imports to be visible for selection:
import pack.sample : foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】;
#REFSEARCH¤【importTests▪foopublicImportVar】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests; #PROJECT(importsProject)

import sampleAlias = pack.sample : SampleClass, sampleVarBAlias = sampleVarB;

#REFSEARCH¤【importSelective▪sampleAlias▪SampleClass▪sampleVarBAlias】
pack.#FIND¤【】#REFSEARCH¤【】;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

import pack.sample :#@SPACE_Opt#FINDFAIL#REFSEARCH¤【#@PACK_SAMPLE_CONTENTS】#@SPACE_Opt2,
 	sampleErrorAlias =#@SPACE_Opt#FINDFAIL#REFSEARCH¤【#@PACK_SAMPLE_CONTENTS】#@SPACE_Opt2;
 	

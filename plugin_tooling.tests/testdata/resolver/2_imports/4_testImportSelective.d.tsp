Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests; #PROJECT(importsProject)

import pack.mod3, pack.sample : 
 	SampleClass, 
 	sampleVar,
 	noSuchElement, 
 	/*SYNTAX-ERROR MISSING*/,
 	SampleClassBAlias = SampleClassB, 
 	sampleVarBAlias = /*SYNTAX-ERROR MISSING*/,
 	MissingAlias = noSuchElement,
 	;
 	
import pack.nonExistant : Foo1, Foo2;
 	
#@__CONTEXT_START__
	pack.sample#FIND¤【】; // No FQN with selective
	pack.#REFSEARCH¤【mod3】;
	
	#REFSEARCH¤【importTests▪pack▪#@PACK_MOD3_MBRS▪SampleClass▪sampleVar▪SampleClassBAlias▪sampleVarBAlias▪MissingAlias】
	
	SampleClass#FIND¤【pack.sample/SampleClass】;
	sampleVar#FIND¤【pack.sample/sampleVar】;
	
	SampleClassBAlias#FIND¤【SampleClassBAlias】;
	SampleClassB#FIND¤【】; // fail
	
	sampleVarBAlias#FIND¤【sampleVarBAlias】 _dummy;
	sampleVarB#FIND¤【】 _dummy;
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests;  #PROJECT(importsProject) 
// Allow member-imports to be visible for selection:
import pack.sample : foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】;
#REFSEARCH¤【importTests▪foopublicImportVar】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTests; #PROJECT(importsProject)

import sampleAlias = pack.sample : SampleClass, sampleVarBAlias = sampleVarB;
// According to DMD (2.063) sampleAlias is found, but this is not implemented. s
// It's a b it of a stupid idea anyways to combine aliasing imports with selective imports
#REFSEARCH¤【importTests▪#comment¤【sampleAlias▪】SampleClass▪sampleVarBAlias】 
pack#FIND¤【】.#REFSEARCH¤【】;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ resolving as pick location
#PROJECT(importsProject)

import pack.mod3, pack.sample#FIND¤【pack.sample/】 : 
 	SampleClass#FIND¤【pack.sample/SampleClass】, 
 	sampleVar#FIND¤【pack.sample/sampleVar】,
 	noSuchElement#FIND¤【】,
 	SampleClassBAlias = SampleClassB#FIND¤【pack.sample/SampleClassB】, 
 	sampleVarBAlias = sampleVarB#FIND¤【pack.sample/sampleVarB】,
 	MissingAlias = noSuchElement#FIND¤【】,
 	;
 	
import pack.nonExistant#FIND¤【】 : Foo1#FIND¤【】, Foo2#FIND¤【】;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax errors
#PROJECT(importsProject)

import pack.sample :#@SPACE_Opt#FINDMISSING#REFSEARCH¤【pack▪#@PACK_SAMPLE_MBRS▪pack2▪#@PACK2_PUBLIC_IMPORTED_MBRS】#@SPACE_Opt2,
 	sampleErrorAlias =#@SPACE_Opt#FINDMISSING#REFSEARCH¤【pack▪#@PACK_SAMPLE_MBRS▪pack2▪#@PACK2_PUBLIC_IMPORTED_MBRS】#@SPACE_Opt2;
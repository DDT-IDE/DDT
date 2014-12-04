Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@STATIC_IMPORT_TEST【
	modA.#REFSEARCH¤【#@MODA_MBRS】;
	
	pack.#REFSEARCH¤【mod2▪mod1▪sample▪subpack▪modNonExistant▪modSyntaxErrors】;
	
	pack.sample#FIND¤【pack.sample/】;
	pack.sample.#REFSEARCH¤【#@PACK_SAMPLE_MBRS】;
	pack.sample.SampleClass#FIND¤【pack.sample/SampleClass】;
	pack.sample.SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】++;
	
	pack.sample.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++; // Ugly D behavior?
	pack.sample.pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】; // Ugly D 

	pack.sample.fooprivateImportVar#FIND¤【】;
	pack.sample.pack2.private_imported#FIND¤【】;
	pack.sample.pack2.private_imported.fooprivateImportVar#FIND¤【】;
	
	pack.subpack#FIND¤【pack.subpack/】; // test import package
	pack.subpack.#REFSEARCH¤【PackSubpackPackage▪mod3】; 
	pack.subpack#REFSEARCH¤【pack.subpack/】;
】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

static import modA;
static import pack.mod1;
static import /* syntax error */;
static import pack.mod2, pack.sample, pack.subpack.mod3;
static import pack.subpack;

import pack.modNonExistant, pack.modSyntaxErrors;

#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪pack▪modA】
	#@STATIC_IMPORT_TEST
#@__CONTEXT_END__

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Non-existant
// Non-existant packages:
import pack_NonExistant#FIND¤【】#REFSEARCH¤【►4】.mod#FIND¤【】;
import pack_NonExistant.subpack#FIND¤【】#REFSEARCH¤【►4】.mod#FIND¤【】;
import pack.subpack_NonExistant#FIND¤【】#REFSEARCH¤【►4】.mod#FIND¤【】;
// Non-existant module:
import pack.NonExistant#FIND¤【】#REFSEARCH¤【】;

pack.#REFSEARCH¤【subpack_NonExistant▪NonExistant】; // Finding no-results would also be acceptable behavior

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Name mismatch
module importTest; #PROJECT(importsProject)
// Test name mismatch - as pick location:
import pack3.#REFSEARCH¤【pack3.fullNameMismatch/▪pack3.moduleNameMismatch/▪pack3.packageNameMismatch/▪pack3.other/】;
 import pack3.moduleNameMismatch#FIND¤【pack3.moduleNameMismatch/】#REFSEARCH¤【pack3.moduleNameMismatch/】;
import pack3.packageNameMismatch#FIND¤【pack3.packageNameMismatch/】#REFSEARCH¤【pack3.packageNameMismatch/】;
   import pack3.fullNameMismatch#FIND¤【pack3.fullNameMismatch/】#REFSEARCH¤【pack3.fullNameMismatch/】;

// Test name mismatch - as import content:
pack3.#REFSEARCH¤【pack3.fullNameMismatch/▪pack3.moduleNameMismatch/▪pack3.packageNameMismatch/】;
 pack3.moduleNameMismatch#FIND¤【pack3.moduleNameMismatch/】.#REFSEARCH¤【moduleNameMismatch_var】;
pack3.packageNameMismatch#FIND¤【pack3.packageNameMismatch/】.#REFSEARCH¤【packageNameMismatch_var】;
   pack3.fullNameMismatch#FIND¤【pack3.fullNameMismatch/】.#REFSEARCH¤【fullNameMismatch_var】;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import modA;
import pack.mod1;
import /* syntax error */;
import pack.mod2, pack.sample, pack.subpack.mod3; 
import pack.subpack;


#@__CONTEXT_START__
	pack.#REFSEARCH¤【mod1▪mod2▪sample▪subpack▪modNonExistant▪modSyntaxErrors】;
	#REFSEARCH¤【importTest▪modA▪#@MODA_MBRS▪pack▪#@PACK_MOD1_MBRS▪
		#@PACK_MOD2_MBRS▪#@PACK_SAMPLE_MBRS▪#@SUBPACK_MOD3_MBRS▪PackSubpackPackage】;
	
	SampleClass#FIND¤【pack.sample/SampleClass】;
	SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】;
	
	pack.sample.sample#FIND¤【】;
	
	foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】;
	pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++;
	
	fooprivateImportVar#FIND¤【】;  // fail
	pack2.private_imported#FIND¤【】++;  // fail
	pack2.private_imported.fooprivateImportVar#FIND¤【】++;  // fail
	
	#@STATIC_IMPORT_TEST
	
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ resolving as pick location
module importTest; #PROJECT(importsProject)

import modA#FIND¤【modA/】#REFSEARCH¤【modA/】;

import pack.mod1#FIND¤【pack.mod1/】#REFSEARCH¤【pack.mod1/】;
import pack.s#FIND¤【】#REFSEARCH¤【pack.sample/▪pack.sample2/▪pack.sample3/▪pack.subpack/▪pack.subpack.mod3/▪pack.subpack.mod4/】;
import pack.subpack.mod3#FIND¤【pack.subpack.mod3/】#REFSEARCH¤【pack.subpack.mod3/】;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax errors
module importTest; #PROJECT(importsProject)

import #FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES】#@SPACE_Opt2;
import #REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES►5】pack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_PACK_MODULES►0】#@SPACE_Opt2;
import #REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES►6】pack .#@SPACE_Opt2#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_PACK_MODULES►0】;
import #REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES►13】pack.subpack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【pack.subpack.mod3/▪pack.subpack.mod4/】#@SPACE_Opt2;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax errors - keywords that could be identifiers
module importTest; #PROJECT(importsProject2)
import in#FINDFAIL#REFSEARCH¤【intromodule/▪intropack.xpto/】;
import #FINDMISSING#REFSEARCH¤【intromodule/▪intropack.xpto/▪other/►2】in;
import int#FIND¤【/int】#REFSEARCH¤【intromodule/▪intropack.xpto/】;
import #FINDMISSING#REFSEARCH¤【intromodule/▪intropack.xpto/▪other/►3】int;

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@STATIC_IMPORT_TEST【
	modA.#REFSEARCH¤【#@MODA_MBRS】;
	
	pack.#REFSEARCH¤【mod2▪mod1▪sample▪subpack】;
	
	pack.sample#FIND¤【pack.sample/】;
	pack.sample.#REFSEARCH¤【#@PACK_SAMPLE_MBRS】;
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

static import modA;
static import pack.mod1;
static import /* syntax error */;
static import pack.mod2, pack.sample, pack.subpack.mod3;

#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪pack▪modA】
	#@STATIC_IMPORT_TEST
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import modA;
import pack.mod1;
import /* syntax error */;
import pack.mod2, pack.sample, pack.subpack.mod3; 


#@__CONTEXT_START__
	pack.#REFSEARCH¤【mod1▪mod2▪sample▪subpack】;
	#REFSEARCH¤【importTest▪modA▪#@MODA_MBRS▪pack▪#@PACK_MOD1_MBRS▪
		#@PACK_MOD2_MBRS▪#@PACK_SAMPLE_MBRS▪#@SUBPACK_MOD3_MBRS】;
	
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
import pack.s#FIND¤【】#REFSEARCH¤【pack.sample/▪pack.sample2/▪pack.sample3/▪pack.subpack.mod3/▪pack.subpack.mod4/】;
import pack_NONEXISTANT.#FINDMISSING#REFSEARCH¤【】;

import pack.subpack.mod3#FIND¤【pack.subpack.mod3/】#REFSEARCH¤【pack.subpack.mod3/】;
import pack_NONEXISTANT.subpack.#FINDMISSING#REFSEARCH¤【】;
import pack.subpack_NONEXISTANT.#FINDMISSING#REFSEARCH¤【】;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax errors
module importTest; #PROJECT(importsProject)

import#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_ALL_MODULES】#@SPACE_Opt2;
import pack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【#@IMPORTSPROJ_PACK_MODULES】#@SPACE_Opt2;
import pack.subpack.#@SPACE_Opt#FINDMISSING#REFSEARCH¤【pack.subpack.mod3/▪pack.subpack.mod4/】#@SPACE_Opt2;

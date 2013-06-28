Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@STATIC_Opt《static #STATIC_IMPORT(flag)●/**/》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importStatic; #PROJECT(importsProject) // Static import test

#@STATIC_Opt import pack.mod1#FIND¤【pack.mod1/】;
#@STATIC_Opt import pack.mod2#FIND¤【pack.mod2/】, pack.sample#FIND¤【pack.sample/】, 
	pack.subpack.mod3#FIND¤【pack.subpack.mod3/】; 


pack.sample#FIND¤【pack.sample/】;
pack.sample.SampleClass#FIND¤【pack.sample/SampleClass】;


void func() {
	pack.sample.SampleClass.foo#FIND¤【pack.sample/SampleClass.foo】++;
	
	pack.sample.foopublicImportVar#FIND¤【pack2.foopublic/foopublicImportVar】++;
	pack.sample.fooprivateImportVar#FIND¤【】++;
	
	pack.sample.pack2.foopublic.foopublicImportVar#FIND¤【pack2.foopublic/foopublicImportVar】++;
	pack.sample.pack2.fooprivate.fooprivateImportVar#FIND¤【】;
}
#?STATIC_IMPORT【 #REFSEARCH¤【importStatic▪pack▪func()】 】
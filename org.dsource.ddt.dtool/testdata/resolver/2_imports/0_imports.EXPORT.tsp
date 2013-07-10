Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@MODA_MBRS【modAVar▪ModAClass】
#@PACK_MOD1_MBRS【mod1Var▪Mod1Class】
#@PACK_MOD2_MBRS【othervar▪Other▪OtherFoo】
#@PACK_MOD3_MBRS【othervar▪Other】
// It's a bit crap that pack▪pack2 are included too (ugly behavior)
#@PACK_SAMPLE_MBRS【sampleVar▪sampleVarB▪SampleClass▪SampleClassB▪pack▪pack2▪#@PACK2_PUBLIC_IMPORTED_MBRS】  
#@SUBPACK_MOD3_MBRS【mod3var▪Mod3Class▪Mod3Template】
#@PACK2_PUBLIC_IMPORTED_MBRS【foopublicImportVar0▪foopublicImportVar▪foopublicImportVar2】

#@IMPORTSPROJ_PACK_MODULES【pack.mod1▪pack.mod2▪pack.mod3▪
pack.sample▪pack.sample2▪pack.sample3▪
pack.subpack.mod3▪pack.subpack.mod4】

#@IMPORTSPROJ_ALL_MODULES【
modA▪
xpto▪
#@IMPORTSPROJ_PACK_MODULES▪
pack2.fullNameMismatch▪pack2.moduleNameMismatch▪pack2.packageNameMismatch▪
pack2.private_imported▪pack2.xpto_imported▪pack2.public_imported▪
】
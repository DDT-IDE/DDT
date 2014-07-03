▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module xpto; #PROJECT(xpto@importsProject)

int xpto_test_var;

#@__CONTEXT_START__
	#@《public ●》 import pack2.xpto_imported;

	xpto_imported_var#FIND¤【pack2.xpto_imported/xpto_imported_var】;
	#REFSEARCH¤【xpto▪xpto_test_var▪pack2▪xpto_imported_var】
	
#@__CONTEXT_END__

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module _dummy; #PROJECT(importsProject)

#@__CONTEXT_START__
	import xpto#FIND¤【xpto/】; // Test self-test
	
	xpto_imported_var#FIND¤【pack2.xpto_imported/xpto_imported_var】;
	#REFSEARCH¤【xpto▪xpto_var▪pack2▪xpto_imported_var】
	
#@__CONTEXT_END__
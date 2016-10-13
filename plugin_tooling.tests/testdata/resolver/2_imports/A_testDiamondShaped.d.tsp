▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import pack.sample; // side1
import pack.sample2; // side2
import pack.sample3; //side3

#@__CONTEXT_START__

/* Note: this configuration must not be broken for the test to remain valid:

         testDiamondShaped 
       /	      |         \
pack.sample  pack.sample2  pack.sample3
       \          |         /                   
       pack2.public_imported 
*/

  // top of diamond
  pack2.public_imported.foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++;
  foopublicImportVar#FIND¤【pack2.public_imported/foopublicImportVar】++; 
  #REFSEARCH¤【importTest▪pack▪#@PACK_SAMPLE_MBRS▪pack2▪#@PACK2_PUBLIC_IMPORTED_MBRS】
  
#@__CONTEXT_END__


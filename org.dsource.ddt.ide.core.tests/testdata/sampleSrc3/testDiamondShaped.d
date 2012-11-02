/*** Diamond shaped and U shaped test                 ***/

import pack.sample; // side1
import pack.sample2; // side2
import pack.sample3; // U side

void func() {
  // top of diamond
  foopublicImportVar++; 
  pack2.foopublic.foopublicImportVar++;
  
  // top of U
  foopublicImport2Var++; 
  pack2.foopublic2.foopublicImport2Var++;
}
/* Make sure access to pack2.* is not broken with these configurations:

    pack2.foopublic            pack2.foopublic2  
   /		       \                 |
pack.sample    pack.sample2     pack.sample3
    \             /                /                   
   testDiamondShaped
   
*/
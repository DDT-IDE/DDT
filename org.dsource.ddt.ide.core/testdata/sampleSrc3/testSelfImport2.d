/*** Self ref test                                              ***/
module testSelfImport2;

import testSelfImport2; // This one has an import to self

int mysample;

alias testSelfImport2 selfref;

void func() {
	testSelfImport2.mysample++;
}
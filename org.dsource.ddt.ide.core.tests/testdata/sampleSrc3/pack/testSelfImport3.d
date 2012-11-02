/*** Self ref test                                              ***/
module pack.testSelfImport3;

import pack.testSelfImport3;

int mysample;

alias pack.testSelfImport3 selfref;

void func() {
	pack.testSelfImport3.mysample++;
}
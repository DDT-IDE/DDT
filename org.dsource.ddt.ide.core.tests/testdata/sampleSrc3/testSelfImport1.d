/*** Self ref test                                              ***/
module testSelfImport1;

int mysample;

alias testSelfImport1 selfref;

void func() {
	mysample++;
	testSelfImport1.mysample++;
}
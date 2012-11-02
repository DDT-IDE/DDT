/** Test entities inside DefUnit containters **/

int dummy = f1+f2+f3t+f3f+f4+f5+f6;

/*** CONDITIONALS ***/

version = Yes;
version(Yes) {
	int f1;
}

debug = Yes;
debug(Yes) {
	int f2;
}

static if(true) {
	int f3t;
} else  {
	int f3f;
}

/*** OTHER (ANONYMOUS DEFUNITS) ***/
  enum { f4, f5 }

template Tpl() {
	int f6;
}
  mixin Tpl!();

